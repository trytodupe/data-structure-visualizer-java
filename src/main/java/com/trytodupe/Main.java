package com.trytodupe;

import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.gui.UIPanelManager;
import com.trytodupe.gui.history.OperationHistoryManager;
import com.trytodupe.gui.playback.PlaybackController;
import imgui.app.Application;
import imgui.app.Configuration;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    public static final boolean DEBUG = true;

    private static final Map<Class<? extends DataStructure>, DataStructure> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put(ArrayStructure.class, new ArrayStructure());
        REGISTRY.put(StackStructure.class, new StackStructure());
        REGISTRY.put(LinkedListStructure.class, new LinkedListStructure());
        REGISTRY.put(BinaryTreeStructure.class, new BinaryTreeStructure<>());
        REGISTRY.put(BinarySearchTreeStructure.class, new BinarySearchTreeStructure<>());
        REGISTRY.put(HuffmanTreeStructure.class, new HuffmanTreeStructure<>());
        REGISTRY.put(AVLTreeStructure.class, new AVLTreeStructure<>());
    }

    @SuppressWarnings("unchecked")
    public static <T extends DataStructure> T getDataStructure(Class<T> clazz) {
        return (T) REGISTRY.get(clazz);
    }

    private final PlaybackController playbackController = new PlaybackController();
    private final OperationHistoryManager historyManager = new OperationHistoryManager();
    private UIPanelManager uiPanelManager;

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Data Structure Visualizer");
        config.setWidth(1600);
        config.setHeight(900);
    }

    @Override
    public void process() {
        if (uiPanelManager == null) {
            uiPanelManager = new UIPanelManager(playbackController, historyManager);
        }
        uiPanelManager.render();
    }

    public static void main(String[] args) {
        launch(new Main());
    }
}
