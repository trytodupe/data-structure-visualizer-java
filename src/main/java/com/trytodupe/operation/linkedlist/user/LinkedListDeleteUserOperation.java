package com.trytodupe.operation.linkedlist.user;

import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.linkedlist.atomic.LinkedListDeleteAtomicOperation;

public class LinkedListDeleteUserOperation extends UserOperation<LinkedListStructure> {

    private final int index;

    public LinkedListDeleteUserOperation(LinkedListStructure dataStructure, int index) {
        super(dataStructure);
        this.index = index;
        this.description = "Linked list delete at " + index;
    }

    @Override
    protected void buildOperations() {
        super.atomicOperations.add(new LinkedListDeleteAtomicOperation(index));
    }
}
