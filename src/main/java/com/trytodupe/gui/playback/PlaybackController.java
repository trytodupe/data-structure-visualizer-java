package com.trytodupe.gui.playback;

import com.trytodupe.gui.HighlightInfo;
import com.trytodupe.gui.HighlightVisitor;
import com.trytodupe.operation.UserOperation;

/**
 * Coordinates stepping through a {@link UserOperation} and keeps highlight information in sync.
 */
public class PlaybackController {

    private final HighlightVisitor highlightVisitor = new HighlightVisitor();

    private UserOperation<?> activeOperation;
    private PlaybackState state = PlaybackState.IDLE;

    public PlaybackState getState() {
        return state;
    }

    public HighlightInfo getHighlightInfo() {
        return highlightVisitor.getHighlightInfo();
    }

    public UserOperation<?> getActiveOperation() {
        return activeOperation;
    }

    public boolean hasActiveOperation() {
        return activeOperation != null;
    }

    /**
     * Start a new visualization session. Resets state and previews the first step if available.
     */
    public void start(UserOperation<?> operation) {
        if (operation == null) {
            throw new IllegalArgumentException("operation cannot be null");
        }
        this.activeOperation = operation;
        this.activeOperation.build();
        this.activeOperation.jumpTo(-1);
        refreshHighlight();
        refreshState();
    }

    public int stepForward() {
        ensureActiveOperation();
        int stepIndex = activeOperation.stepForward();
        refreshHighlight();
        refreshState();
        return stepIndex;
    }

    public int stepBackward() {
        ensureActiveOperation();
        int stepIndex = activeOperation.stepBackward();
        refreshHighlight();
        refreshState();
        return stepIndex;
    }

    public int jumpTo(int targetStep) {
        ensureActiveOperation();
        int stepIndex = activeOperation.jumpTo(targetStep);
        refreshHighlight();
        refreshState();
        return stepIndex;
    }

    public void stop() {
        this.activeOperation = null;
        this.highlightVisitor.reset();
        this.state = PlaybackState.IDLE;
    }

    private void ensureActiveOperation() {
        if (activeOperation == null) {
            throw new IllegalStateException("No active operation. Start a session first.");
        }
    }

    private void refreshHighlight() {
        highlightVisitor.reset();
        if (activeOperation != null) {
            activeOperation.previewNext(highlightVisitor);
        }
    }

    private void refreshState() {
        if (activeOperation == null) {
            state = PlaybackState.IDLE;
            return;
        }
        if (activeOperation.getTotalStep() == 0) {
            state = PlaybackState.IDLE;
            return;
        }
        boolean finished = activeOperation.getCurrentStep() >= activeOperation.getTotalStep() - 1;
        state = finished ? PlaybackState.IDLE : PlaybackState.IN_PROGRESS;
    }
}
