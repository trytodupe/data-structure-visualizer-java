package com.trytodupe.operation.stack.atomic;

import com.trytodupe.datastructure.StackStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

public class StackPushAtomicOperation extends AtomicOperation<StackStructure> {

    private final int value;

    public StackPushAtomicOperation (int value) {
        this.value = value;
    }

    @Override
    public void execute (StackStructure stackStructure) {
        stackStructure.push(value);
    }

    @Override
    public void undo (StackStructure stackStructure) {
        stackStructure.pop();
    }

    @Override
    public String getDescription () {
        return "Push " + value + " onto stack";
    }

    @Override
    public void accept (IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
