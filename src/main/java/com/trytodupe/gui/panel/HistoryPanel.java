package com.trytodupe.gui.panel;

import com.trytodupe.gui.history.OperationHistoryEntry;
import imgui.ImGui;
import imgui.type.ImString;

import java.util.List;
import java.util.Locale;

/**
 * Renders undo/redo controls and JSON import/export for the operation history.
 */
public class HistoryPanel {

    private final UiState state;
    private final ImString historyJsonBuffer = new ImString(8192);
    private String historyStatusMessage = "";

    public HistoryPanel(UiState state) {
        this.state = state;
    }

    public void render() {
        ImGui.begin("Operation History");
        boolean locked = state.playbackController.getState() == com.trytodupe.gui.playback.PlaybackState.IN_PROGRESS;

        renderUndoRedoButtons(locked);
        ImGui.separator();
        renderImportExport();
        ImGui.separator();
        renderHistoryEntries(locked);
        ImGui.end();
    }

    private void renderUndoRedoButtons(boolean locked) {
        boolean undoDisabled = locked || !state.historyManager.canUndo();
        if (undoDisabled) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("Undo")) {
            OperationHistoryEntry undone = state.historyManager.undoLatest();
            if (undone != null) {
                state.playbackController.stop();
                state.activeHistoryEntry = null;
                state.activeVisualizationClass = undone.getOperation().getDataStructure().getClass();
            }
        }
        if (undoDisabled) {
            ImGui.endDisabled();
        }

        ImGui.sameLine();
        boolean redoDisabled = locked || !state.historyManager.canRedo();
        if (redoDisabled) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("Redo")) {
            OperationHistoryEntry redone = state.historyManager.redoNext();
            if (redone != null) {
                state.playbackController.stop();
                state.activeHistoryEntry = null;
                state.activeVisualizationClass = redone.getOperation().getDataStructure().getClass();
            }
        }
        if (redoDisabled) {
            ImGui.endDisabled();
        }

        ImGui.sameLine();
        if (redoDisabled) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("Redo && Visualize")) {
            OperationHistoryEntry redoEntry = state.historyManager.peekRedoEntry();
            if (redoEntry != null) {
                state.visualizeEntry(redoEntry);
                redoEntry.getOperation().build();
                redoEntry.getOperation().jumpTo(-1);
            }
        }
        if (redoDisabled) {
            ImGui.endDisabled();
        }
    }

    private void renderImportExport() {
        if (ImGui.button("Export JSON")) {
            historyJsonBuffer.set(state.historyManager.exportAsJson());
            historyStatusMessage = "Exported " + state.historyManager.getEntries().size() + " operations.";
        }
        ImGui.sameLine();
        if (ImGui.button("Import JSON")) {
            try {
                state.playbackController.stop();
                state.activeHistoryEntry = null;
                state.historyManager.importFromJson(historyJsonBuffer.get());
                historyStatusMessage = "Imported " + state.historyManager.getEntries().size() + " operations.";
            } catch (Exception ex) {
                historyStatusMessage = "Import failed: " + ex.getMessage();
            }
        }

        ImGui.inputTextMultiline("History JSON", historyJsonBuffer, ImGui.getContentRegionAvailX(), 120f);
        if (!historyStatusMessage.isEmpty()) {
            ImGui.textColored(0xFF66CCFF, historyStatusMessage);
        }
    }

    private void renderHistoryEntries(boolean locked) {
        List<OperationHistoryEntry> entries = state.historyManager.getEntries();
        for (int idx = entries.size() - 1; idx >= 0; idx--) {
            OperationHistoryEntry entry = entries.get(idx);
            ImGui.separator();
            ImGui.text(entry.getDescription() == null ? "(No description)" : entry.getDescription());
            ImGui.text(String.format(Locale.US, "Status: %s", entry.getStatus()));
        }
    }
}
