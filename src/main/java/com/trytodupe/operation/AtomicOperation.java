package com.trytodupe.operation;

import com.trytodupe.datastructure.DataStructure;

public abstract class AtomicOperation<T extends DataStructure> {

    public abstract void execute(T dataStructure);

    public abstract void undo(T dataStructure);

    public abstract String getDescription();

    public abstract void accept(IOperationVisitor visitor);
}
