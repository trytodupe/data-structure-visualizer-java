package com.trytodupe.gui;

import com.trytodupe.Main;
import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
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
import imgui.flag.ImGuiCond;
import imgui.type.ImInt;

import java.util.List;

/**
 * UIPanelManager manages all UI panels and handles user interactions.
 * Simplified design: 4 main windows
 * - Animation Controls: Animation playback controls
 * - Operation Creator: Tab-based operation creation with integrated operation info
 * - Data Structure Visualization: Unified visualization for all data structures
 * - Operation History: Show executed operations history
 */
public class UIPanelManager {

    private final Main mainApp;

    // UI State
    private ImInt selectedDataStructure = new ImInt(0); // 0: Array, 1: Stack, 2: BinaryTree, 3: BST, 4: HuffmanTree, 5: AVLTree
    private String[] dataStructureNames = {"Array", "Stack", "Binary Tree", "BST", "Huffman Tree", "AVL Tree"};

    // Operation parameters
    private int[] arraySize = {10};
    private int[] stackCapacity = {10};
    private int[] insertValue = {0};
    private int[] insertPosition = {0};
    private int[] deleteValue = {0};

    // Renderers
    private ArrayStructureRenderer arrayRenderer = new ArrayStructureRenderer();
    private StackStructureRenderer stackRenderer = new StackStructureRenderer();
    private BinaryTreeStructureRenderer binaryTreeRenderer = new BinaryTreeStructureRenderer();

    // Pending operation (待执行的操作)
    private UserOperation<?> pendingOperation = null;
    private String pendingOperationDescription = "";

