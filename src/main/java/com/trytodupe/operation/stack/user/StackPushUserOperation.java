package com.trytodupe.operation.stack.user;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.stack.atomic.StackPushAtomicOperation;

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
