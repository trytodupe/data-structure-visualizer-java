package com.trytodupe;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
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

    public static void main(String[] args) {
        OperationTestRunner.runTests();

        // Uncomment to launch GUI
        // launch(new Main());
    }
}
