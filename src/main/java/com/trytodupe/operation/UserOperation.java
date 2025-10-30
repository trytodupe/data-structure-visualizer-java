package com.trytodupe.operation;

import com.trytodupe.Main;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.serialization.ISerializable;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class UserOperation<T extends DataStructure> implements ISerializable {

    protected transient T dataStructure;

    protected String description;

    protected transient List<AtomicOperation<T>> atomicOperations;

    private transient boolean built = false;


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
		if (!built) {
			throw new IllegalStateException("Operation instance has not been executed yet.");
		}

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

    @SuppressWarnings("unchecked")
    protected Class<T> getDataStructureType() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) superClass.getActualTypeArguments()[0];
    }

    @Override
    public void postDeserialize() {
        Class<T> type = getDataStructureType();
        this.dataStructure = Main.getDataStructure(type);

        if (this.atomicOperations == null) {
            this.atomicOperations = new ArrayList<>();
        }
    }

}
