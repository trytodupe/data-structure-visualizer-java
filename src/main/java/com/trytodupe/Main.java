package com.trytodupe;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.UserOperation;
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

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Dear ImGui is Awesome!");
    }

    @Override
    public void process() {
        ImGui.text("Hello, World!");
    }

    @SuppressWarnings("unchecked")
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
        // launch(new Main());
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
