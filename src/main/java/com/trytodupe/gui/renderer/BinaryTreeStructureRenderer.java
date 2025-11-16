package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.gui.HighlightInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Renderer for BinaryTreeStructure.
 * Displays binary tree nodes in a hierarchical layout with tree structure visualization.
 */
public class BinaryTreeStructureRenderer extends DataStructureRenderer<BinaryTreeStructure<?>> {

    private static final float NODE_RADIUS = 25;
    private static final float LEVEL_HEIGHT = 80;
    private static final float MIN_NODE_DISTANCE = 80;
    private Map<String, NodePosition> nodePositions = new HashMap<>();

    @Override
    public void render(BinaryTreeStructure<?> treeStructure, HighlightInfo highlightInfo) {
        ImGui.begin("Binary Tree Visualization");
        renderContent(treeStructure, highlightInfo);
        ImGui.end();
    }

    @Override
    public void renderContent(BinaryTreeStructure<?> treeStructure, HighlightInfo highlightInfo) {
        ImVec2 canvasPos = ImGui.getCursorScreenPos();
        ImVec2 canvasSize = ImGui.getContentRegionAvail();

        // Clear previous positions
        nodePositions.clear();

        // Get tree root
        BinaryTreeNode<?> root = treeStructure.getRoot();

        if (root != null) {
            ImGui.text("Binary Tree (Root UUID: " + root.getUUID() + ")");
            ImGui.spacing();

            // Calculate positions for all nodes
            float startX = canvasPos.x + canvasSize.x / 2;
            float startY = canvasPos.y + 30;
            float horizontalSpacing = 150;

            calculateNodePositions(root, startX, startY, horizontalSpacing);

            // Draw edges
            drawEdges(root, highlightInfo);

            // Draw nodes
            drawNodes(root, highlightInfo);

            // Draw temp slot if needed
            if (highlightInfo != null && highlightInfo.highlightTempSlot) {
                drawTempSlot(canvasPos.x + 50, canvasPos.y + 50);
            }
        } else {
            ImGui.text("Tree is empty");
        }
    }

    /**
     * Calculate node positions using a simple binary tree layout algorithm
     */
    private void calculateNodePositions(BinaryTreeNode<?> node, float x, float y, float spacing) {
        if (node == null) return;

        NodePosition pos = new NodePosition(x, y);
        nodePositions.put(node.getUUID().toString(), pos);

        if (node.getLeft() != null || node.getRight() != null) {
            float nextSpacing = spacing / 2;
            float leftX = x - spacing / 2;
            float rightX = x + spacing / 2;
            float childY = y + LEVEL_HEIGHT;

            if (node.getLeft() != null) {
                calculateNodePositions(node.getLeft(), leftX, childY, nextSpacing);
            }
            if (node.getRight() != null) {
                calculateNodePositions(node.getRight(), rightX, childY, nextSpacing);
            }
        }
    }

    /**
     * Draw edges between nodes
     */
    private void drawEdges(BinaryTreeNode<?> node, HighlightInfo highlightInfo) {
        if (node == null) return;

        NodePosition nodePos = nodePositions.get(node.getUUID().toString());
        if (nodePos == null) return;

        if (node.getLeft() != null) {
            NodePosition leftPos = nodePositions.get(node.getLeft().getUUID().toString());
            if (leftPos != null) {
                ImGui.getWindowDrawList().addLine(
                    nodePos.x, nodePos.y,
                    leftPos.x, leftPos.y,
                    0xFFFFFFFF, 2
                );
            }
            drawEdges(node.getLeft(), highlightInfo);
        }

        if (node.getRight() != null) {
            NodePosition rightPos = nodePositions.get(node.getRight().getUUID().toString());
            if (rightPos != null) {
                ImGui.getWindowDrawList().addLine(
                    nodePos.x, nodePos.y,
                    rightPos.x, rightPos.y,
                    0xFFFFFFFF, 2
                );
            }
            drawEdges(node.getRight(), highlightInfo);
        }
    }

    /**
     * Draw nodes
     */
    private void drawNodes(BinaryTreeNode<?> node, HighlightInfo highlightInfo) {
        if (node == null) return;

        NodePosition nodePos = nodePositions.get(node.getUUID().toString());
        if (nodePos == null) return;

        // Determine if node should be highlighted
        boolean isHighlighted = highlightInfo != null &&
            highlightInfo.nodeUUIDs.contains(java.util.UUID.fromString(node.getUUID().toString()));

        // Draw node circle
        int color = isHighlighted ? 0xFF00FF00 : 0xFFFFFFFF;
        ImGui.getWindowDrawList().addCircle(nodePos.x, nodePos.y, NODE_RADIUS, color, 32, 2);

        if (isHighlighted) {
            ImGui.getWindowDrawList().addCircleFilled(nodePos.x, nodePos.y, NODE_RADIUS - 2, 0x8000FF00);
        }

        // Draw node value
        String value = node.getValue() != null ? node.getValue().toString() : "null";
        ImVec2 textSize = ImGui.calcTextSize(value);
        ImGui.getWindowDrawList().addText(
            nodePos.x - textSize.x / 2,
            nodePos.y - textSize.y / 2,
            0xFF000000,
            value
        );

        // Recursively draw children
        if (node.getLeft() != null) {
            drawNodes(node.getLeft(), highlightInfo);
        }
        if (node.getRight() != null) {
            drawNodes(node.getRight(), highlightInfo);
        }
    }

    /**
     * Draw temp slot indicator
     */
    private void drawTempSlot(float x, float y) {
        ImGui.getWindowDrawList().addRect(x, y, x + 60, y + 60, 0xFF00FF00, 0, ImDrawFlags.None, 2);
        ImGui.getWindowDrawList().addRectFilled(x, y, x + 60, y + 60, 0x8000FF00);
        ImGui.getWindowDrawList().addText(x + 20, y + 20, 0xFF000000, "Temp");
    }

    /**
     * Helper class to store node positions
     */
    private static class NodePosition {
        float x, y;

        NodePosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
