package com.trytodupe;

import com.google.gson.JsonObject;
import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.ArrayDeleteUserOperation;
import com.trytodupe.operation.array.ArrayInitUserOperation;
import com.trytodupe.operation.array.ArrayInsertUserOperation;
import com.trytodupe.serialization.GsonProvider;
import com.trytodupe.serialization.ISerializable;
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

        ArrayStructure arrayStructure = new ArrayStructure();

        List<UserOperation<ArrayStructure>> testOperations = new ArrayList<>();

        testOperations.add(new ArrayInitUserOperation(arrayStructure, new int[]{1, 2, 3}));
        testOperations.add(new ArrayInsertUserOperation(arrayStructure, 1, 99));
        testOperations.add(new ArrayDeleteUserOperation(arrayStructure, 2));


        // forward testing
        for (UserOperation<ArrayStructure> op : testOperations) {

            JsonObject json = op.toJson(GsonProvider.get());

            ISerializable deserialized = ISerializable.fromJson(GsonProvider.get(), json);

            System.out.println(deserialized.getClass());

            if (deserialized instanceof UserOperation<?>) {
                ((UserOperation<?>) deserialized).execute();
            }

//            System.out.println(op.getDescription());
//
//            System.out.println(op.toJson(GsonProvider.get()));
//
//            op.execute();
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