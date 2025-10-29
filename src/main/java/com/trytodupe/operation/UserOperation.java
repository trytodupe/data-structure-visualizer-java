package com.trytodupe.operation;

import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.serialization.ISerializable;

import java.util.ArrayList;
import java.util.List;

public abstract class UserOperation<T extends DataStructure> implements ISerializable {

    protected T dataStructure;

    protected String description;

    protected List<AtomicOperation<T>> atomicOperations;

    private boolean built = false;


    public UserOperation(T dataStructure) {
        this.dataStructure = dataStructure;
        this.atomicOperations = new ArrayList<>();
    }

    protected abstract void buildAtomicOperations();

    public void execute() {
        if (!built) {
            buildAtomicOperations();
            built = true;
        }

        for (AtomicOperation<T> atomicOperation : atomicOperations) {
            atomicOperation.execute(dataStructure);
        }
    }

    public void undo() {
        for (int i = atomicOperations.size() - 1; i >= 0; i--) {
            atomicOperations.get(i).undo(dataStructure);
        }
    }

//    public void drawOverlay(int currentStep) {
//        atomicOperations.get(currentStep).drawOverlay();
//    }

    public String getDescription() {
        return description;
    }

}
