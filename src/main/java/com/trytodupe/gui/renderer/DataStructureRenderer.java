package com.trytodupe.gui.renderer;

import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.gui.HighlightInfo;

/**
 * Abstract base renderer for visualizing data structures.
 */
public abstract class DataStructureRenderer<T extends DataStructure> {

    public abstract void render(T dataStructure, HighlightInfo highlightInfo);

    public abstract void renderContent(T dataStructure, HighlightInfo highlightInfo);
}
