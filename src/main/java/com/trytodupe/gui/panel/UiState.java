package com.trytodupe.gui.panel;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.gui.TreeNodePicker;
import com.trytodupe.gui.history.OperationHistoryEntry;
import com.trytodupe.gui.history.OperationHistoryManager;
import com.trytodupe.gui.renderer.ArrayStructureRenderer;
import com.trytodupe.gui.renderer.BinaryTreeStructureRenderer;
import com.trytodupe.gui.renderer.LinkedListStructureRenderer;
import com.trytodupe.gui.renderer.StackStructureRenderer;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.gui.playback.PlaybackController;
import com.trytodupe.gui.HighlightInfo;

public class UiState {
    public final PlaybackController playbackController;
    public final OperationHistoryManager historyManager;
    public final TreeNodePicker nodePicker = new TreeNodePicker();
    public final ArrayStructureRenderer arrayRenderer = new ArrayStructureRenderer();
    public final StackStructureRenderer stackRenderer = new StackStructureRenderer();
    public final LinkedListStructureRenderer linkedListRenderer = new LinkedListStructureRenderer();
    public final BinaryTreeStructureRenderer treeRenderer = new BinaryTreeStructureRenderer();

    public Class<? extends DataStructure> activeVisualizationClass = ArrayStructure.class;
    public OperationHistoryEntry activeHistoryEntry;

    public UiState(PlaybackController playbackController, OperationHistoryManager historyManager) {
        this.playbackController = playbackController;
        this.historyManager = historyManager;
        this.treeRenderer.setNodePicker(nodePicker);
    }

    public void setActiveVisualizationClass(Class<? extends DataStructure> clazz) {
        if (clazz != null) {
            this.activeVisualizationClass = clazz;
        }
    }

    public void startNewOperation(UserOperation<?> operation) {
        if (operation == null) {
            return;
        }
        OperationHistoryEntry entry = historyManager.add(operation);
        this.activeHistoryEntry = entry;
        this.activeVisualizationClass = operation.getDataStructure().getClass();
        historyManager.setInProgressEntry(entry);
        playbackController.start(operation);
    }

    public void visualizeEntry(OperationHistoryEntry entry) {
        if (entry == null) {
            return;
        }
        this.activeHistoryEntry = entry;
        this.activeVisualizationClass = entry.getOperation().getDataStructure().getClass();
        entry.markInProgress();
        historyManager.setInProgressEntry(entry);
        playbackController.start(entry.getOperation());
    }

    public void resetHistoryState() {
        playbackController.stop();
        historyManager.clearAll();
        activeHistoryEntry = null;
    }

    public HighlightInfo getCurrentHighlight() {
        return playbackController.getHighlightInfo();
    }
}
