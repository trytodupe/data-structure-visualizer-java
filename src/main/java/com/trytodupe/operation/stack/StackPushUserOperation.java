package com.trytodupe.operation.stack;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.UserOperation;

public class StackPushUserOperation extends UserOperation<StackStructure> {

    private final int value;

    public StackPushUserOperation (StackStructure stackStructure, int value) {
        super(stackStructure);
        this.value = value;
        this.description = "Push " + value + " onto stack";
    }

    @Override
    protected void buildAtomicOperations () {
        this.atomicOperations.add(new StackPushAtomicOperation(value));
    }
}
