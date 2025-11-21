package com.trytodupe.gui;

import com.trytodupe.Main;
import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.gui.history.OperationHistoryEntry;
import com.trytodupe.gui.history.OperationHistoryManager;
import com.trytodupe.gui.history.OperationHistoryStatus;
import com.trytodupe.gui.playback.PlaybackController;
import com.trytodupe.gui.playback.PlaybackState;
import com.trytodupe.gui.renderer.ArrayStructureRenderer;
import com.trytodupe.gui.renderer.BinaryTreeStructureRenderer;
import com.trytodupe.gui.renderer.StackStructureRenderer;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.user.ArrayInitUserOperation;
import com.trytodupe.operation.array.user.ArrayInsertUserOperation;
import com.trytodupe.operation.stack.user.StackInitUserOperation;
import com.trytodupe.operation.stack.user.StackPopUserOperation;
import com.trytodupe.operation.stack.user.StackPushUserOperation;
import imgui.ImGui;

import java.util.Locale;

public class UIPanelManager {

    private final PlaybackController playbackController;
    private final OperationHistoryManager historyManager;

    private final ArrayStructureRenderer arrayRenderer = new ArrayStructureRenderer();
    private final StackStructureRenderer stackRenderer = new StackStructureRenderer();
    private final BinaryTreeStructureRenderer treeRenderer = new BinaryTreeStructureRenderer();

    private OperationHistoryEntry activeHistoryEntry;

    private int selectedStructureTab = 0;
    private int selectedVisualization = 0;

    private final int[] arraySize = {5};
    private final int[] arrayInsertValue = {10};
    private final int[] arrayInsertIndex = {0};

    private final int[] stackCapacity = {5};
    private final int[] stackValue = {1};

    public UIPanelManager(PlaybackController playbackController, OperationHistoryManager historyManager) {
        this.playbackController = playbackController;
        this.historyManager = historyManager;
    }

    public void render() {
        syncHistoryState();
        renderOperationBuilder();
        renderVisualizationWindow();
        renderPlaybackWindow();
        renderHistoryWindow();
    }

