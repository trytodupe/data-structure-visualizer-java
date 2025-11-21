package com.trytodupe.operation.linkedlist.atomic;

import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

public class LinkedListInsertAtomicOperation extends AtomicOperation<LinkedListStructure> {

    private final int index;
    private final int value;
    private String insertedUuid;

    public LinkedListInsertAtomicOperation(int index, int value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public int getValue() {
        return value;
    }

    public String getInsertedUuid() {
        return insertedUuid;
    }

    @Override
    public void execute(LinkedListStructure dataStructure) {
        LinkedListStructure.Node node = dataStructure.insertAt(index, value);
        insertedUuid = node.getUuid();
    }

    @Override
    public void undo(LinkedListStructure dataStructure) {
        if (insertedUuid != null) {
            dataStructure.removeByUUID(insertedUuid);
        }
    }

    @Override
    public String getDescription() {
        return "Insert value " + value + " at position " + index;
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
