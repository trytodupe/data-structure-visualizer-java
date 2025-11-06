package com.trytodupe.operation.array.user;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.array.atomic.ArrayCopyAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayResizeAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayWriteAtomicOperation;

public class ArrayInsertUserOperation extends UserOperation<ArrayStructure> {

    private final int index;
    private final int value;

    public ArrayInsertUserOperation (ArrayStructure arrayStructure, int index, int value) {
        super(arrayStructure);
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
