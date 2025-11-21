package com.trytodupe.gui;

import com.trytodupe.Main;
import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.DataStructure;
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
import com.trytodupe.operation.CompositeUserOperation;
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

import java.util.List;
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
    private Class<? extends DataStructure> activeVisualizationClass = ArrayStructure.class;

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
                    array.clear();
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
                    stack.clear();
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
                tree.clear();
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
            } else if (parentUUID == null && tree.getRoot() != null) {
                setBuilderError("Root already exists; select a parent node.");
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
            if (!ensureTreeNotEmpty(tree)) {
                // message set
            } else if (childUUID == null) {
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
            if (!ensureTreeNotEmpty(tree)) {
                // handled
            } else if (nodeUUID == null) {
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
                bst.clear();
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
            if (!ensureTreeNotEmpty(bst)) {
                // handled
            } else if (uuid == null) {
                setBuilderError("Node UUID is required for BST deletion.");
            } else if (ensureTreeNodeExists(bst, uuid, "Node UUID")) {
                startNewOperation(new BinarySearchTreeDeleteUserOperation<>(bst, uuid));
            }
        }
    }

    private void renderVisualizationWindow() {
        ImGui.begin("Visualization");

        HighlightInfo highlightInfo = playbackController.getHighlightInfo();
        if (activeVisualizationClass == null) {
            ImGui.text("No data structure selected.");
            ImGui.end();
            return;
        }

        if (BinarySearchTreeStructure.class.isAssignableFrom(activeVisualizationClass)) {
            BinarySearchTreeStructure<?> bst = Main.getDataStructure(BinarySearchTreeStructure.class);
            treeRenderer.renderContent(bst, highlightInfo);
        } else if (BinaryTreeStructure.class.isAssignableFrom(activeVisualizationClass)) {
            BinaryTreeStructure<?> tree = Main.getDataStructure(BinaryTreeStructure.class);
            treeRenderer.renderContent(tree, highlightInfo);
        } else if (StackStructure.class.isAssignableFrom(activeVisualizationClass)) {
            StackStructure stack = Main.getDataStructure(StackStructure.class);
            stackRenderer.renderContent(stack, highlightInfo);
        } else if (ArrayStructure.class.isAssignableFrom(activeVisualizationClass)) {
            ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
            arrayRenderer.renderContent(array, highlightInfo);
        } else {
            ImGui.text("Unsupported visualization target: " + activeVisualizationClass.getSimpleName());
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

        if (operation instanceof CompositeUserOperation<?>) {
            CompositeUserOperation<?> composite = (CompositeUserOperation<?>) operation;
            ImGui.separator();
            List<UserOperation<?>> children = composite.getChildOperations();
            int childCount = children.size();
            int activeIdx = childCount == 0 ? -1 : Math.max(0, Math.min(composite.getActiveChildIndex(), childCount - 1));
            int displayIdx = (activeIdx < 0) ? 0 : activeIdx + 1;
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

        if (disableActions) {
            ImGui.beginDisabled();
        }
        if (!historyManager.canUndo()) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("Undo##Global")) {
            OperationHistoryEntry undone = historyManager.undoLatest();
            if (undone != null) {
                playbackController.stop();
                activeHistoryEntry = null;
                activeVisualizationClass = undone.getOperation().getDataStructure().getClass();
            }
        }
        if (!historyManager.canUndo()) {
            ImGui.endDisabled();
        }
        ImGui.sameLine();
        if (!historyManager.canRedo()) {
            ImGui.beginDisabled();
        }
        if (ImGui.button("Redo##Global")) {
            OperationHistoryEntry redone = historyManager.redoNext();
            if (redone != null) {
                playbackController.stop();
                activeHistoryEntry = null;
                activeVisualizationClass = redone.getOperation().getDataStructure().getClass();
            }
        }
        if (!historyManager.canRedo()) {
            ImGui.endDisabled();
        }
        if (disableActions) {
            ImGui.endDisabled();
        }

        ImGui.separator();

        List<OperationHistoryEntry> entries = historyManager.getEntries();
        for (int idx = entries.size() - 1; idx >= 0; idx--) {
            OperationHistoryEntry entry = entries.get(idx);
            ImGui.separator();
            ImGui.text(entry.getDescription() == null ? "(No description)" : entry.getDescription());
            ImGui.text(String.format(Locale.US, "Status: %s", entry.getStatus()));

            boolean canVisualize = entry.getStatus() != OperationHistoryStatus.IN_PROGRESS;
            if (!canVisualize) {
                ImGui.beginDisabled();
            }
            if (ImGui.button("Visualize##" + entry.getId())) {
                visualizeEntry(entry);
            }
            if (!canVisualize) {
                ImGui.endDisabled();
            }
            ImGui.separator();
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
        activeVisualizationClass = operation.getDataStructure().getClass();
        playbackController.start(operation);
    }

    private void visualizeEntry(OperationHistoryEntry entry) {
        if (entry == null) {
            return;
        }
        activeHistoryEntry = entry;
        activeVisualizationClass = entry.getOperation().getDataStructure().getClass();
        playbackController.start(entry.getOperation());
    }

    private void syncHistoryState() {
        OperationHistoryEntry tracked = historyManager.getInProgressEntry();
        if (tracked == null) {
            return;
        }

        if (playbackController.getState() == PlaybackState.IN_PROGRESS) {
            tracked.markInProgress();
            return;
        }

        UserOperation<?> op = playbackController.getActiveOperation();
        if (op != null && op.getTotalStep() > 0 && op.getCurrentStep() >= op.getTotalStep() - 1) {
            historyManager.markDone(tracked);
        } else {
            historyManager.markIdle(tracked);
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

    private boolean ensureTreeNotEmpty(BinaryTreeStructure<?> tree) {
        if (tree.getRoot() == null) {
            setBuilderError("Tree is empty.");
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
