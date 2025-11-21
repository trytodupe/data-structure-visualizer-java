package com.trytodupe.gui;

import com.trytodupe.Main;
import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.gui.history.OperationHistoryEntry;
import com.trytodupe.gui.history.OperationHistoryManager;
import com.trytodupe.gui.history.OperationHistoryStatus;
import com.trytodupe.gui.playback.PlaybackController;
import com.trytodupe.gui.playback.PlaybackState;
import com.trytodupe.gui.renderer.ArrayStructureRenderer;
import com.trytodupe.gui.renderer.BinaryTreeStructureRenderer;
import com.trytodupe.gui.renderer.StackStructureRenderer;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.user.ArrayDeleteUserOperation;
import com.trytodupe.operation.array.user.ArrayDeleteUserOperation;
import com.trytodupe.operation.array.user.ArrayInitUserOperation;
import com.trytodupe.operation.array.user.ArrayInsertUserOperation;
import com.trytodupe.operation.binarysearchtree.composite.BinarySearchTreeInitCompositeOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeDeleteUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;
import com.trytodupe.operation.binarytree.composite.BinaryTreeInitCompositeOperation;
import com.trytodupe.operation.binarytree.user.BinaryTreeDeleteUserOperation;
import com.trytodupe.operation.binarytree.user.BinaryTreeInsertUserOperation;
import com.trytodupe.operation.binarytree.user.BinaryTreeUpdateValueUserOperation;
import com.trytodupe.operation.stack.user.StackInitUserOperation;
import com.trytodupe.operation.stack.user.StackPopUserOperation;
import com.trytodupe.operation.stack.user.StackPushUserOperation;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.Locale;
import java.util.UUID;

public class UIPanelManager {

    private final PlaybackController playbackController;
    private final OperationHistoryManager historyManager;

    private final ArrayStructureRenderer arrayRenderer = new ArrayStructureRenderer();
    private final StackStructureRenderer stackRenderer = new StackStructureRenderer();
    private final BinaryTreeStructureRenderer treeRenderer = new BinaryTreeStructureRenderer();
    private final TreeNodePicker nodePicker = new TreeNodePicker();

    private OperationHistoryEntry activeHistoryEntry;

    private int selectedStructureTab = 0;
    private int selectedVisualization = 0;

    private final int[] arrayInsertValue = {10};
    private final int[] arrayInsertIndex = {0};
    private final int[] arrayDeleteIndex = {0};

    private final int[] stackValue = {1};
    private final ImString arrayInitInput = new ImString(128);
    private final ImString stackInitInput = new ImString(128);
    private final ImString binaryTreeInitInput = new ImString(256);
    private final ImString binaryTreeInsertParent = new ImString(64);
    private final ImInt binaryTreeInsertValue = new ImInt(0);
    private final ImInt binaryTreeChildTypeSelection = new ImInt(0);
    private final ImString binaryTreeDeleteParent = new ImString(64);
    private final ImString binaryTreeDeleteNode = new ImString(64);
    private final ImString binaryTreeUpdateNode = new ImString(64);
    private final ImInt binaryTreeUpdateValue = new ImInt(0);

    private final ImString bstInitInput = new ImString(256);
    private final ImInt bstInsertValue = new ImInt(0);
    private final ImString bstDeleteUuid = new ImString(64);

    private String builderErrorMessage = "";

    private static final String[] CHILD_TYPE_LABELS = {"LEFT", "RIGHT"};

    public UIPanelManager(PlaybackController playbackController, OperationHistoryManager historyManager) {
        this.playbackController = playbackController;
        this.historyManager = historyManager;
        this.treeRenderer.setNodePicker(nodePicker);
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
                renderTreeBuilder();
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }

        if (!builderErrorMessage.isEmpty()) {
            ImGui.separator();
            ImGui.textColored(0xFFCC3333, builderErrorMessage);
        }

        if (locked) {
            ImGui.endDisabled();
        }

