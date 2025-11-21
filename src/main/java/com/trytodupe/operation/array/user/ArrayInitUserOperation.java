package com.trytodupe.operation.array.user;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.common.DataStructureClearAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayResizeAtomicOperation;
import com.trytodupe.operation.array.atomic.ArrayWriteAtomicOperation;

import java.util.Arrays;

public class ArrayInitUserOperation extends UserOperation<ArrayStructure> {

    private final int[] initialValues;

    public ArrayInitUserOperation (ArrayStructure arrayStructure, int[] initialValues) {
        super(arrayStructure);

        if (initialValues == null)
            throw new IllegalArgumentException("Initial values array cannot be null.");

        this.initialValues = initialValues;
        this.description = "Initialize array to: " + Arrays.toString(initialValues);
    }

    @Override
    protected void buildOperations () {
        super.atomicOperations.add(new DataStructureClearAtomicOperation<>());
        super.atomicOperations.add(new ArrayResizeAtomicOperation(initialValues.length));

        for (int i = 0; i < initialValues.length; i++) {
            super.atomicOperations.add(new ArrayWriteAtomicOperation(i, initialValues[i]));
        }
    }
}
