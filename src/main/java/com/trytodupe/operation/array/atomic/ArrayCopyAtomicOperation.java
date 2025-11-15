package com.trytodupe.operation.array.atomic;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

public class ArrayCopyAtomicOperation extends AtomicOperation<ArrayStructure> {

    private final int fromIndex; // highlight
    private final int toIndex; // highlight

    private int oldValue; // old value from toIndex
    private int newValue; // value from fromIndex

    public ArrayCopyAtomicOperation (int fromIndex, int toIndex) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public void execute(ArrayStructure arrayStructure) {
        oldValue = arrayStructure.getData(toIndex);
        newValue = arrayStructure.getData(fromIndex);
        arrayStructure.setData(toIndex, newValue);
    }

    @Override
    public void undo(ArrayStructure arrayStructure) {
        arrayStructure.setData(toIndex, oldValue);
    }

    @Override
    public String getDescription() {
        return "Copy element from index " + fromIndex + " to index " + toIndex;
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
