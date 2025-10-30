package com.trytodupe.operation.stack;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.UserOperation;

public class StackPopUserOperation extends UserOperation<StackStructure> {

    public StackPopUserOperation (StackStructure dataStructure) {
        super(dataStructure);
        this.description = "Pop top element from stack";
    }

    @Override
    protected void buildAtomicOperations () {
        this.atomicOperations.add(new StackPopAtomicOperation());
    }
}
