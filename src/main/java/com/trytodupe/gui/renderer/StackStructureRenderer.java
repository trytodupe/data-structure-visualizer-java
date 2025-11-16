package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.gui.HighlightInfo;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;

/**
 * Renderer for StackStructure.
 * Displays stack elements in a vertical layout with highlighting support for the top element.
 */
public class StackStructureRenderer extends DataStructureRenderer<StackStructure> {

    private static final float CELL_WIDTH = 80;
    private static final float CELL_HEIGHT = 50;
    private static final float CELL_SPACING = 5;

    @Override
    public void render(StackStructure stackStructure, HighlightInfo highlightInfo) {
        ImGui.begin("Stack Visualization");
        renderContent(stackStructure, highlightInfo);
        ImGui.end();
    }

    @Override
    public void renderContent(StackStructure stackStructure, HighlightInfo highlightInfo) {
        ImVec2 canvasPos = ImGui.getCursorScreenPos();

        float startX = canvasPos.x + 50;
        float startY = canvasPos.y + 50;

        // Draw title
        ImGui.text("Stack Elements (Size: " + stackStructure.size() + ")");
        ImGui.spacing();

        // Draw stack elements from bottom to top
        int stackSize = stackStructure.size();
        for (int i = 0; i < stackSize; i++) {
            float cellX = startX;
            float cellY = startY + (stackSize - i - 1) * (CELL_HEIGHT + CELL_SPACING);

            // Top element is highlighted
            boolean isTop = (i == stackSize - 1);
            boolean shouldHighlight = highlightInfo != null && highlightInfo.arrayIndices.contains(i);
            boolean highlight = shouldHighlight || isTop;

            // Draw stack cell
            drawStackCell(cellX, cellY, CELL_WIDTH, CELL_HEIGHT, highlight, isTop);

            // Draw element index
            ImGui.setCursorScreenPos(cellX + CELL_WIDTH + 10, cellY + 15);
            ImGui.text("Index " + i);
        }

        // Draw empty slots if any
        if (highlightInfo != null && highlightInfo.highlightTempSlot) {
            float tempSlotY = startY + stackSize * (CELL_HEIGHT + CELL_SPACING);
            drawStackCell(startX, tempSlotY, CELL_WIDTH, CELL_HEIGHT, true, false);
            ImGui.setCursorScreenPos(startX + CELL_WIDTH + 10, tempSlotY + 15);
            ImGui.text("Temp");
        }
    }

    /**
     * Draw a single stack cell
     */
    private void drawStackCell(float x, float y, float width, float height, boolean highlighted, boolean isTop) {
        ImVec2 min = new ImVec2(x, y);
        ImVec2 max = new ImVec2(x + width, y + height);

        int borderColor = highlighted ? 0xFF00FF00 : 0xFFFFFFFF; // Green if highlighted, white otherwise
        ImGui.getWindowDrawList().addRect(min.x, min.y, max.x, max.y, borderColor, 0, ImDrawFlags.None, 2);

        if (highlighted) {
            ImGui.getWindowDrawList().addRectFilled(min.x, min.y, max.x, max.y, 0x8000FF00); // Semi-transparent green fill
        }

        // Draw "TOP" indicator if this is the top element
        if (isTop) {
            ImGui.getWindowDrawList().addLine(
                x, y - 15,
                x + width / 2, y - 5,
                0xFFFF0000, 2
            );
            ImGui.getWindowDrawList().addLine(
                x + width, y - 15,
                x + width / 2, y - 5,
                0xFFFF0000, 2
            );
        }
    }
}
