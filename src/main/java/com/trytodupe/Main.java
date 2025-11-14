package com.trytodupe;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeNode;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.user.ArrayDeleteUserOperation;
import com.trytodupe.operation.array.user.ArrayInitUserOperation;
import com.trytodupe.operation.array.user.ArrayInsertUserOperation;
import com.trytodupe.operation.binarysearchtree.composite.BinarySearchTreeInitCompositeOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeDeleteUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;
import com.trytodupe.operation.binarytree.composite.BinaryTreeInitCompositeOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeAddAtomicOperation;
import com.trytodupe.operation.huffmantree.composite.HuffmanTreeInitCompositeOperation;
import com.trytodupe.operation.stack.user.StackInitUserOperation;
import com.trytodupe.operation.stack.user.StackPopUserOperation;
import com.trytodupe.operation.stack.user.StackPushUserOperation;
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
        REGISTRY.put(BinaryTreeStructure.class, new BinaryTreeStructure<Integer>());
        REGISTRY.put(BinarySearchTreeStructure.class, new BinarySearchTreeStructure<Integer>());
        REGISTRY.put(HuffmanTreeStructure.class, new HuffmanTreeStructure<Character>());
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

		List<UserOperation<ArrayStructure>> testArrayOps = new ArrayList<>();
		ArrayStructure array = getDataStructure(ArrayStructure.class);

		testArrayOps.add(new ArrayInitUserOperation(array, new int[]{1, 2, 3}));
		testArrayOps.add(new ArrayInsertUserOperation(array, 1, 99));
		testArrayOps.add(new ArrayDeleteUserOperation(array, 2));

//		OperationTestRunner.runTestSuite(ArrayStructure.class, testArrayOps);
//-----
		List<UserOperation<StackStructure>> testStackOps = new ArrayList<>();
		StackStructure stack = getDataStructure(StackStructure.class);

        testStackOps.add(new StackInitUserOperation(stack, new int[]{1, 2, 3}));
		testStackOps.add(new StackPushUserOperation(stack, 4));
		testStackOps.add(new StackPushUserOperation(stack, 5));
		testStackOps.add(new StackPopUserOperation(stack));
		testStackOps.add(new StackPushUserOperation(stack, 6));

//		OperationTestRunner.runTestSuite(StackStructure.class, testStackOps);
//-----
        List<UserOperation<BinaryTreeStructure<Integer>>> testBTreeOps = new ArrayList<>();
        BinaryTreeStructure<Integer> btree = getDataStructure(BinaryTreeStructure.class);

        Integer[] treeValues = {1, 2, 3, 4, 5, null, 7};
        testBTreeOps.add(new BinaryTreeInitCompositeOperation(btree, treeValues));

//        OperationTestRunner.runTestSuite(BinaryTreeStructure.class, testBTreeOps);
//-----
        List<UserOperation<BinarySearchTreeStructure<Integer>>> testBSTOps = new ArrayList<>();
        BinarySearchTreeStructure<Integer> bst = getDataStructure(BinarySearchTreeStructure.class);

        Integer[] bstValues = {5, 3, 7, 2, 4, 6, 8};
        testBSTOps.add(new BinarySearchTreeInitCompositeOperation(bst, bstValues));


//        OperationTestRunner.runTestSuite(BinarySearchTreeStructure.class, testBSTOps);
//-----

        bst = getDataStructure(BinarySearchTreeStructure.class);
        List<BinarySearchTreeInsertUserOperation> testBSTInsertOps = new ArrayList<>();

//        for (Integer value : bstValues) {
//            testBSTInsertOps.add(new BinarySearchTreeInsertUserOperation(bst, value));
//            testBSTInsertOps.get(testBSTInsertOps.size() - 1).execute();
//        }

//        UserOperation<BinarySearchTreeStructure<Integer>> deleteOp =
//                new BinarySearchTreeDeleteUserOperation(bst, testBSTInsertOps.get(0).getUUID());

//        deleteOp.execute();
//        bst.printValue();
//        System.out.println("done");
//
//        deleteOp.undo();
//        bst.printValue();
//-----

        HuffmanTreeStructure<Character> huffmanTree = getDataStructure(HuffmanTreeStructure.class);

        String string = "abcaba";

        HuffmanTreeInitCompositeOperation huffmanOp = new HuffmanTreeInitCompositeOperation(huffmanTree, string);
        huffmanOp.execute();

        System.out.println(huffmanTree.getRoots());
        BinaryTreeNode.printTree(huffmanTree.getRoots().get(0));

        huffmanOp.undo();



//        launch(new Main());
    }
}
