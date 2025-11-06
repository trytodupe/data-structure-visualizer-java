package com.trytodupe.operation.stack.atomic;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.AtomicOperation;

public class StackPopAtomicOperation extends AtomicOperation<StackStructure> {

    private int oldValue;

    @Override
    public void execute (StackStructure stackStructure) {
        oldValue = stackStructure.pop();
    }

    @Override
    public void undo (StackStructure stackStructure) {
        stackStructure.push(oldValue);
    }

    @Override
    public String getDescription () {
        return "Pop top element from stack";
    }
}
