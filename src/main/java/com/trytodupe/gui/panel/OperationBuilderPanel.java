package com.trytodupe.gui.panel;

import com.trytodupe.Main;
import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.user.ArrayDeleteUserOperation;
import com.trytodupe.operation.array.user.ArrayInitUserOperation;
import com.trytodupe.operation.array.user.ArrayInsertUserOperation;
import com.trytodupe.operation.avltree.composite.AVLTreeInitCompositeOperation;
import com.trytodupe.operation.binarysearchtree.composite.BinarySearchTreeInitCompositeOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeDeleteUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;
import com.trytodupe.operation.binarytree.composite.BinaryTreeInitCompositeOperation;
import com.trytodupe.operation.binarytree.user.BinaryTreeDeleteUserOperation;
import com.trytodupe.operation.binarytree.user.BinaryTreeInsertUserOperation;
import com.trytodupe.operation.binarytree.user.BinaryTreeUpdateValueUserOperation;
import com.trytodupe.operation.huffmantree.composite.HuffmanTreeInitCompositeOperation;
import com.trytodupe.operation.linkedlist.user.LinkedListDeleteUserOperation;
import com.trytodupe.operation.linkedlist.user.LinkedListInitUserOperation;
import com.trytodupe.operation.linkedlist.user.LinkedListInsertUserOperation;
import com.trytodupe.operation.stack.user.StackInitUserOperation;
import com.trytodupe.operation.stack.user.StackPopUserOperation;
import com.trytodupe.operation.stack.user.StackPushUserOperation;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.UUID;

/**
 * Responsible only for rendering the builder UI and validating inputs.
 * Delegates execution/visualization to {@link UiState}.
 */
public class OperationBuilderPanel {

    private final UiState state;

    private final int[] arrayInsertValue = {10};
    private final int[] arrayInsertIndex = {0};
    private final int[] arrayDeleteIndex = {0};

    private final int[] stackValue = {1};
    private final ImString arrayInitInput = new ImString(128);
    private final ImString stackInitInput = new ImString(128);
    private final ImString linkedListInitInput = new ImString(256);
    private final int[] linkedListInsertValue = {0};
    private final int[] linkedListInsertIndex = {0};
    private final int[] linkedListDeleteIndex = {0};
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
    private final ImString avlInitInput = new ImString(256);
    private final ImString huffmanInitInput = new ImString(256);

    private String builderErrorMessage = "";

    private static final String[] CHILD_TYPE_LABELS = {"LEFT", "RIGHT"};

    public OperationBuilderPanel(UiState state) {
        this.state = state;
    }

