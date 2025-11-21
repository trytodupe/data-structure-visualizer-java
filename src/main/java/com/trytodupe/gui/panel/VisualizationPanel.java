package com.trytodupe.gui.panel;

import com.trytodupe.Main;
import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.datastructure.tree.node.AVLNodeExtension;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.datastructure.tree.node.HuffmanNodeExtension;
import com.trytodupe.gui.HighlightInfo;
import imgui.ImGui;

/**
 * Renders the data structure visualization window.
 */
public class VisualizationPanel {

    private final UiState state;

    public VisualizationPanel(UiState state) {
        this.state = state;
    }

    public void render() {
        ImGui.begin("Visualization");

        HighlightInfo highlightInfo = state.getCurrentHighlight();
        if (state.activeVisualizationClass == null) {
            ImGui.text("No data structure selected.");
            ImGui.end();
            return;
        }

        state.treeRenderer.resetLabelProviders();

        Class<?> clazz = state.activeVisualizationClass;
        if (LinkedListStructure.class.isAssignableFrom(clazz)) {
            LinkedListStructure list = Main.getDataStructure(LinkedListStructure.class);
            state.linkedListRenderer.renderContent(list, highlightInfo);
        } else if (AVLTreeStructure.class.isAssignableFrom(clazz)) {
            AVLTreeStructure<?> avl = Main.getDataStructure(AVLTreeStructure.class);
            state.treeRenderer.setSecondaryLabelProvider(node -> {
                if (node == null) {
                    return null;
                }
                int balance = avl.getBalance(node.getUUID());
                int height = 0;
                if (node.getExtension() instanceof AVLNodeExtension) {
                    AVLNodeExtension ext = (AVLNodeExtension) node.getExtension();
                    height = ext.getHeight();
                }
                return "h=" + height + " b=" + balance;
            });
            state.treeRenderer.renderContent(avl, highlightInfo);
        } else if (HuffmanTreeStructure.class.isAssignableFrom(clazz)) {
            HuffmanTreeStructure<?> huffman = Main.getDataStructure(HuffmanTreeStructure.class);
            state.treeRenderer.setPrimaryLabelProvider(node -> {
                Object value = node.getValue();
                return value == null ? "*" : value.toString();
            });
            state.treeRenderer.setSecondaryLabelProvider(node -> {
                if (node.getExtension() instanceof HuffmanNodeExtension) {
                    HuffmanNodeExtension ext = (HuffmanNodeExtension) node.getExtension();
                    return "w=" + ext.getWeight();
                }
                return null;
            });
            state.treeRenderer.renderContent(huffman, highlightInfo);
        } else if (BinarySearchTreeStructure.class.isAssignableFrom(clazz)) {
            BinarySearchTreeStructure<?> bst = Main.getDataStructure(BinarySearchTreeStructure.class);
            state.treeRenderer.renderContent(bst, highlightInfo);
        } else if (BinaryTreeStructure.class.isAssignableFrom(clazz)) {
            BinaryTreeStructure<?> tree = Main.getDataStructure(BinaryTreeStructure.class);
            state.treeRenderer.renderContent(tree, highlightInfo);
        } else if (StackStructure.class.isAssignableFrom(clazz)) {
            StackStructure stack = Main.getDataStructure(StackStructure.class);
            state.stackRenderer.renderContent(stack, highlightInfo);
        } else if (ArrayStructure.class.isAssignableFrom(clazz)) {
            ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
            state.arrayRenderer.renderContent(array, highlightInfo);
        } else {
            ImGui.text("Unsupported visualization target: " + clazz.getSimpleName());
        }

        if (highlightInfo != null && !highlightInfo.isEmpty()) {
            ImGui.separator();
            ImGui.text("Highlight Summary");
            ImGui.bulletText("Array indices: " + highlightInfo.arrayIndices.size());
            ImGui.bulletText("Nodes: " + highlightInfo.nodeUUIDs.size());
            ImGui.bulletText("Arrows: " + highlightInfo.arrows.size());
        }

        ImGui.end();
    }
}
