package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.gui.HighlightInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;

/**
 * Renderer for ArrayStructure.
 * Displays array elements in a horizontal layout with highlighting support.
 */
public class ArrayStructureRenderer extends DataStructureRenderer<ArrayStructure> {

    private static final float CELL_WIDTH = 50;
    private static final float CELL_HEIGHT = 50;
    private static final float CELL_SPACING = 10;
    private static final float TEXT_CENTER_OFFSET_X = 25;
    private static final float TEXT_CENTER_OFFSET_Y = 20;

    @Override
    public void render(ArrayStructure arrayStructure, HighlightInfo highlightInfo) {
        ImGui.begin("Array Visualization");
        renderContent(arrayStructure, highlightInfo);
        ImGui.end();
    }

    @Override
    public void renderContent(ArrayStructure arrayStructure, HighlightInfo highlightInfo) {
        ImVec2 canvas = ImGui.getContentRegionAvail();
        ImVec2 canvasPos = ImGui.getCursorScreenPos();

        // Draw array elements
        int arraySize = arrayStructure.size();
        float startX = canvasPos.x + 50;
        float startY = canvasPos.y + 50;

        // Draw title
        ImGui.text("Array Elements (" + arrayStructure.size() + "/" + arrayStructure.capacity() + ")");
        ImGui.spacing();

        // Determine if entire array should be highlighted
        boolean highlightEntire = highlightInfo != null && highlightInfo.highlightEntireArray;

        for (int i = 0; i < arraySize; i++) {
            float cellX = startX + i * (CELL_WIDTH + CELL_SPACING);
            float cellY = startY;

            // Determine if this cell should be highlighted
            boolean isCellHighlighted = highlightInfo != null && highlightInfo.arrayIndices.contains(i);
            boolean shouldHighlight = highlightEntire || isCellHighlighted;

            // Draw cell background
            drawArrayCell(cellX, cellY, CELL_WIDTH, CELL_HEIGHT, shouldHighlight);

            // Draw cell index below
            ImGui.setCursorScreenPos(cellX, cellY + CELL_HEIGHT + 5);
            ImGui.text(String.valueOf(i));
        }

        // Draw temp slot if highlighted
        if (highlightInfo != null && highlightInfo.highlightTempSlot) {
            float tempSlotX = startX + arraySize * (CELL_WIDTH + CELL_SPACING);
            float tempSlotY = startY;
            drawArrayCell(tempSlotX, tempSlotY, CELL_WIDTH, CELL_HEIGHT, true);
            ImGui.setCursorScreenPos(tempSlotX, tempSlotY + CELL_HEIGHT + 5);
            ImGui.text("T");
        }

        // Draw arrows if any
        if (highlightInfo != null && !highlightInfo.arrows.isEmpty()) {
            ImGui.text("Arrows: " + highlightInfo.arrows.size());
        }
    }

    /**
     * Draw a single array cell
     */
    private void drawArrayCell(float x, float y, float width, float height, boolean highlighted) {
        ImVec2 min = new ImVec2(x, y);
        ImVec2 max = new ImVec2(x + width, y + height);

        int color = highlighted ? 0xFF00FF00 : 0xFFFFFFFF; // Green if highlighted, white otherwise
        ImGui.getWindowDrawList().addRect(min.x, min.y, max.x, max.y, color, 0, ImDrawFlags.None, 2);

        if (highlighted) {
            ImGui.getWindowDrawList().addRectFilled(min.x, min.y, max.x, max.y, 0x8000FF00); // Semi-transparent green fill
        }
    }
}
