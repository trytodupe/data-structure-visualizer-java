package com.trytodupe.operation.array;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.UserOperation;

public class ArrayInsertUserOperation extends UserOperation<ArrayStructure> {

    private final int index;
    private final int value;

    public ArrayInsertUserOperation (ArrayStructure dataStructure, int index, int value) {
        super(dataStructure);
        this.index = index;
        this.value = value;
        this.description = "Insert " + value + " at index " + index;
    }

    @Override
    protected void buildAtomicOperations () {
        super.atomicOperations.add(new ArrayResizeAtomicOperation(ArrayResizeAtomicOperation.ResizeMode.INCREMENT));

        for (int i = super.dataStructure.getSize() - 1; i >= index; i--) {
            super.atomicOperations.add(new ArrayCopyAtomicOperation(i, i + 1));
        }

        super.atomicOperations.add(new ArrayWriteAtomicOperation(index, value));
    }
}
