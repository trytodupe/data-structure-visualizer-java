package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.datastructure.LinkedListStructure.Node;
import com.trytodupe.gui.HighlightInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;

import java.util.List;
import java.util.UUID;

public class LinkedListStructureRenderer extends DataStructureRenderer<LinkedListStructure> {

    private static final float NODE_WIDTH = 80f;
    private static final float NODE_HEIGHT = 50f;
    private static final float NODE_SPACING = 30f;

    @Override
    public void render(LinkedListStructure dataStructure, HighlightInfo highlightInfo) {
        ImGui.begin("Linked List Visualization");
        renderContent(dataStructure, highlightInfo);
        ImGui.end();
    }

    @Override
    public void renderContent(LinkedListStructure dataStructure, HighlightInfo highlightInfo) {
        ImVec2 canvasPos = ImGui.getCursorScreenPos();
        float startX = canvasPos.x + 20f;
        float startY = canvasPos.y + 60f;

        List<Node> nodes = dataStructure.getNodes();
        ImGui.text("Linked List (size=" + nodes.size() + ")");
        ImGui.spacing();

        ImVec2 drawPos = new ImVec2(startX, startY);
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            boolean highlight = highlightInfo != null &&
                highlightInfo.nodeUUIDs.contains(UUID.fromString(node.getUuid()));
            drawNode(drawPos.x, drawPos.y, NODE_WIDTH, NODE_HEIGHT, node.getValue(), highlight);

            if (i < nodes.size() - 1) {
                drawArrow(drawPos.x + NODE_WIDTH, drawPos.y + NODE_HEIGHT / 2f,
                    drawPos.x + NODE_WIDTH + NODE_SPACING, drawPos.y + NODE_HEIGHT / 2f);
            }
            drawPos.x += NODE_WIDTH + NODE_SPACING;
        }

        if (nodes.isEmpty()) {
            ImGui.textDisabled("(empty)");
        }
    }

    private void drawNode(float x, float y, float width, float height, int value, boolean highlight) {
        ImVec2 min = new ImVec2(x, y);
        ImVec2 max = new ImVec2(x + width, y + height);
        int borderColor = highlight ? 0xFF00FF00 : 0xFFFFFFFF;
        int fillColor = highlight ? 0x4000FF00 : 0xFF1F1F1F;
        ImGui.getWindowDrawList().addRectFilled(min.x, min.y, max.x, max.y, fillColor, 6f);
        ImGui.getWindowDrawList().addRect(min.x, min.y, max.x, max.y, borderColor, 6f, ImDrawFlags.None, 2f);
        String text = String.valueOf(value);
        ImVec2 textSize = ImGui.calcTextSize(text);
        ImGui.getWindowDrawList().addText(
            min.x + (width - textSize.x) / 2f,
            min.y + (height - textSize.y) / 2f,
            0xFFFFFFFF,
            text
        );
    }

    private void drawArrow(float fromX, float fromY, float toX, float toY) {
        ImGui.getWindowDrawList().addLine(fromX, fromY, toX, toY, 0xFFFFFFFF, 2f);
        float arrowSize = 6f;
        ImGui.getWindowDrawList().addTriangleFilled(
            toX, toY,
            toX - arrowSize, toY - arrowSize / 2f,
            toX - arrowSize, toY + arrowSize / 2f,
            0xFFFFFFFF
        );
    }
}
