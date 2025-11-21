package com.trytodupe.operation.linkedlist.user;

import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.linkedlist.atomic.LinkedListInsertAtomicOperation;

public class LinkedListInsertUserOperation extends UserOperation<LinkedListStructure> {

    private final int index;
    private final int value;

    public LinkedListInsertUserOperation(LinkedListStructure dataStructure, int index, int value) {
        super(dataStructure);
        this.index = index;
        this.value = value;
        this.description = "Linked list insert value " + value + " at " + index;
    }

    @Override
    protected void buildOperations() {
        String prevUuid = null;
        if (index > 0 && index - 1 < dataStructure.size()) {
            LinkedListStructure.Node prev = dataStructure.getNodeAtIndex(index - 1);
            if (prev != null) {
                prevUuid = prev.getUuid();
            }
        }
        super.atomicOperations.add(new LinkedListInsertAtomicOperation(index, value, prevUuid));
    }
}