    public void render() {
        ImGui.begin("Operation Builder");
        boolean locked = state.playbackController.getState() == com.trytodupe.gui.playback.PlaybackState.IN_PROGRESS;
        if (locked) {
            ImGui.beginDisabled();
            ImGui.textDisabled("Complete the current playback to build new operations.");
        }

        if (ImGui.beginTabBar("BuilderTabs")) {
            if (ImGui.beginTabItem("Array")) {
                state.setActiveVisualizationClass(ArrayStructure.class);
                renderArrayBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Stack")) {
                state.setActiveVisualizationClass(StackStructure.class);
                renderStackBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Linked List")) {
                state.setActiveVisualizationClass(LinkedListStructure.class);
                renderLinkedListBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Tree")) {
                renderTreeTabs();
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

    private void renderTreeTabs() {
        if (ImGui.beginTabBar("TreeBuilderTabs")) {
            if (ImGui.beginTabItem("Binary Tree")) {
                state.setActiveVisualizationClass(BinaryTreeStructure.class);
                renderBinaryTreeBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("BST")) {
                state.setActiveVisualizationClass(BinarySearchTreeStructure.class);
                renderBstBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("AVL Tree")) {
                state.setActiveVisualizationClass(AVLTreeStructure.class);
                renderAvlBuilder();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Huffman Tree")) {
                state.setActiveVisualizationClass(HuffmanTreeStructure.class);
                renderHuffmanBuilder();
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
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
                    state.resetHistoryState();
                    state.startNewOperation(new ArrayInitUserOperation(array, values));
                    clearBuilderError();
                }
            }
        }

        ImGui.separator();
        int insertMax = Math.max(array.getSize(), 0);
        ImGui.sliderInt("Insert Value", arrayInsertValue, -99, 99);
        ImGui.sliderInt("Insert Index", arrayInsertIndex, 0, insertMax);
        if (ImGui.button("Insert Into Array")) {
            if (ensureArrayCanInsert(array, arrayInsertIndex[0]) && ensureArrayHasCapacity(array)) {
                state.startNewOperation(new ArrayInsertUserOperation(array, arrayInsertIndex[0], arrayInsertValue[0]));
                clearBuilderError();
            }
        }

        ImGui.separator();
        int deleteMax = Math.max(array.getSize() - 1, 0);
        ImGui.sliderInt("Delete Index", arrayDeleteIndex, 0, Math.max(deleteMax, 0));
        if (ImGui.button("Delete From Array")) {
            if (ensureArrayCanDelete(array, arrayDeleteIndex[0])) {
                state.startNewOperation(new ArrayDeleteUserOperation(array, arrayDeleteIndex[0]));
                clearBuilderError();
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
                    state.resetHistoryState();
                    state.startNewOperation(new StackInitUserOperation(stack, values));
                    clearBuilderError();
                }
            }
        }

        ImGui.separator();
        ImGui.sliderInt("Value", stackValue, -99, 99);
        if (ImGui.button("Push")) {
            if (ensureStackHasCapacity(stack)) {
                state.startNewOperation(new StackPushUserOperation(stack, stackValue[0]));
                clearBuilderError();
            }
        }
        ImGui.sameLine();
        if (ImGui.button("Pop")) {
            if (ensureStackNotEmpty(stack)) {
                state.startNewOperation(new StackPopUserOperation(stack));
                clearBuilderError();
            }
        }
    }

    private void renderLinkedListBuilder() {
        LinkedListStructure list = Main.getDataStructure(LinkedListStructure.class);
        ImGui.inputTextWithHint("Initial Values##LinkedList", "e.g. 1,2,3", linkedListInitInput);
        if (ImGui.button("Initialize Linked List")) {
            int[] values = parseCommaSeparatedInts(linkedListInitInput.get(), "Linked list initial values");
            if (values != null) {
                state.resetHistoryState();
                state.startNewOperation(new LinkedListInitUserOperation(list, values));
                clearBuilderError();
            }
        }

        ImGui.separator();
        linkedListInsertIndex[0] = Math.max(0, Math.min(linkedListInsertIndex[0], list.size()));
        ImGui.sliderInt("Insert Index##LinkedList", linkedListInsertIndex, 0, Math.max(list.size(), 0));
        ImGui.sliderInt("Value##LinkedListInsert", linkedListInsertValue, -999, 999);
        if (ImGui.button("Insert Node")) {
            if (ensureLinkedListInsertIndex(list, linkedListInsertIndex[0])) {
                state.startNewOperation(new LinkedListInsertUserOperation(list, linkedListInsertIndex[0], linkedListInsertValue[0]));
                clearBuilderError();
            }
        }

        ImGui.separator();
        if (list.size() == 0) {
            linkedListDeleteIndex[0] = 0;
        } else {
            linkedListDeleteIndex[0] = Math.max(0, Math.min(linkedListDeleteIndex[0], list.size() - 1));
        }
        ImGui.sliderInt("Delete Index##LinkedList", linkedListDeleteIndex, 0, Math.max(list.size() - 1, 0));
        if (ImGui.button("Delete Node")) {
            if (ensureLinkedListDeleteIndex(list, linkedListDeleteIndex[0])) {
                state.startNewOperation(new LinkedListDeleteUserOperation(list, linkedListDeleteIndex[0]));
                clearBuilderError();
            }
        }
    }

    private void renderBinaryTreeBuilder() {
        BinaryTreeStructure<Integer> tree = Main.getDataStructure(BinaryTreeStructure.class);

        ImGui.text("Initialize (level-order, use 'null' for gaps)");
        ImGui.inputTextWithHint("Values##BinaryTreeInit", "e.g. 1,2,3,null,5", binaryTreeInitInput);
        if (ImGui.button("Init Binary Tree")) {
            Integer[] values = parseNullableIntegerArray(binaryTreeInitInput.get(), "Binary tree values");
            if (values != null) {
                state.resetHistoryState();
                state.startNewOperation(new BinaryTreeInitCompositeOperation<>(tree, values));
                clearBuilderError();
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
                // handled
            } else if (parentUUID == null && tree.getRoot() != null) {
                setBuilderError("Root already exists; select a parent node.");
            } else {
                BinaryTreeNode.ChildType childType = parentUUID == null ? null
                    : (binaryTreeChildTypeSelection.get() == 0
                        ? BinaryTreeNode.ChildType.LEFT
                        : BinaryTreeNode.ChildType.RIGHT);
                if (parentUUID == null || ensureChildSlotAvailable(tree, parentUUID, childType)) {
                    state.startNewOperation(new BinaryTreeInsertUserOperation<>(tree, parentUUID, value, childType));
                    clearBuilderError();
                }
            }
        }

        ImGui.separator();
        renderNodePickerRow("Child Node", binaryTreeDeleteNode, "binary-delete-child");
        renderNodePickerRow("Parent Node (optional)", binaryTreeDeleteParent, "binary-delete-parent");
        if (ImGui.button("Delete Node")) {
            String childUUID = trimToNull(binaryTreeDeleteNode.get());
            if (!ensureTreeNotEmpty(tree)) {
                // handled
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
                state.startNewOperation(new BinaryTreeDeleteUserOperation<>(tree, parentUUID, childUUID));
                clearBuilderError();
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
                state.startNewOperation(new BinaryTreeUpdateValueUserOperation<>(tree, nodeUUID, binaryTreeUpdateValue.get()));
                clearBuilderError();
            }
        }
    }

    private void renderBstBuilder() {
        BinarySearchTreeStructure<Integer> bst = Main.getDataStructure(BinarySearchTreeStructure.class);

        ImGui.inputTextWithHint("Values##BSTInit", "e.g. 10,4,15,2", bstInitInput);
        if (ImGui.button("Init BST")) {
            int[] raw = parseCommaSeparatedInts(bstInitInput.get(), "BST initial values");
            if (raw != null) {
                Integer[] values = boxIntegers(raw);
                state.resetHistoryState();
                state.startNewOperation(new BinarySearchTreeInitCompositeOperation<>(bst, values));
                clearBuilderError();
            }
        }

        ImGui.separator();
        ImGui.inputInt("Value##BSTInsert", bstInsertValue);
        if (ImGui.button("Insert Into BST")) {
            state.startNewOperation(new BinarySearchTreeInsertUserOperation<>(bst, bstInsertValue.get()));
            clearBuilderError();
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
                state.startNewOperation(new BinarySearchTreeDeleteUserOperation<>(bst, uuid));
                clearBuilderError();
            }
        }
    }

