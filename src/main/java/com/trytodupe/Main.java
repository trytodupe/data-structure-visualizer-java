package com.trytodupe;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.BinaryTreeStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.user.ArrayDeleteUserOperation;
import com.trytodupe.operation.array.user.ArrayInitUserOperation;
import com.trytodupe.operation.array.user.ArrayInsertUserOperation;
import com.trytodupe.operation.binarytree.composite.BinaryTreeInitCompositeOperation;
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

		OperationTestRunner.runTestSuite(ArrayStructure.class, testArrayOps);

		List<UserOperation<StackStructure>> testStackOps = new ArrayList<>();
		StackStructure stack = getDataStructure(StackStructure.class);

        testStackOps.add(new StackInitUserOperation(stack, new int[]{1, 2, 3}));
		testStackOps.add(new StackPushUserOperation(stack, 4));
		testStackOps.add(new StackPushUserOperation(stack, 5));
		testStackOps.add(new StackPopUserOperation(stack));
		testStackOps.add(new StackPushUserOperation(stack, 6));

		OperationTestRunner.runTestSuite(StackStructure.class, testStackOps);

        List<UserOperation<BinaryTreeStructure<Integer>>> testBTreeOps = new ArrayList<>();
        BinaryTreeStructure<Integer> btree = getDataStructure(BinaryTreeStructure.class);

        Integer[] treeValues = {1, 2, 3, 4, 5, null, 7};
        testBTreeOps.add(new BinaryTreeInitCompositeOperation(btree, treeValues));

        OperationTestRunner.runTestSuite(BinaryTreeStructure.class, testBTreeOps);


//        launch(new Main());
    }
}
