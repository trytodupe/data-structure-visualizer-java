package com.trytodupe.operation.array;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.UserOperation;

public class ArrayDeleteUserOperation extends UserOperation<ArrayStructure> {

    private final int index;

    public ArrayDeleteUserOperation (ArrayStructure dataStructure, int index) {
        super(dataStructure);
        this.index = index;
        this.description = "Delete element at index " + index;
    }

    @Override
    protected void buildAtomicOperations () {
        for (int i = index + 1; i < super.dataStructure.getSize(); i++) {
            super.atomicOperations.add(new ArrayCopyAtomicOperation(i, i - 1));
        }

        super.atomicOperations.add(new ArrayResizeAtomicOperation(ArrayResizeAtomicOperation.ResizeMode.DECREMENT));
    }
}
