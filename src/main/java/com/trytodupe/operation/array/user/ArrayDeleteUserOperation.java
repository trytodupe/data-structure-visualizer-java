package com.trytodupe.operation.array.user;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.atomic.ArrayCopyAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayResizeAtomicOperation;

public class ArrayDeleteUserOperation extends UserOperation<ArrayStructure> {

    private final int index;

    public ArrayDeleteUserOperation (ArrayStructure arrayStructure, int index) {
        super(arrayStructure);
        this.index = index;
        this.description = "Delete element at index " + index;
    }

    @Override
    protected void buildOperations () {
        for (int i = index + 1; i < super.dataStructure.getSize(); i++) {
            super.atomicOperations.add(new ArrayCopyAtomicOperation(i, i - 1));
        }

        super.atomicOperations.add(new ArrayResizeAtomicOperation(ArrayResizeAtomicOperation.ResizeMode.DECREMENT));
    }
}
