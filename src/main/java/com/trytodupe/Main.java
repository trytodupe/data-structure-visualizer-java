package com.trytodupe;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.ArrayDeleteUserOperation;
import com.trytodupe.operation.array.ArrayInitUserOperation;
import com.trytodupe.operation.array.ArrayInsertUserOperation;
import com.trytodupe.operation.stack.StackPopUserOperation;
import com.trytodupe.operation.stack.StackPushUserOperation;
import com.trytodupe.test.OperationTestRunner;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    public static final boolean DEBUG = true;

    private static final Map<Class<? extends DataStructure>, DataStructure> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put(ArrayStructure.class, new ArrayStructure());
        REGISTRY.put(StackStructure.class, new StackStructure());
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

    public static void main(String[] args) {

		List<UserOperation<ArrayStructure>> testArrayOps = new ArrayList<>();
		ArrayStructure array = getDataStructure(ArrayStructure.class);

		testArrayOps.add(new ArrayInitUserOperation(array, new int[]{1, 2, 3}));
		testArrayOps.add(new ArrayInsertUserOperation(array, 1, 99));
		testArrayOps.add(new ArrayDeleteUserOperation(array, 2));

		OperationTestRunner.runTestSuite(ArrayStructure.class, testArrayOps);

		List<UserOperation<StackStructure>> testStackOps = new ArrayList<>();
		StackStructure stack = getDataStructure(StackStructure.class);

		testStackOps.add(new StackPushUserOperation(stack, 1));
		testStackOps.add(new StackPushUserOperation(stack, 2));
		testStackOps.add(new StackPopUserOperation(stack));
		testStackOps.add(new StackPushUserOperation(stack, 3));

		OperationTestRunner.runTestSuite(StackStructure.class, testStackOps);


//        launch(new Main());
    }
}
