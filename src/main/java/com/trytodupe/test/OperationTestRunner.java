package com.trytodupe.test;

import com.google.gson.JsonObject;
import com.trytodupe.Main;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.avltree.composite.AVLTreeInitCompositeOperation;
import com.trytodupe.operation.binarysearchtree.composite.BinarySearchTreeInitCompositeOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeDeleteUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;
import com.trytodupe.operation.binarytree.composite.BinaryTreeInitCompositeOperation;
import com.trytodupe.operation.huffmantree.composite.HuffmanTreeInitCompositeOperation;
import com.trytodupe.serialization.GsonProvider;
import com.trytodupe.serialization.ISerializable;

import java.util.ArrayList;
import java.util.List;

public class OperationTestRunner {

	public static void runTestSuite (Class<?> clazz, List<? extends UserOperation<?>> operations) {

		List<UserOperation<?>> deserializedOperations = new ArrayList<>();

		System.out.println("=== Running test suite for " + clazz.getSimpleName() + " ===\n");

		// forward testing
		for (UserOperation<?> op : operations) {
			System.out.println("Executing: " + op.getDescription());

			// serialize
			JsonObject json = op.toJson(GsonProvider.get());
			System.out.println("Serialized JSON: " + json);

			// deserialize
			ISerializable deserialized = ISerializable.fromJson(GsonProvider.get(), json);
			System.out.println("Deserialized type: " + deserialized.getClass().getSimpleName());


			// execute
			if (deserialized instanceof UserOperation<?>) {
				deserializedOperations.add((UserOperation<?>) deserialized);
				((UserOperation<?>) deserialized).execute();
			}

//            op.execute();

			System.out.println();
		}

		// backward testing
		for (int i = deserializedOperations.size() - 1; i >= 0; i--) {
			UserOperation<?> op = deserializedOperations.get(i);
			System.out.println("Undoing: " + op.getDescription());
			op.undo();
			System.out.println();
		}

		System.out.println("=== Test suite finished ===\n");
	}

    public static void runTests() {
        // ===== Binary Tree Test =====
        testBinaryTree();

        // ===== Binary Search Tree Test =====
        testBinarySearchTree();

        // ===== Huffman Tree Test =====
        testHuffmanTree();

        // ===== AVL Tree Test =====
        testAVLTree();
    }

    /**
     * Test unbalanced binary tree with array-based level-order construction.
     */
    private static void testBinaryTree() {
        System.out.println("\n===== Binary Tree Test =====");
        List<UserOperation<BinaryTreeStructure<Integer>>> testOps = new ArrayList<>();
        BinaryTreeStructure<Integer> btree = Main.getDataStructure(BinaryTreeStructure.class);

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
        BinarySearchTreeStructure<Integer> bst = Main.getDataStructure(BinarySearchTreeStructure.class);

        Integer[] bstValues = {10, 20, 30, 40, 50, 25};
        testOps.add(new BinarySearchTreeInitCompositeOperation<>(bst, bstValues));
        OperationTestRunner.runTestSuite(BinarySearchTreeStructure.class, testOps);

        // Test 2: Individual operations
        System.out.println("\n--- BST Individual Operations ---");
        bst = Main.getDataStructure(BinarySearchTreeStructure.class);

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

        HuffmanTreeStructure<Character> huffmanTree = Main.getDataStructure(HuffmanTreeStructure.class);
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

        AVLTreeStructure<Integer> avlTree = Main.getDataStructure(AVLTreeStructure.class);
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