    private void renderAvlBuilder() {
        AVLTreeStructure<Integer> avl = Main.getDataStructure(AVLTreeStructure.class);
        ImGui.inputTextWithHint("Values##AVLInit", "e.g. 10,4,15,2", avlInitInput);
        if (ImGui.button("Init AVL Tree")) {
            int[] raw = parseCommaSeparatedInts(avlInitInput.get(), "AVL initial values");
            if (raw != null) {
                Integer[] boxed = boxIntegers(raw);
                state.resetHistoryState();
                state.startNewOperation(new AVLTreeInitCompositeOperation<>(avl, boxed));
                clearBuilderError();
            }
        }
    }

    private void renderHuffmanBuilder() {
        HuffmanTreeStructure<Character> huffman = Main.getDataStructure(HuffmanTreeStructure.class);
        ImGui.inputTextWithHint("String##Huffman", "e.g. data structure", huffmanInitInput);
        if (ImGui.button("Build Huffman Tree")) {
            String input = huffmanInitInput.get().trim();
            if (input.isEmpty()) {
                setBuilderError("Input string cannot be empty.");
            } else {
                state.resetHistoryState();
                state.startNewOperation(new HuffmanTreeInitCompositeOperation<>(huffman, input));
                clearBuilderError();
            }
        }
    }

    private void renderNodePickerRow(String label, ImString storage, String contextKey) {
        String current = trimToNull(storage.get());
        ImGui.text(label + ": " + (current == null ? "(none)" : current));
        ImGui.sameLine();
        if (state.nodePicker.isPicking(contextKey)) {
            if (ImGui.button("Cancel##" + contextKey)) {
                state.nodePicker.cancel();
            }
            ImGui.sameLine();
            ImGui.textDisabled("Click a node...");
        } else {
            if (ImGui.button("Pick##" + contextKey)) {
                state.nodePicker.begin(contextKey, uuid -> {
                    storage.set(uuid);
                    clearBuilderError();
                });
            }
        }
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

    private boolean ensureTreeNotEmpty(BinaryTreeStructure<?> tree) {
        if (tree.getRoot() == null) {
            setBuilderError("Tree is empty.");
            return false;
        }
        return true;
    }

    private boolean ensureChildSlotAvailable(BinaryTreeStructure<?> tree, String parentUUID, BinaryTreeNode.ChildType childType) {
        if (childType == null) {
            setBuilderError("Select child type (Left or Right).");
            return false;
        }
        UUID parsed = parseUuid(parentUUID, "Parent UUID");
        if (parsed == null) {
            return false;
        }
        BinaryTreeNode<?> parent = tree.getNode(parsed);

        if (parent == null) {
            setBuilderError("Parent UUID does not exist.");
            return false;
        }
        BinaryTreeNode<?> existing = childType == BinaryTreeNode.ChildType.LEFT ? parent.getLeft() : parent.getRight();
        if (existing != null) {
            setBuilderError("Selected parent already has a " + childType.name().toLowerCase() + " child.");
            return false;
        }
        return true;
    }

    private boolean ensureLinkedListInsertIndex(LinkedListStructure list, int index) {
        if (index < 0 || index > list.size()) {
            setBuilderError("Insert index must be between 0 and " + list.size());
            return false;
        }
        return true;
    }

    private boolean ensureLinkedListDeleteIndex(LinkedListStructure list, int index) {
        if (list.size() == 0) {
            setBuilderError("Linked list is empty; nothing to delete.");
            return false;
        }
        if (index < 0 || index >= list.size()) {
            setBuilderError("Delete index must be between 0 and " + (list.size() - 1));
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void setBuilderError(String message) {
        this.builderErrorMessage = message;
    }

    private void clearBuilderError() {
        this.builderErrorMessage = "";
    }
}
