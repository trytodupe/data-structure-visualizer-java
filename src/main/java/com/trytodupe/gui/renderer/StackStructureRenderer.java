package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.gui.HighlightInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;

public class StackStructureRenderer extends DataStructureRenderer<StackStructure> {

    private static final float CELL_WIDTH = 80f;
    private static final float CELL_HEIGHT = 40f;
    private static final float CELL_SPACING = 6f;

    @Override
    public void render(StackStructure stackStructure, HighlightInfo highlightInfo) {
        ImGui.begin("Stack Visualization");
        renderContent(stackStructure, highlightInfo);
        ImGui.end();
    }

    @Override
    public void renderContent(StackStructure stackStructure, HighlightInfo highlightInfo) {
        ImVec2 cursorPos = ImGui.getCursorPos();
        ImVec2 windowPos = ImGui.getWindowPos();
        float startX = windowPos.x + cursorPos.x + 20;
        float startY = windowPos.y + cursorPos.y + 40;

        int stackSize = stackStructure.size();
        ImGui.text("Stack (size=" + stackSize + ")");
        ImGui.spacing();

        for (int i = 0; i < stackSize; i++) {
            float cellY = startY + (stackSize - i - 1) * (CELL_HEIGHT + CELL_SPACING);
            drawCell(startX, cellY, CELL_WIDTH, CELL_HEIGHT, highlightInfo, i, stackSize - 1);
            ImGui.getWindowDrawList().addText(startX + CELL_WIDTH + 10, cellY + 10, 0xFFFFFFFF, "Index " + i);
        }

        if (highlightInfo != null && highlightInfo.highlightTempSlot) {
            float tempY = startY + stackSize * (CELL_HEIGHT + CELL_SPACING);
            drawRect(startX, tempY, CELL_WIDTH, CELL_HEIGHT, true);
            ImGui.getWindowDrawList().addText(startX + CELL_WIDTH + 10, tempY + 10, 0xFFFFFFFF, "Temp");
        }
    }

    private void drawCell(float x, float y, float width, float height, HighlightInfo highlightInfo, int index, int topIndex) {
        boolean highlight = highlightInfo != null && highlightInfo.arrayIndices.contains(index);
        boolean isTop = index == topIndex;
        drawRect(x, y, width, height, highlight || isTop);
        if (isTop) {
            ImGui.getWindowDrawList().addText(x + width + 5, y - 15, 0xFFFF0000, "TOP");
        }
    }

    private void drawRect(float x, float y, float width, float height, boolean highlight) {
        ImVec2 min = new ImVec2(x, y);
        ImVec2 max = new ImVec2(x + width, y + height);
        int color = highlight ? 0xFF00FF00 : 0xFFFFFFFF;
        ImGui.getWindowDrawList().addRect(min.x, min.y, max.x, max.y, color, 0f, ImDrawFlags.None, 2f);
        if (highlight) {
            ImGui.getWindowDrawList().addRectFilled(min.x, min.y, max.x, max.y, 0x4000FF00);
        }
    }
}
