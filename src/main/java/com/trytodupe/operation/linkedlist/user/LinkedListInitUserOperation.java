package com.trytodupe.operation.linkedlist.user;

import com.trytodupe.datastructure.LinkedListStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.common.DataStructureClearAtomicOperation;
import com.trytodupe.operation.linkedlist.atomic.LinkedListInsertAtomicOperation;

public class LinkedListInitUserOperation extends UserOperation<LinkedListStructure> {

    private final int[] values;

    public LinkedListInitUserOperation(LinkedListStructure dataStructure, int[] values) {
        super(dataStructure);
        this.values = values;
        this.description = "Initialize linked list";
    }

    @Override
    protected void buildOperations() {
        super.atomicOperations.add(new DataStructureClearAtomicOperation<>());
        for (int i = 0; i < values.length; i++) {
            super.atomicOperations.add(new LinkedListInsertAtomicOperation(i, values[i], null));
        }
    }
}
