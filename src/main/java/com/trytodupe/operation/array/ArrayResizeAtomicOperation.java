package com.trytodupe.operation.array;

import com.trytodupe.datastructure.ArrayStructure;
import com.trytodupe.operation.AtomicOperation;

public class ArrayResizeAtomicOperation extends AtomicOperation<ArrayStructure> {

    public enum ResizeMode {
        ABSOLUTE,
        INCREMENT,
        DECREMENT
    }

    private final ResizeMode mode;
    private int oldSize;
    private int newSize;

    public ArrayResizeAtomicOperation (int newSize) {
        this.mode = ResizeMode.ABSOLUTE;
        this.newSize = newSize;
    }

    public ArrayResizeAtomicOperation (ResizeMode mode) {
        if (mode == ResizeMode.ABSOLUTE)
            throw new IllegalArgumentException("Use the (int newSize) constructor for ABSOLUTE mode.");
        this.mode = mode;
    }

    @Override
    public void execute (ArrayStructure arrayStructure) {
        oldSize = arrayStructure.getSize();
        int targetSize =
                mode == ResizeMode.ABSOLUTE ? newSize
                : mode == ResizeMode.INCREMENT ? oldSize + 1
                : oldSize - 1;
        arrayStructure.resize(targetSize);
    }

    @Override
    public void undo (ArrayStructure arrayStructure) {
        arrayStructure.resize(oldSize);
    }

    @Override
    public String getDescription () {
        return "Resize array to " + newSize;
    }
}
