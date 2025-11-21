package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.gui.HighlightInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;

public class ArrayStructureRenderer extends DataStructureRenderer<ArrayStructure> {

    private static final float CELL_WIDTH = 50f;
    private static final float CELL_HEIGHT = 50f;
    private static final float CELL_SPACING = 10f;

    @Override
    public void render(ArrayStructure arrayStructure, HighlightInfo highlightInfo) {
        ImGui.begin("Array Visualization");
        renderContent(arrayStructure, highlightInfo);
        ImGui.end();
    }

    @Override
    public void renderContent(ArrayStructure arrayStructure, HighlightInfo highlightInfo) {
        ImVec2 canvasPos = ImGui.getCursorScreenPos();
        float startX = canvasPos.x + 50;
        float startY = canvasPos.y + 50;

        ImGui.text("Array Elements (" + arrayStructure.getSize() + ")");
        ImGui.spacing();

        boolean highlightEntire = highlightInfo != null && highlightInfo.highlightEntireArray;

        for (int i = 0; i < arrayStructure.getSize(); i++) {
            float cellX = startX + i * (CELL_WIDTH + CELL_SPACING);
            float cellY = startY;
            boolean isHighlighted = highlightInfo != null && highlightInfo.arrayIndices.contains(i);
            drawCell(cellX, cellY, CELL_WIDTH, CELL_HEIGHT, highlightEntire || isHighlighted);

            ImGui.setCursorScreenPos(cellX + 15, cellY + 15);
            ImGui.text(String.valueOf(arrayStructure.getData(i)));
            ImGui.setCursorScreenPos(cellX, cellY + CELL_HEIGHT + 5);
            ImGui.text(String.valueOf(i));
        }

        if (highlightInfo != null && highlightInfo.highlightTempSlot) {
            float tempSlotX = startX + arrayStructure.getSize() * (CELL_WIDTH + CELL_SPACING);
            drawCell(tempSlotX, startY, CELL_WIDTH, CELL_HEIGHT, true);
            ImGui.setCursorScreenPos(tempSlotX, startY + CELL_HEIGHT + 5);
            ImGui.text("T");
        }
    }

    private void drawCell(float x, float y, float width, float height, boolean highlight) {
        ImVec2 min = new ImVec2(x, y);
        ImVec2 max = new ImVec2(x + width, y + height);
        int borderColor = highlight ? 0xFF00FF00 : 0xFFFFFFFF;
        ImGui.getWindowDrawList().addRect(min.x, min.y, max.x, max.y, borderColor, 0f, ImDrawFlags.None, 2f);
        if (highlight) {
            ImGui.getWindowDrawList().addRectFilled(min.x, min.y, max.x, max.y, 0x4000FF00);
        }
    }
}
