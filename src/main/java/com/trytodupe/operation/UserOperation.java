package com.trytodupe.operation;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.serialization.ISerializable;

import java.util.List;

public abstract class UserOperation<T extends DataStructure> implements ISerializable {

    protected T dataStructure;

    protected List<AtomicOperation<T>> atomicOperations;

    public UserOperation(T dataStructure) {
        this.dataStructure = dataStructure;
    }

    public void execute() {
        for (AtomicOperation<T> atomicOperation : atomicOperations) {
            atomicOperation.execute(dataStructure);
        }
    }

    public void undo() {
        for (int i = atomicOperations.size() - 1; i >= 0; i--) {
            atomicOperations.get(i).undo(dataStructure);
        }
    }

}
