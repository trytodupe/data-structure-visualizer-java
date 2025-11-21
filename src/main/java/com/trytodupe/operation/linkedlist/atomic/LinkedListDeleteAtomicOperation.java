package com.trytodupe.operation.linkedlist.atomic;

import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

public class LinkedListDeleteAtomicOperation extends AtomicOperation<LinkedListStructure> {

    private final int index;
    private final String targetUuid;
    private LinkedListStructure.Node removedNode;

    public LinkedListDeleteAtomicOperation(int index, String targetUuid) {
        this.index = index;
        this.targetUuid = targetUuid;
    }

    public int getIndex() {
        return index;
    }

    public String getRemovedUuid() {
        if (removedNode != null) {
            return removedNode.getUuid();
        }
        return targetUuid;
    }

    public int getRemovedValue() {
        return removedNode != null ? removedNode.getValue() : 0;
    }

    @Override
    public void execute(LinkedListStructure dataStructure) {
        if (targetUuid != null) {
            removedNode = dataStructure.removeByUUID(targetUuid);
            if (removedNode == null) {
                removedNode = dataStructure.removeAt(index);
            }
        } else {
            removedNode = dataStructure.removeAt(index);
        }
    }

    @Override
    public void undo(LinkedListStructure dataStructure) {
        if (removedNode != null) {
            dataStructure.insertExistingAt(index, removedNode);
        }
    }

    @Override
    public String getDescription() {
        return "Delete value at position " + index;
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
