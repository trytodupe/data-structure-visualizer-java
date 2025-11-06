package com.trytodupe.operation.array.atomic;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.AtomicOperation;

public class ArrayWriteAtomicOperation extends AtomicOperation<ArrayStructure> {

    private final int index;
    private int oldValue;
    private final int newValue;

    public ArrayWriteAtomicOperation (int index, int newValue) {
        this.index = index;
        this.newValue = newValue;
    }

    @Override
    public void execute (ArrayStructure arrayStructure) {
        oldValue = arrayStructure.getData(index);
        arrayStructure.setData(index, newValue);
    }

    @Override
    public void undo (ArrayStructure arrayStructure) {
        arrayStructure.setData(index, oldValue);
    }

    @Override
    public String getDescription () {
        return "Set array[" + index + "] = " + newValue;
    }
}

