package com.trytodupe;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.gui.HighlightInfo;
import com.trytodupe.gui.HighlightVisitor;
import com.trytodupe.gui.UIPanelManager;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.avltree.composite.AVLTreeInitCompositeOperation;
import com.trytodupe.operation.binarytree.composite.BinaryTreeInitCompositeOperation;
import com.trytodupe.operation.binarysearchtree.composite.BinarySearchTreeInitCompositeOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeDeleteUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;
import com.trytodupe.operation.huffmantree.composite.HuffmanTreeInitCompositeOperation;
import com.trytodupe.test.OperationTestRunner;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;

import java.util.*;

public class Main extends Application {

    public static final boolean DEBUG = true;

    private static final Map<Class<? extends DataStructure>, DataStructure> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put(ArrayStructure.class, new ArrayStructure());
        REGISTRY.put(StackStructure.class, new StackStructure());
        REGISTRY.put(BinaryTreeStructure.class, new BinaryTreeStructure<>());
        REGISTRY.put(BinarySearchTreeStructure.class, new BinarySearchTreeStructure<>());
        REGISTRY.put(HuffmanTreeStructure.class, new HuffmanTreeStructure<>());
        REGISTRY.put(AVLTreeStructure.class, new AVLTreeStructure<>());
    }

    @SuppressWarnings("unchecked")
    public static <T extends DataStructure> T getDataStructure(Class<T> clazz) {
        return (T) REGISTRY.get(clazz);
    }

    // ImGui UI fields
    private List<AtomicOperation<?>> animationTimeline = new ArrayList<>();
    private int currentStep = 0;
    private HighlightVisitor highlightVisitor = new HighlightVisitor();
    private boolean isPlaying = false;
    private float playbackSpeed = 1.0f;
    private UIPanelManager uiPanelManager;
    private boolean initialized = false;

    // Operation history stack
    private List<String> operationStack = new ArrayList<>();
    private UserOperation<?> lastExecutedOperation = null;

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Data Structure Visualizer");
        config.setWidth(1600);
        config.setHeight(900);
    }

    @Override
    public void process() {
        // Initialize on first run
        if (!initialized) {
            uiPanelManager = new UIPanelManager(this);
            initialized = true;
        }

        // Render ImGui windows
        uiPanelManager.render();

        // Update animation
        if (isPlaying && !animationTimeline.isEmpty()) {
            if (currentStep < animationTimeline.size()) {
                currentStep++;
            }
        }

        // Update highlight info
        if (currentStep > 0 && currentStep <= animationTimeline.size()) {
            highlightVisitor.reset();
            animationTimeline.get(currentStep - 1).accept(highlightVisitor);
        }
    }

    // ... Getter and Setter methods ...
    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int step) {
        this.currentStep = Math.max(0, Math.min(step, animationTimeline.size()));
    }

    public int getAnimationTimelineSize() {
        return animationTimeline.size();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    public float getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed(float speed) {
        this.playbackSpeed = speed;
    }

    public HighlightInfo getCurrentHighlightInfo() {
        return highlightVisitor.getHighlightInfo();
    }

    public List<String> getOperationStack() {
        return operationStack;
    }

    public void resetAnimation() {
        currentStep = 0;
        isPlaying = false;
        highlightVisitor.reset();
    }

    /**
     * Step forward by executing the next atomic operation
     */
    public void stepForward() {
        if (currentStep < animationTimeline.size()) {
            // Execute the next atomic operation
            AtomicOperation<?> operation = animationTimeline.get(currentStep);
            operation.accept(highlightVisitor);
            currentStep++;
        }
    }

    /**
     * Step backward by undoing the previous atomic operation
     */
    public void stepBackward() {
        if (currentStep > 0) {
            currentStep--;
            // Undo the current atomic operation
            AtomicOperation<?> operation = animationTimeline.get(currentStep);
            operation.accept(highlightVisitor);
        }
    }

    /**
     * Jump to a specific step by executing or undoing operations as needed
     */
    public void jumpToStep(int targetStep) {
        targetStep = Math.max(0, Math.min(targetStep, animationTimeline.size()));

        if (targetStep > currentStep) {
            // Step forward
            while (currentStep < targetStep) {
                stepForward();
            }
        } else if (targetStep < currentStep) {
            // Step backward
            while (currentStep > targetStep) {
                stepBackward();
            }
        }
    }

    /**
     * Set animation timeline from a UserOperation
     */
    @SuppressWarnings("unchecked")
    public void setAnimationTimeline(UserOperation<?> userOperation) {
        // Build operations first if not already built
        userOperation.execute();

        // Extract atomic operations from user operation
        animationTimeline.clear();
        animationTimeline.addAll((List<AtomicOperation<?>>) (List<?>) userOperation.getAtomicOperations());
        currentStep = 0;
        isPlaying = false;
        highlightVisitor.reset();

        // Record operation in the stack
        lastExecutedOperation = userOperation;
        operationStack.add(userOperation.getDescription());
    }

    public static void main(String[] args) {
        // ===== Binary Tree Test =====
//        testBinaryTree();

        // ===== Binary Search Tree Test =====
//        testBinarySearchTree();

        // ===== Huffman Tree Test =====
//        testHuffmanTree();

        // ===== AVL Tree Test =====
        testAVLTree();

        // Uncomment to launch GUI
        launch(new Main());
    }

    /**
     * Test unbalanced binary tree with array-based level-order construction.
     */
    private static void testBinaryTree() {
        System.out.println("\n===== Binary Tree Test =====");
        List<UserOperation<BinaryTreeStructure<Integer>>> testOps = new ArrayList<>();
        BinaryTreeStructure<Integer> btree = getDataStructure(BinaryTreeStructure.class);

        Integer[] treeValues = {1, 2, 3, 4, 5, null, 7};
        testOps.add(new BinaryTreeInitCompositeOperation<>(btree, treeValues));

        OperationTestRunner.runTestSuite(BinaryTreeStructure.class, testOps);
    }

    /**
     * Test binary search tree with insertion and deletion operations.
     */
    private static void testBinarySearchTree() {
        System.out.println("\n===== Binary Search Tree Test =====");

        // Test 1: Initialization with multiple values
        List<UserOperation<BinarySearchTreeStructure<Integer>>> testOps = new ArrayList<>();
        BinarySearchTreeStructure<Integer> bst = getDataStructure(BinarySearchTreeStructure.class);

        Integer[] bstValues = {10, 20, 30, 40, 50, 25};
        testOps.add(new BinarySearchTreeInitCompositeOperation<>(bst, bstValues));
        OperationTestRunner.runTestSuite(BinarySearchTreeStructure.class, testOps);

        // Test 2: Individual operations
        System.out.println("\n--- BST Individual Operations ---");
        bst = getDataStructure(BinarySearchTreeStructure.class);

        // Insert values individually and demonstrate operations
        List<BinarySearchTreeInsertUserOperation<Integer>> insertOps = new ArrayList<>();
        for (Integer value : bstValues) {
            BinarySearchTreeInsertUserOperation<Integer> insertOp = new BinarySearchTreeInsertUserOperation<>(bst, value);
            insertOp.execute();
            insertOps.add(insertOp);
        }
        System.out.println("After insertions:");
        bst.printValue();

        // Test deletion
        if (!insertOps.isEmpty()) {
            String deleteUUID = insertOps.get(0).getUUID();
            UserOperation<BinarySearchTreeStructure<Integer>> deleteOp =
                    new BinarySearchTreeDeleteUserOperation<>(bst, deleteUUID);

            deleteOp.execute();
            System.out.println("After deletion:");
            bst.printValue();

            deleteOp.undo();
            System.out.println("After undo:");
            bst.printValue();
        }
    }

    /**
     * Test Huffman tree construction from a string.
     */
    private static void testHuffmanTree() {
        System.out.println("\n===== Huffman Tree Test =====");

        HuffmanTreeStructure<Character> huffmanTree = getDataStructure(HuffmanTreeStructure.class);
        String testString = "abcaba";

        HuffmanTreeInitCompositeOperation<Character> huffmanOp = new HuffmanTreeInitCompositeOperation<>(huffmanTree, testString);
        huffmanOp.execute();

        System.out.println("Huffman roots after initialization: " + huffmanTree.getRoots().size());
        if (!huffmanTree.getRoots().isEmpty()) {
            BinaryTreeNode.printTree(huffmanTree.getRoots().get(0));
        }

        huffmanOp.undo();
        System.out.println("Huffman roots after undo: " + huffmanTree.getRoots().size());
    }

    /**
     * Test AVL tree with automatic balancing.
     */
    private static void testAVLTree() {
        System.out.println("\n===== AVL Tree Test =====");

        AVLTreeStructure<Integer> avlTree = getDataStructure(AVLTreeStructure.class);
        Integer[] avlValues = {10, 20, 30, 40, 50, 25};

        AVLTreeInitCompositeOperation<Integer> avlInitOp = new AVLTreeInitCompositeOperation<>(avlTree, avlValues);
        avlInitOp.execute();

        System.out.println("AVL tree after initialization and balancing:");
        avlTree.printValue();

        avlInitOp.undo();
        System.out.println("AVL tree after undo:");
        avlTree.printValue();
    }
}
