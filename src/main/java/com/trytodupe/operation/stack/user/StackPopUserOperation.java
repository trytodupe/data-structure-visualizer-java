package com.trytodupe.operation.stack.user;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.stack.atomic.StackPopAtomicOperation;

public class StackPopUserOperation extends UserOperation<StackStructure> {

    public StackPopUserOperation (StackStructure dataStructure) {
        super(dataStructure);
        this.description = "Pop top element from stack";
    }

    @Override
    protected void buildOperations () {
        this.atomicOperations.add(new StackPopAtomicOperation());
    }
}