        ImGui.end();
    }

    private void renderArrayBuilder() {
        ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
        ImGui.inputTextWithHint("Initial Values", "e.g. 1,2,3", arrayInitInput);
        if (ImGui.button("Initialize Array")) {
            int[] values = parseCommaSeparatedInts(arrayInitInput.get(), "Array initial values");
            if (values != null) {
                if (values.length > array.capacity()) {
                    setBuilderError("Array initial values exceed capacity (" + array.capacity() + ").");
                } else {
                    startNewOperation(new ArrayInitUserOperation(array, values));
                }
            }
        }

        ImGui.separator();
        int insertMax = Math.max(array.getSize(), 0);
        ImGui.sliderInt("Insert Value", arrayInsertValue, -99, 99);
        ImGui.sliderInt("Insert Index", arrayInsertIndex, 0, insertMax);
        if (ImGui.button("Insert Into Array")) {
            if (ensureArrayCanInsert(array, arrayInsertIndex[0]) && ensureArrayHasCapacity(array)) {
                startNewOperation(new ArrayInsertUserOperation(array, arrayInsertIndex[0], arrayInsertValue[0]));
            }
        }

        ImGui.separator();
        int deleteMax = Math.max(array.getSize() - 1, 0);
        ImGui.sliderInt("Delete Index", arrayDeleteIndex, 0, Math.max(deleteMax, 0));
        if (ImGui.button("Delete From Array")) {
            if (ensureArrayCanDelete(array, arrayDeleteIndex[0])) {
                startNewOperation(new ArrayDeleteUserOperation(array, arrayDeleteIndex[0]));
            }
        }
    }

    private void renderStackBuilder() {
        StackStructure stack = Main.getDataStructure(StackStructure.class);
        ImGui.inputTextWithHint("Initial Values", "e.g. 5,4,3", stackInitInput);
        if (ImGui.button("Initialize Stack")) {
            int[] values = parseCommaSeparatedInts(stackInitInput.get(), "Stack initial values");
            if (values != null) {
                if (values.length > stack.capacity()) {
                    setBuilderError("Stack initial values exceed capacity (" + stack.capacity() + ").");
                } else {
                    startNewOperation(new StackInitUserOperation(stack, values));
                }
            }
        }

        ImGui.separator();
        ImGui.sliderInt("Value", stackValue, -99, 99);
        if (ImGui.button("Push")) {
            if (ensureStackHasCapacity(stack)) {
                startNewOperation(new StackPushUserOperation(stack, stackValue[0]));
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Pop")) {
            if (ensureStackNotEmpty(stack)) {
                startNewOperation(new StackPopUserOperation(stack));
            }
        }
    }

    private void renderTreeBuilder() {
        if (ImGui.beginTabBar("TreeBuilderTabs")) {
            if (ImGui.beginTabItem("Binary Tree")) {
                renderBinaryTreeBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("BST")) {
                renderBstBuilder();
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
    }

    private void renderBinaryTreeBuilder() {
        BinaryTreeStructure<Integer> tree = Main.getDataStructure(BinaryTreeStructure.class);

        ImGui.text("Initialize (level-order, use 'null' for gaps)");
        ImGui.inputTextWithHint("Values##BinaryTreeInit", "e.g. 1,2,3,null,5", binaryTreeInitInput);
        if (ImGui.button("Init Binary Tree")) {
            Integer[] values = parseNullableIntegerArray(binaryTreeInitInput.get(), "Binary tree values");
            if (values != null) {
                startNewOperation(new BinaryTreeInitCompositeOperation<>(tree, values));
            }
        }

        ImGui.separator();

        renderNodePickerRow("Parent Node (blank = root)", binaryTreeInsertParent, "binary-insert-parent");
        ImGui.inputInt("Value##BinaryTreeInsertValue", binaryTreeInsertValue);
        ImGui.combo("Child Type", binaryTreeChildTypeSelection, CHILD_TYPE_LABELS);
        if (ImGui.button("Insert Node")) {
            String parentUUID = trimToNull(binaryTreeInsertParent.get());
            Integer value = binaryTreeInsertValue.get();

            if (parentUUID != null && !ensureTreeNodeExists(tree, parentUUID, "Parent UUID")) {
                // error already set
            } else {
                BinaryTreeNode.ChildType childType = parentUUID == null ? null
                    : (binaryTreeChildTypeSelection.get() == 0
                        ? BinaryTreeNode.ChildType.LEFT
                        : BinaryTreeNode.ChildType.RIGHT);
                startNewOperation(new BinaryTreeInsertUserOperation<>(tree, parentUUID, value, childType));
            }
        }

        ImGui.separator();
        renderNodePickerRow("Child Node", binaryTreeDeleteNode, "binary-delete-child");
        renderNodePickerRow("Parent Node (optional)", binaryTreeDeleteParent, "binary-delete-parent");
        if (ImGui.button("Delete Node")) {
            String childUUID = trimToNull(binaryTreeDeleteNode.get());
            if (childUUID == null) {
                setBuilderError("Child UUID is required for deletion.");
            } else if (ensureTreeNodeExists(tree, childUUID, "Child UUID")) {
                String parentUUID = trimToNull(binaryTreeDeleteParent.get());
                if (parentUUID != null) {
                    if (!ensureTreeNodeExists(tree, parentUUID, "Parent UUID")) {
                        return;
                    }
                } else {
                    BinaryTreeNode<Integer> parentNode = tree.getParent(UUID.fromString(childUUID));
                    parentUUID = parentNode == null ? null : parentNode.getUUID().toString();
                }
                startNewOperation(new BinaryTreeDeleteUserOperation<>(tree, parentUUID, childUUID));
            }
        }

        ImGui.separator();
        renderNodePickerRow("Node", binaryTreeUpdateNode, "binary-update-node");
        ImGui.inputInt("New Value", binaryTreeUpdateValue);
        if (ImGui.button("Update Value")) {
            String nodeUUID = trimToNull(binaryTreeUpdateNode.get());
            if (nodeUUID == null) {
                setBuilderError("Node UUID is required to update value.");
            } else if (ensureTreeNodeExists(tree, nodeUUID, "Node UUID")) {
                startNewOperation(new BinaryTreeUpdateValueUserOperation<>(tree, nodeUUID, binaryTreeUpdateValue.get()));
            }
        }
    }

    private void renderBstBuilder() {
        BinarySearchTreeStructure<Integer> bst = Main.getDataStructure(BinarySearchTreeStructure.class);

        ImGui.text("Initialize BST (insert order)");
        ImGui.inputTextWithHint("Values##BSTInit", "e.g. 10,4,15,2", bstInitInput);
        if (ImGui.button("Init BST")) {
            int[] raw = parseCommaSeparatedInts(bstInitInput.get(), "BST initial values");
            if (raw != null) {
                Integer[] values = boxIntegers(raw);
                startNewOperation(new BinarySearchTreeInitCompositeOperation<>(bst, values));
            }
        }

        ImGui.separator();
        ImGui.inputInt("Value##BSTInsert", bstInsertValue);
        if (ImGui.button("Insert Into BST")) {
            startNewOperation(new BinarySearchTreeInsertUserOperation<>(bst, bstInsertValue.get()));
        }

        ImGui.separator();
        renderNodePickerRow("BST Node", bstDeleteUuid, "bst-delete-node");
        if (ImGui.button("Delete From BST")) {
            String uuid = trimToNull(bstDeleteUuid.get());
            if (uuid == null) {
                setBuilderError("Node UUID is required for BST deletion.");
            } else if (ensureTreeNodeExists(bst, uuid, "Node UUID")) {
                startNewOperation(new BinarySearchTreeDeleteUserOperation<>(bst, uuid));
            }
        }
    }

    private void renderVisualizationWindow() {
        ImGui.begin("Visualization");

        String[] items = {"Array", "Stack", "Binary Tree", "BST"};
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
            case 3:
                BinarySearchTreeStructure<?> bst = Main.getDataStructure(BinarySearchTreeStructure.class);
                treeRenderer.renderContent(bst, highlightInfo);
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

        clearBuilderError();
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

    private void setBuilderError(String message) {
        this.builderErrorMessage = message;
    }

    private void clearBuilderError() {
        this.builderErrorMessage = "";
    }

    private int[] parseCommaSeparatedInts(String raw, String contextLabel) {
        if (raw == null || raw.trim().isEmpty()) {
            return new int[0];
        }

        String[] tokens = raw.split(",");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty()) {
                setBuilderError(contextLabel + ": empty value at position " + (i + 1));
                return null;
            }
            try {
                values[i] = Integer.parseInt(token);
            } catch (NumberFormatException ex) {
                setBuilderError(contextLabel + ": '" + token + "' is not a valid integer.");
                return null;
            }
        }
        return values;
    }

    private boolean ensureArrayHasCapacity(ArrayStructure array) {
        if (array.getSize() >= array.capacity()) {
            setBuilderError("Array is full (capacity " + array.capacity() + ").");
            return false;
        }
        return true;
    }

    private boolean ensureArrayCanInsert(ArrayStructure array, int index) {
        int size = array.getSize();
        if (index < 0 || index > size) {
            setBuilderError("Insert index must be between 0 and " + size + ".");
            return false;
        }
        return true;
    }

    private boolean ensureArrayCanDelete(ArrayStructure array, int index) {
        int size = array.getSize();
        if (size == 0) {
            setBuilderError("Array is empty; nothing to delete.");
            return false;
        }
        if (index < 0 || index >= size) {
            setBuilderError("Delete index must be between 0 and " + (size - 1) + ".");
            return false;
        }
        return true;
    }

    private boolean ensureStackHasCapacity(StackStructure stack) {
        if (stack.size() >= stack.capacity()) {
            setBuilderError("Stack is full (capacity " + stack.capacity() + ").");
            return false;
        }
        return true;
    }

    private boolean ensureStackNotEmpty(StackStructure stack) {
        if (stack.size() <= 0) {
            setBuilderError("Stack is empty; cannot pop.");
            return false;
        }
        return true;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer[] parseNullableIntegerArray(String raw, String contextLabel) {
        if (raw == null || raw.trim().isEmpty()) {
            return new Integer[0];
        }
        String[] tokens = raw.split(",");
        Integer[] result = new Integer[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty()) {
                setBuilderError(contextLabel + ": empty value at position " + (i + 1));
                return null;
            }
            if ("null".equalsIgnoreCase(token)) {
                result[i] = null;
            } else {
                try {
                    result[i] = Integer.parseInt(token);
                } catch (NumberFormatException ex) {
                    setBuilderError(contextLabel + ": '" + token + "' is not a valid integer.");
                    return null;
                }
            }
        }
        return result;
    }

    private Integer[] boxIntegers(int[] raw) {
        Integer[] result = new Integer[raw.length];
        for (int i = 0; i < raw.length; i++) {
            result[i] = raw[i];
        }
        return result;
    }

    private boolean ensureTreeNodeExists(BinaryTreeStructure<?> tree, String uuid, String fieldLabel) {
        UUID parsed = parseUuid(uuid, fieldLabel);
        if (parsed == null) {
            return false;
        }
        if (tree.getNode(parsed) == null) {
            setBuilderError(fieldLabel + " does not exist in the current tree.");
            return false;
        }
        return true;
    }

    private UUID parseUuid(String uuid, String fieldLabel) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException ex) {
            setBuilderError(fieldLabel + " is not a valid UUID.");
            return null;
        }
    }

    private void renderNodePickerRow(String label, ImString storage, String contextKey) {
        String current = trimToNull(storage.get());
        ImGui.text(label + ": " + (current == null ? "(none)" : current));
        ImGui.sameLine();
        if (nodePicker.isPicking(contextKey)) {
            if (ImGui.button("Cancel##" + contextKey)) {
                nodePicker.cancel();
            }
            ImGui.sameLine();
            ImGui.textDisabled("Click a node...");
        } else {
            if (ImGui.button("Pick##" + contextKey)) {
                nodePicker.begin(contextKey, uuid -> {
                    storage.set(uuid);
                    clearBuilderError();
                });
            }
        }
    }
}