    private void renderOperationBuilder() {
        ImGui.begin("Operation Builder");
        boolean locked = isUiLocked();
        if (locked) {
            ImGui.beginDisabled();
            ImGui.textDisabled("Complete the current playback to build new operations.");
        }

        if (ImGui.beginTabBar("BuilderTabs")) {
            if (ImGui.beginTabItem("Array")) {
                selectedStructureTab = 0;
                renderArrayBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Stack")) {
                selectedStructureTab = 1;
                renderStackBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Tree")) {
                selectedStructureTab = 2;
                ImGui.text("Tree operations coming soon.");
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }

        if (locked) {
            ImGui.endDisabled();
        }

        ImGui.end();
    }

    private void renderArrayBuilder() {
        ImGui.sliderInt("Initial Size", arraySize, 1, 20);
        if (ImGui.button("Initialize Array")) {
            ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
            int[] values = new int[arraySize[0]];
            for (int i = 0; i < values.length; i++) {
                values[i] = i + 1;
            }
            startNewOperation(new ArrayInitUserOperation(array, values));
        }

        ImGui.separator();
        ImGui.sliderInt("Insert Value", arrayInsertValue, -99, 99);
        ImGui.sliderInt("Insert Index", arrayInsertIndex, 0, 20);
        if (ImGui.button("Insert Into Array")) {
            ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
            startNewOperation(new ArrayInsertUserOperation(array, arrayInsertIndex[0], arrayInsertValue[0]));
        }
    }

    private void renderStackBuilder() {
        ImGui.sliderInt("Initial Size", stackCapacity, 0, 10);
        if (ImGui.button("Initialize Stack")) {
            StackStructure stack = Main.getDataStructure(StackStructure.class);
            int[] values = new int[stackCapacity[0]];
            for (int i = 0; i < values.length; i++) {
                values[i] = i + 1;
            }
            startNewOperation(new StackInitUserOperation(stack, values));
        }

        ImGui.separator();
        ImGui.sliderInt("Value", stackValue, -99, 99);
        if (ImGui.button("Push")) {
            StackStructure stack = Main.getDataStructure(StackStructure.class);
            startNewOperation(new StackPushUserOperation(stack, stackValue[0]));
        }
        ImGui.sameLine();
        if (ImGui.button("Pop")) {
            StackStructure stack = Main.getDataStructure(StackStructure.class);
            startNewOperation(new StackPopUserOperation(stack));
        }
    }

    private void renderVisualizationWindow() {
        ImGui.begin("Visualization");

        String[] items = {"Array", "Stack", "Binary Tree"};
        if (ImGui.beginCombo("Structure", items[selectedVisualization])) {
            for (int i = 0; i < items.length; i++) {
                boolean selected = selectedVisualization == i;
                if (ImGui.selectable(items[i], selected)) {
                    selectedVisualization = i;
                }
                if (selected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }

        HighlightInfo highlightInfo = playbackController.getHighlightInfo();

        switch (selectedVisualization) {
            case 0:
                ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
                arrayRenderer.renderContent(array, highlightInfo);
                break;
            case 1:
                StackStructure stack = Main.getDataStructure(StackStructure.class);
                stackRenderer.renderContent(stack, highlightInfo);
                break;
            case 2:
                BinaryTreeStructure<?> tree = Main.getDataStructure(BinaryTreeStructure.class);
                treeRenderer.renderContent(tree, highlightInfo);
                break;
            default:
                ImGui.text("Unsupported visualization.");
                break;
        }

        if (highlightInfo != null && !highlightInfo.isEmpty()) {
            ImGui.separator();
            ImGui.text("Highlight Summary");
            ImGui.bulletText("Array indices: " + highlightInfo.arrayIndices.size());
            ImGui.bulletText("Nodes: " + highlightInfo.nodeUUIDs.size());
            ImGui.bulletText("Arrows: " + highlightInfo.arrows.size());
        }

        ImGui.end();
    }

    private void renderPlaybackWindow() {
        ImGui.begin("Playback");
        if (!playbackController.hasActiveOperation()) {
            ImGui.text("No active operation.");
            ImGui.end();
            return;
        }

        UserOperation<?> operation = playbackController.getActiveOperation();
        ImGui.text("Status: " + playbackController.getState().name());
        ImGui.textWrapped(operation.getDescription());

        ImGui.separator();
        if (ImGui.button("Step Back")) {
            if (operation.getCurrentStep() >= 0) {
                playbackController.stepBackward();
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Step Forward")) {
            if (operation.getCurrentStep() < operation.getTotalStep() - 1) {
                playbackController.stepForward();
            }
        }

        int totalSteps = operation.getTotalStep();
        int[] sliderValue = {operation.getCurrentStep()};
        if (ImGui.sliderInt("Timeline", sliderValue, -1, Math.max(totalSteps - 1, 0))) {
            playbackController.jumpTo(sliderValue[0]);
        }

        ImGui.end();
    }

    private void renderHistoryWindow() {
        ImGui.begin("Operation History");

        boolean disableActions = isUiLocked();

        for (OperationHistoryEntry entry : historyManager.getEntries()) {
            ImGui.separator();
            ImGui.text(entry.getDescription() == null ? "(No description)" : entry.getDescription());
            ImGui.text(String.format(Locale.US, "Status: %s", entry.getStatus()));

            if (disableActions) {
                ImGui.beginDisabled();
            }

            boolean canUndo = entry.getStatus() == OperationHistoryStatus.DONE;
            if (!canUndo) {
                ImGui.beginDisabled();
            }
            if (ImGui.button("Undo##" + entry.getId())) {
                historyManager.undo(entry);
                playbackController.stop();
                activeHistoryEntry = null;
            }
            if (!canUndo) {
                ImGui.endDisabled();
            }

            ImGui.sameLine();

            boolean canRedo = entry.getStatus() == OperationHistoryStatus.UNDONE;
            if (!canRedo) {
                ImGui.beginDisabled();
            }
            if (ImGui.button("Redo##" + entry.getId())) {
                historyManager.redo(entry);
            }
            if (!canRedo) {
                ImGui.endDisabled();
            }

            ImGui.sameLine();

            boolean canVisualize = entry.getStatus() == OperationHistoryStatus.UNDONE;
            if (!canVisualize) {
                ImGui.beginDisabled();
            }
            if (ImGui.button("Visualize##" + entry.getId())) {
                visualizeEntry(entry);
            }
            if (!canVisualize) {
                ImGui.endDisabled();
            }

            if (disableActions) {
                ImGui.endDisabled();
            }
        }

        ImGui.end();
    }

    private void startNewOperation(UserOperation<?> operation) {
        if (operation == null) {
            return;
        }

        OperationHistoryEntry entry = historyManager.add(operation);
        activeHistoryEntry = entry;
        playbackController.start(operation);
    }

    private void visualizeEntry(OperationHistoryEntry entry) {
        if (entry == null) {
            return;
        }
        activeHistoryEntry = entry;
        entry.markInProgress();
        playbackController.start(entry.getOperation());
    }

    private void syncHistoryState() {
        if (!playbackController.hasActiveOperation()) {
            activeHistoryEntry = null;
            return;
        }

        if (activeHistoryEntry == null) {
            activeHistoryEntry = historyManager.getEntries().stream()
                .filter(e -> e.getOperation() == playbackController.getActiveOperation())
                .findFirst()
                .orElse(null);
        }

        if (activeHistoryEntry == null) {
            return;
        }

        if (playbackController.getState() == PlaybackState.IN_PROGRESS) {
            activeHistoryEntry.markInProgress();
        } else {
            UserOperation<?> op = playbackController.getActiveOperation();
            if (op.getTotalStep() == 0 || op.getCurrentStep() >= op.getTotalStep() - 1) {
                historyManager.markDone(activeHistoryEntry);
            } else {
                activeHistoryEntry.markIdle();
            }
        }
    }

    private boolean isUiLocked() {
        return playbackController.getState() == PlaybackState.IN_PROGRESS;
    }
}
