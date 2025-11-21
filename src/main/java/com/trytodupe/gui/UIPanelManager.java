package com.trytodupe.gui;

import com.trytodupe.gui.panel.HistoryPanel;
import com.trytodupe.gui.panel.OperationBuilderPanel;
import com.trytodupe.gui.panel.PlaybackPanel;
import com.trytodupe.gui.panel.UiState;
import com.trytodupe.gui.panel.VisualizationPanel;
import com.trytodupe.gui.history.OperationHistoryManager;
import com.trytodupe.gui.playback.PlaybackController;

/**
 * Coordinates the individual ImGui panels.
 */
public class UIPanelManager {

    private final UiState state;
    private final OperationBuilderPanel builderPanel;
    private final VisualizationPanel visualizationPanel;
    private final PlaybackPanel playbackPanel;
    private final HistoryPanel historyPanel;

    public UIPanelManager(PlaybackController playbackController, OperationHistoryManager historyManager) {
        this.state = new UiState(playbackController, historyManager);
        this.builderPanel = new OperationBuilderPanel(state);
        this.visualizationPanel = new VisualizationPanel(state);
        this.playbackPanel = new PlaybackPanel(state);
        this.historyPanel = new HistoryPanel(state);
    }

    public void render() {
        builderPanel.render();
        visualizationPanel.render();
        playbackPanel.render();
        historyPanel.render();
    }
}
