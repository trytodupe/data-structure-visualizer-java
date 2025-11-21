package com.trytodupe.operation.linkedlist.atomic;

import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

public class LinkedListDeleteAtomicOperation extends AtomicOperation<LinkedListStructure> {

    private final int index;
    private LinkedListStructure.Node removedNode;

    public LinkedListDeleteAtomicOperation(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getRemovedUuid() {
        return removedNode != null ? removedNode.getUuid() : null;
    }

    public int getRemovedValue() {
        return removedNode != null ? removedNode.getValue() : 0;
    }

    @Override
    public void execute(LinkedListStructure dataStructure) {
        removedNode = dataStructure.removeAt(index);
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