    public UIPanelManager(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Render all UI panels
     */
    public void render() {
        renderControlPanel();
        renderOperationCreatorPanel();
        renderDataStructureVisualization();
        renderOperationHistoryPanel();
    }

    /**
     * Render main control panel (play, pause, step, speed) - Left Top
     */
    private void renderControlPanel() {
        ImGui.begin("Animation Controls");
        ImGui.setWindowSize(350, 300, ImGuiCond.FirstUseEver);

        ImGui.text("Playback:");

        // Step forward button
        if (ImGui.button("Step Forward", 100, 30)) {
            mainApp.stepForward();
        }
        ImGui.sameLine();

        // Step backward button
        if (ImGui.button("Step Backward", 100, 30)) {
            mainApp.stepBackward();
        }

        // Auto step toggle button
        if (ImGui.button(mainApp.isPlaying() ? "Pause" : "Auto Step", 100, 30)) {
            mainApp.setPlaying(!mainApp.isPlaying());
        }

        ImGui.separator();

        // Playback speed slider
        float[] speed = {mainApp.getPlaybackSpeed()};
        if (ImGui.sliderFloat("Speed", speed, 0.1f, 5.0f)) {
            mainApp.setPlaybackSpeed(speed[0]);
        }

        ImGui.separator();

        // Current step display and slider
        ImGui.text("Step: " + mainApp.getCurrentStep() + " / " + mainApp.getAnimationTimelineSize());

        int[] stepArray = {mainApp.getCurrentStep()};
        if (ImGui.sliderInt("##StepSlider", stepArray, 0, mainApp.getAnimationTimelineSize())) {
            mainApp.jumpToStep(stepArray[0]);
        }

        ImGui.end();
    }

    /**
     * Render operation creator panel with tabs - Left Bottom
     * Integrates data structure selection and operation info
     */
    private void renderOperationCreatorPanel() {
        ImGui.begin("Operation Creator");
        ImGui.setWindowSize(350, 500, ImGuiCond.FirstUseEver);

        // Tab bar for data structure selection
        if (ImGui.beginTabBar("DataStructureTab")) {
            for (int i = 0; i < dataStructureNames.length; i++) {
                if (ImGui.beginTabItem(dataStructureNames[i])) {
                    selectedDataStructure.set(i);
                    renderOperationCreatorContent(i);
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }

        ImGui.end();
    }

    /**
     * Render operation creator content based on selected data structure
     */
    private void renderOperationCreatorContent(int index) {
        // Fixed height for pending operation section (150 pixels)
        float PENDING_OPERATION_HEIGHT = 90;

        ImGui.separator();

        // Show pending operation info or placeholder with fixed height
        ImGui.beginChild("PendingOperationSection", 0, PENDING_OPERATION_HEIGHT, true);
        {
            if (pendingOperation != null) {
                ImGui.textColored(0xFF00FF00, "PENDING OPERATION");
                ImGui.text("Type: " + pendingOperationDescription);
                ImGui.spacing();
                ImGui.separator();

                if (ImGui.button("Execute", 80, 25)) {
                    mainApp.setAnimationTimeline(pendingOperation);
                    pendingOperation = null;
                    pendingOperationDescription = "";
                }

                ImGui.sameLine();

                if (ImGui.button("Cancel", 80, 25)) {
                    pendingOperation = null;
                    pendingOperationDescription = "";
                }
            } else {
                // Show placeholder when no pending operation
                ImGui.textDisabled("No pending operation");
                ImGui.spacing();
                ImGui.textDisabled("Create an operation above");
            }
        }
        ImGui.endChild();

        ImGui.separator();

        // Show current animation step info if no pending operation
        if (pendingOperation == null) {
            HighlightInfo highlightInfo = mainApp.getCurrentHighlightInfo();
            if (highlightInfo != null && !highlightInfo.isEmpty()) {
                ImGui.text("Current Step Info:");
                ImGui.text("Indices: " + highlightInfo.arrayIndices.size());
                ImGui.text("Nodes: " + highlightInfo.nodeUUIDs.size());
                ImGui.text("Arrows: " + highlightInfo.arrows.size());
                if (highlightInfo.highlightEntireArray) {
                    ImGui.bulletText("Entire array highlighted");
                }
                if (highlightInfo.highlightTempSlot) {
                    ImGui.bulletText("Temp slot highlighted");
                }
            }
            ImGui.separator();
        }

        // Operation creator for each data structure
        switch (index) {
            case 0: // Array
                renderArrayOperationCreator();
                break;
            case 1: // Stack
                renderStackOperationCreator();
                break;
            case 2: // Binary Tree
                ImGui.text("Binary Tree operations coming soon");
                break;
            case 3: // BST
                ImGui.text("BST operations coming soon");
                break;
            case 4: // Huffman Tree
                ImGui.text("Huffman Tree operations coming soon");
                break;
            case 5: // AVL Tree
                ImGui.text("AVL Tree operations coming soon");
                break;
        }
    }

    /**
     * Render data structure visualization - Unified window for all data structures
     */
    private void renderDataStructureVisualization() {
        ImGui.begin("Data Structure Visualization");

        HighlightInfo highlightInfo = mainApp.getCurrentHighlightInfo();

        switch (selectedDataStructure.get()) {
            case 0: // Array
                ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
                arrayRenderer.renderContent(array, highlightInfo);
                break;
            case 1: // Stack
                StackStructure stack = Main.getDataStructure(StackStructure.class);
                stackRenderer.renderContent(stack, highlightInfo);
                break;
            case 2: // Binary Tree
                BinaryTreeStructure<?> binaryTree = Main.getDataStructure(BinaryTreeStructure.class);
                binaryTreeRenderer.renderContent(binaryTree, highlightInfo);
                break;
            case 3: // BST
                BinarySearchTreeStructure<?> bst = Main.getDataStructure(BinarySearchTreeStructure.class);
                binaryTreeRenderer.renderContent(bst, highlightInfo);
                break;
            case 4: // Huffman Tree
                HuffmanTreeStructure<?> huffmanTree = Main.getDataStructure(HuffmanTreeStructure.class);
                ImGui.text("Huffman Tree visualization not yet implemented");
                break;
            case 5: // AVL Tree
                AVLTreeStructure<?> avlTree = Main.getDataStructure(AVLTreeStructure.class);
                binaryTreeRenderer.renderContent(avlTree, highlightInfo);
                break;
        }

        ImGui.end();
    }

    /**
     * Render operation history panel - Bottom
     */
    private void renderOperationHistoryPanel() {
        ImGui.begin("Operation History");
        ImGui.setWindowSize(750, 200, ImGuiCond.FirstUseEver);

        ImGui.text("Executed Operations:");
        ImGui.separator();

        List<String> operationStack = mainApp.getOperationStack();
        if (operationStack.isEmpty()) {
            ImGui.text("(No operations executed yet)");
        } else {
            ImGui.beginChild("OperationList", 0, 0, true);
            for (int i = 0; i < operationStack.size(); i++) {
                String opDesc = operationStack.get(i);
                ImGui.text((i + 1) + ". " + opDesc);
            }
            ImGui.endChild();
        }

        ImGui.end();
    }

    /**
     * Render array-specific operation creator
     */
    private void renderArrayOperationCreator() {
        ImGui.text("Array Operations");
        ImGui.spacing();

        ImGui.sliderInt("Size##array", arraySize, 1, 20);

        if (ImGui.button("Create Array", 150, 25)) {
            ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
            int[] initialValues = new int[arraySize[0]];
            for (int i = 0; i < arraySize[0]; i++) {
                initialValues[i] = i + 1;
            }
            pendingOperation = new ArrayInitUserOperation(array, initialValues);
            pendingOperationDescription = "Array Init (size=" + arraySize[0] + ")";
        }

        ImGui.spacing();
        ImGui.separator();
        ImGui.spacing();

        ImGui.sliderInt("Value##insert", insertValue, 0, 100);
        ImGui.sliderInt("Position##insert", insertPosition, 0, 20);

        if (ImGui.button("Insert", 150, 25)) {
            ArrayStructure array = Main.getDataStructure(ArrayStructure.class);
            pendingOperation = new ArrayInsertUserOperation(array, insertPosition[0], insertValue[0]);
            pendingOperationDescription = "Array Insert (pos=" + insertPosition[0] + ", val=" + insertValue[0] + ")";
        }
    }

    /**
     * Render stack-specific operation creator
     */
    private void renderStackOperationCreator() {
        ImGui.text("Stack Operations");
        ImGui.spacing();

        ImGui.sliderInt("Capacity##stack", stackCapacity, 1, 20);

        if (ImGui.button("Create Stack", 150, 25)) {
            StackStructure stack = Main.getDataStructure(StackStructure.class);
            int[] initialValues = new int[stackCapacity[0]];
            for (int i = 0; i < stackCapacity[0]; i++) {
                initialValues[i] = i + 1;
            }
            pendingOperation = new StackInitUserOperation(stack, initialValues);
            pendingOperationDescription = "Stack Init (capacity=" + stackCapacity[0] + ")";
        }

        ImGui.spacing();
        ImGui.separator();
        ImGui.spacing();

        ImGui.sliderInt("Value##push", insertValue, 0, 100);

        if (ImGui.button("Push", 150, 25)) {
            StackStructure stack = Main.getDataStructure(StackStructure.class);
            pendingOperation = new StackPushUserOperation(stack, insertValue[0]);
            pendingOperationDescription = "Stack Push (value=" + insertValue[0] + ")";
        }

        ImGui.sameLine();

        if (ImGui.button("Pop", 150, 25)) {
            StackStructure stack = Main.getDataStructure(StackStructure.class);
            pendingOperation = new StackPopUserOperation(stack);
            pendingOperationDescription = "Stack Pop";
        }
    }
}
