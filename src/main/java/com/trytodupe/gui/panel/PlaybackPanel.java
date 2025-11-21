package com.trytodupe.gui.panel;

import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.UserOperation;
import imgui.ImGui;

import java.util.List;

/**
 * Shows playback controls and timeline.
 */
public class PlaybackPanel {

    private final UiState state;

    public PlaybackPanel(UiState state) {
        this.state = state;
    }

    public void render() {
        ImGui.begin("Playback");
        if (!state.playbackController.hasActiveOperation()) {
            ImGui.text("No active operation.");
            ImGui.end();
            return;
        }

        UserOperation<?> operation = state.playbackController.getActiveOperation();
        ImGui.text("Status: " + state.playbackController.getState().name());
        ImGui.textWrapped(operation.getDescription());

        if (operation instanceof CompositeUserOperation<?>) {
            CompositeUserOperation<?> composite = (CompositeUserOperation<?>) operation;
            ImGui.separator();
            List<UserOperation<?>> children = composite.getChildOperations();
            int childCount = children.size();
            int activeIdx = childCount == 0 ? -1 : Math.max(0, Math.min(composite.getActiveChildIndex(), childCount - 1));
            int displayIdx = activeIdx < 0 ? 0 : activeIdx + 1;
            ImGui.text(String.format("Sub-Operation: %d / %d", displayIdx, childCount));
            if (activeIdx >= 0 && activeIdx < childCount) {
                UserOperation<?> child = children.get(activeIdx);
                ImGui.textWrapped("Current: " + child.getDescription());
                int childProgress = Math.max(child.getCurrentStep() + 1, 0);
                int childTotal = child.getTotalStep();
                ImGui.text(String.format("Child progress: %d / %d", childProgress, childTotal));
            }
        }

        ImGui.separator();
        if (ImGui.button("Step Back")) {
            if (operation.getCurrentStep() >= 0) {
                state.playbackController.stepBackward();
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Step Forward")) {
            if (operation.getCurrentStep() < operation.getTotalStep() - 1) {
                state.playbackController.stepForward();
            }
        }

        int totalSteps = operation.getTotalStep();
        int[] sliderValue = {operation.getCurrentStep()};
        if (ImGui.sliderInt("Timeline", sliderValue, -1, Math.max(totalSteps - 1, 0))) {
            state.playbackController.jumpTo(sliderValue[0]);
        }

        ImGui.end();
    }
}
