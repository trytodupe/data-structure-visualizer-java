package com.trytodupe.gui;

import com.trytodupe.operation.IOperationVisitor;
import com.trytodupe.operation.array.atomic.ArrayCopyAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayResizeAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayWriteAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeLeftRotateAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeRightRotateAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeUpdateNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeAddNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeConnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeDisconnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeRemoveNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeReplaceSubtreeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeSwapValueAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeAddAtomicOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeMergeAtomicOperation;
import com.trytodupe.operation.linkedlist.atomic.LinkedListDeleteAtomicOperation;
import com.trytodupe.operation.linkedlist.atomic.LinkedListInsertAtomicOperation;
import com.trytodupe.operation.stack.atomic.StackPopAtomicOperation;
import com.trytodupe.operation.stack.atomic.StackPushAtomicOperation;
import com.trytodupe.operation.utils.VisualizePathAtomicOperation;

import java.util.UUID;

public class HighlightVisitor implements IOperationVisitor {

    private HighlightInfo highlightInfo;

    public HighlightVisitor() {
        this.highlightInfo = new HighlightInfo();
    }

    public HighlightInfo getHighlightInfo() {
        return highlightInfo;
    }

    public void reset() {
        this.highlightInfo = new HighlightInfo();
    }

    @Override
    public void visit(ArrayCopyAtomicOperation op) {
        highlightInfo.arrayIndices.add(op.getFromIndex());
        highlightInfo.arrayIndices.add(op.getToIndex());
        highlightInfo.arrows.add(new ArrowInfo(
            String.valueOf(op.getFromIndex()),
            String.valueOf(op.getToIndex()),
            ArrowInfo.ArrowType.SINGLE_DIRECTIONAL
        ));
    }

    @Override
    public void visit(ArrayResizeAtomicOperation op) {
        highlightInfo.highlightEntireArray = true;
    }

    @Override
    public void visit(ArrayWriteAtomicOperation op) {
        highlightInfo.arrayIndices.add(op.getIndex());
    }

    @Override
    public void visit(AVLTreeLeftRotateAtomicOperation<?> op) {
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getPivotUUID()));
        // Optionally, highlight the new root and its children involved in the rotation
        // This would require getters for newRootUUID, etc. if you want to highlight them.
    }

    @Override
    public void visit(AVLTreeRightRotateAtomicOperation<?> op) {
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getPivotUUID()));
        // Optionally, highlight the new root and its children involved in the rotation
    }

    @Override
    public void visit(AVLTreeUpdateNodeAtomicOperation<?> op) {
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getUuid()));
    }

    @Override
    public void visit(BinaryTreeAddNodeAtomicOperation<?> op) {
        // When a node is added, it's in a 'temp slot'.
        // We only need to highlight the temp slot.
        highlightInfo.highlightTempSlot = true;
    }

    @Override
    public void visit(BinaryTreeConnectNodeAtomicOperation<?> op) {
        if (op.getParentUUID() != null) {
            highlightInfo.nodeUUIDs.add(UUID.fromString(op.getParentUUID()));
        }
        // When a node is connected, it moves from the temp slot.
        highlightInfo.highlightTempSlot = true;
    }

    @Override
    public void visit(BinaryTreeDisconnectNodeAtomicOperation<?> op) {
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getChildUUID()));
        // When a node is disconnected, it moves to the temp slot.
        highlightInfo.highlightTempSlot = true;
    }

    @Override
    public void visit(BinaryTreeRemoveNodeAtomicOperation<?> op) {
        // When a node is removed, it's taken from the temp slot.
        highlightInfo.highlightTempSlot = true;
    }

    @Override
    public void visit(BinaryTreeReplaceSubtreeAtomicOperation<?> op) {
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getSubTree1UUID()));
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getSubTree2UUID()));
        highlightInfo.arrows.add(new ArrowInfo(
            op.getSubTree2UUID(),
            op.getSubTree1UUID(),
            ArrowInfo.ArrowType.SINGLE_DIRECTIONAL
        ));
    }

    @Override
    public void visit(BinaryTreeSwapValueAtomicOperation<?> op) {
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getUuid1()));
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getUuid2()));
        highlightInfo.arrows.add(new ArrowInfo(
            op.getUuid1(),
            op.getUuid2(),
            ArrowInfo.ArrowType.DOUBLE_DIRECTIONAL
        ));
    }

    @Override
    public void visit(BinaryTreeUpdateValueAtomicOperation<?> op) {
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getUuid()));
    }

    @Override
    public void visit(HuffmanTreeAddAtomicOperation<?> op) {
        // A new node is added, its UUID is available via getUUID() after execute.
        // For now, no specific highlight, as it's a new node being prepared.
    }

    @Override
    public void visit(HuffmanTreeMergeAtomicOperation<?> op) {
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getLeftUUID()));
        highlightInfo.nodeUUIDs.add(UUID.fromString(op.getRightUUID()));
    }

    @Override
    public void visit(StackPopAtomicOperation op) {
        // Popping affects the top of the stack. We can highlight the top element's index.
        // This would require the stack size or the index of the popped element from the operation.
    }

    @Override
    public void visit(StackPushAtomicOperation op) {
        // Pushing affects the top of the stack. We can highlight the new top element's index.
        // This would require the stack size or the index of the pushed element from the operation.
    }

    @Override
    public void visit(VisualizePathAtomicOperation op) {
        for (String uuid : op.getNodeUUIDs()) {
            highlightInfo.nodeUUIDs.add(UUID.fromString(uuid));
        }
    }

    @Override
    public void visit(LinkedListInsertAtomicOperation op) {
        if (op.getInsertedUuid() != null) {
            highlightInfo.nodeUUIDs.add(UUID.fromString(op.getInsertedUuid()));
        }
    }

    @Override
    public void visit(LinkedListDeleteAtomicOperation op) {
        if (op.getRemovedUuid() != null) {
            highlightInfo.nodeUUIDs.add(UUID.fromString(op.getRemovedUuid()));
        }
    }
}
