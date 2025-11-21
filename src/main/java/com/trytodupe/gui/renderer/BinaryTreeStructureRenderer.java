package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.gui.HighlightInfo;
import com.trytodupe.gui.TreeNodePicker;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiMouseButton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BinaryTreeStructureRenderer extends DataStructureRenderer<BinaryTreeStructure<?>> {

    private static final float NODE_RADIUS = 20f;
    private static final float LEVEL_HEIGHT = 80f;
    private static final float MIN_HORIZONTAL_SPACING = 60f;

    private final Map<String, NodePosition> nodePositions = new HashMap<>();
    private TreeNodePicker nodePicker;

    public void setNodePicker(TreeNodePicker picker) {
        this.nodePicker = picker;
    }

    @Override
    public void render(BinaryTreeStructure<?> treeStructure, HighlightInfo highlightInfo) {
        ImGui.begin("Tree Visualization");
        renderContent(treeStructure, highlightInfo);
        ImGui.end();
    }

    @Override
    public void renderContent(BinaryTreeStructure<?> treeStructure, HighlightInfo highlightInfo) {
        ImVec2 canvasPos = ImGui.getCursorScreenPos();
        ImVec2 canvasSize = ImGui.getContentRegionAvail();

        nodePositions.clear();
        BinaryTreeNode<?> root = treeStructure.getRoot();
        if (root == null) {
            ImGui.text("Tree is empty.");
            return;
        }

        float margin = 80f;
        float minX = canvasPos.x + margin;
        float maxX = canvasPos.x + Math.max(200f, canvasSize.x - margin);
        float startY = canvasPos.y + 60f;
        layoutNode(root, minX, maxX, startY);
        drawEdges(root);
        drawNodes(root, highlightInfo);

        if (highlightInfo != null && highlightInfo.highlightTempSlot) {
            drawTempSlot(canvasPos.x + 20f, canvasPos.y + 20f);
        }
    }

    private void layoutNode(BinaryTreeNode<?> node, float minX, float maxX, float y) {
        float x = (minX + maxX) * 0.5f;
        nodePositions.put(node.getUUID().toString(), new NodePosition(x, y));
        float nextY = y + LEVEL_HEIGHT;
        float halfGap = MIN_HORIZONTAL_SPACING * 0.5f;
        if (node.getLeft() != null) {
            float childMax = Math.min(x - halfGap, maxX);
            layoutNode(node.getLeft(), minX, childMax, nextY);
        }
        if (node.getRight() != null) {
            float childMin = Math.max(x + halfGap, minX);
            layoutNode(node.getRight(), childMin, maxX, nextY);
        }
    }

    private void drawEdges(BinaryTreeNode<?> node) {
        if (node == null) {
            return;
        }
        NodePosition parent = nodePositions.get(node.getUUID().toString());
        if (parent == null) {
            return;
        }
        if (node.getLeft() != null) {
            NodePosition left = nodePositions.get(node.getLeft().getUUID().toString());
            if (left != null) {
                ImGui.getWindowDrawList().addLine(parent.x, parent.y, left.x, left.y, 0xFFFFFFFF, 2f);
            }
            drawEdges(node.getLeft());
        }
        if (node.getRight() != null) {
            NodePosition right = nodePositions.get(node.getRight().getUUID().toString());
            if (right != null) {
                ImGui.getWindowDrawList().addLine(parent.x, parent.y, right.x, right.y, 0xFFFFFFFF, 2f);
            }
            drawEdges(node.getRight());
        }
    }

    private void drawNodes(BinaryTreeNode<?> node, HighlightInfo highlightInfo) {
        if (node == null) {
            return;
        }
        NodePosition pos = nodePositions.get(node.getUUID().toString());
        if (pos == null) {
            return;
        }
        boolean highlight = highlightInfo != null && highlightInfo.nodeUUIDs.contains(UUID.fromString(node.getUUID().toString()));
        boolean picking = nodePicker != null && nodePicker.isPicking();
        boolean hoveredForPick = false;
        if (picking) {
            ImVec2 mousePos = ImGui.getMousePos();
            float dx = mousePos.x - pos.x;
            float dy = mousePos.y - pos.y;
            hoveredForPick = dx * dx + dy * dy <= (NODE_RADIUS * NODE_RADIUS);
            if (hoveredForPick && ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
                nodePicker.complete(node.getUUID().toString());
            }
        }

        int borderColor = highlight ? 0xFF00FF00 : 0xFFFFFFFF;
        if (hoveredForPick) {
            borderColor = 0xFF00FFFF;
        }
        ImGui.getWindowDrawList().addCircle(pos.x, pos.y, NODE_RADIUS, borderColor, 32, 2f);
        if (highlight || hoveredForPick) {
            int fillColor = hoveredForPick ? 0x4000FFFF : 0x4000FF00;
            ImGui.getWindowDrawList().addCircleFilled(pos.x, pos.y, NODE_RADIUS - 2f, fillColor);
        }
        String value = node.getValue() == null ? "null" : node.getValue().toString();
        ImVec2 text = ImGui.calcTextSize(value);
        ImGui.getWindowDrawList().addText(pos.x - text.x / 2f, pos.y - text.y / 2f, 0xFF000000, value);

        drawNodes(node.getLeft(), highlightInfo);
        drawNodes(node.getRight(), highlightInfo);
    }

    private void drawTempSlot(float x, float y) {
        ImGui.getWindowDrawList().addRect(x, y, x + 60f, y + 60f, 0xFF00FF00, 0f, ImDrawFlags.None, 2f);
        ImGui.getWindowDrawList().addRectFilled(x, y, x + 60f, y + 60f, 0x4000FF00);
        ImGui.getWindowDrawList().addText(x + 10f, y + 20f, 0xFF000000, "Temp");
    }

    private static class NodePosition {
        final float x;
        final float y;

        NodePosition(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
