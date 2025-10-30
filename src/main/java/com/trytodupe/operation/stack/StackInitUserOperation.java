package com.trytodupe.operation.stack;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.UserOperation;

import java.util.Arrays;

public class StackInitUserOperation extends UserOperation<StackStructure> {

    private final int[] initialValues;

    public StackInitUserOperation (StackStructure stackStructure, int[] initialValues) {
        super(stackStructure);

        if (initialValues == null)
            throw new IllegalArgumentException("Initial values array cannot be null.");

        this.initialValues = initialValues;
        this.description = "Initialize stack to: " + Arrays.toString(initialValues);
    }

    @Override
    protected void buildAtomicOperations () {
        for (int value : initialValues) {
            super.atomicOperations.add(new StackPushAtomicOperation(value));
        }
    }
}
