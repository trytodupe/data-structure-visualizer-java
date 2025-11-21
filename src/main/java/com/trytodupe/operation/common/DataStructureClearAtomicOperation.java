package com.trytodupe.operation.common;

import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

/**
 * Generic clear operation for data structures; execute() calls clear(), undo is a no-op.
 * (Init operations should rebuild the structure so undo is typically not invoked directly.)
 */
public class DataStructureClearAtomicOperation<T extends DataStructure> extends AtomicOperation<T> {
    @Override
    public void execute(T dataStructure) {
        dataStructure.clear();
    }

    @Override
    public void undo(T dataStructure) {
        // Clearing is not reversible; init operations should rebuild state via subsequent atomics.
    }

    @Override
    public String getDescription() {
        return "Clear data structure";
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        // No highlighting for clear
    }
}
