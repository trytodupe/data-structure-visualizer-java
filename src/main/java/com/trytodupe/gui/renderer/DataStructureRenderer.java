package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.gui.HighlightInfo;

/**
 * Abstract base class for rendering different data structures.
 * Each concrete implementation handles rendering of a specific data structure type.
 */
public abstract class DataStructureRenderer<T extends DataStructure> {

    protected float posX = 400;
    protected float posY = 50;
    protected float width = 1150;
    protected float height = 850;

    /**
     * Render the data structure with highlight information
     * @param dataStructure the data structure to render
     * @param highlightInfo highlighting information for the current step
     */
    public abstract void render(T dataStructure, HighlightInfo highlightInfo);

    /**
     * Render the content of the data structure (without ImGui.begin/end)
     * This method is called within an already-open ImGui window
     * @param dataStructure the data structure to render
     * @param highlightInfo highlighting information for the current step
     */
    public abstract void renderContent(T dataStructure, HighlightInfo highlightInfo);

    /**
     * Get the renderer for the given data structure type
     */
    public static DataStructureRenderer<?> getRenderer(Class<?> dataStructureClass) {
        // This will be implemented as a factory method
        return null;
    }

    // Helper methods for rendering
    protected void drawText(float x, float y, String text) {
        // ImGui drawing will be handled in concrete implementations
    }

    protected void drawRect(float x, float y, float width, float height, int color) {
        // ImGui drawing will be handled in concrete implementations
    }

    protected void drawCircle(float x, float y, float radius, int color) {
        // ImGui drawing will be handled in concrete implementations
    }
}
