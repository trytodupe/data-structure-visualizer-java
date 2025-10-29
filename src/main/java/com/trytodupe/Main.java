package com.trytodupe;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.ArrayDeleteUserOperation;
import com.trytodupe.operation.array.ArrayInitUserOperation;
import com.trytodupe.operation.array.ArrayInsertUserOperation;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static final boolean DEBUG = true;

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Dear ImGui is Awesome!");
    }

    @Override
    public void process() {
        ImGui.text("Hello, World!");
    }

    public static void main(String[] args) {

        ArrayStructure arrayStructure = new ArrayStructure();

        List<UserOperation<ArrayStructure>> testOperations = new ArrayList<>();

        testOperations.add(new ArrayInitUserOperation(arrayStructure, new int[]{1, 2, 3}));
        testOperations.add(new ArrayInsertUserOperation(arrayStructure, 1, 99));
        testOperations.add(new ArrayDeleteUserOperation(arrayStructure, 2));


        // forward testing
        for (UserOperation<ArrayStructure> op : testOperations) {
            System.out.println(op.getDescription());
            op.execute();
            System.out.println();
        }

        // backward testing
        for (int i = testOperations.size() - 1; i >= 0; i--) {
            UserOperation<ArrayStructure> op = testOperations.get(i);
            System.out.println("Undoing: " + op.getDescription());
            op.undo();
            System.out.println();
        }


//        launch(new Main());
    }
}