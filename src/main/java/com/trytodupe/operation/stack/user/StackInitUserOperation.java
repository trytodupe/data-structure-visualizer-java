package com.trytodupe.operation.stack.user;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.stack.atomic.StackPushAtomicOperation;

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
    protected void buildOperations () {
        for (int value : initialValues) {
            super.atomicOperations.add(new StackPushAtomicOperation(value));
        }
    }
}
