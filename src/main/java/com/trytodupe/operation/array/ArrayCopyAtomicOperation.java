package com.trytodupe.operation.array;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.AtomicOperation;

public class ArrayCopyAtomicOperation extends AtomicOperation<ArrayStructure> {

    private final int fromIndex;
    private final int toIndex;

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

}
