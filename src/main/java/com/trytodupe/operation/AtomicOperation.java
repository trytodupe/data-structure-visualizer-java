package com.trytodupe.operation;

import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.serialization.ISerializable;

public abstract class AtomicOperation<T extends DataStructure> implements ISerializable {

    public abstract void execute(T dataStructure);

    public abstract void undo(T dataStructure);

    public abstract String getDescription();

//    public abstract void drawOverlay();

}
