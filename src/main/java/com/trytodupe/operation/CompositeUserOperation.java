package com.trytodupe.operation;

import com.trytodupe.Main;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.serialization.ISerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CompositeUserOperation<T extends DataStructure> extends UserOperation<T> implements ISerializable {

    protected transient List<UserOperation<?>> childOperations;

    public CompositeUserOperation (T dataStructure) {
        super(dataStructure);

        this.childOperations = new ArrayList<>();
    }

    @Override
    public void build() {
        if (!built) {
            buildOperations();

            // build atomic operations
            for (UserOperation<?> childOperation : childOperations) {
                childOperation.build();

                for (AtomicOperation<?> atomicOperation : childOperation.atomicOperations) {
                    atomicOperations.add((AtomicOperation<? super T>) atomicOperation);
                }
            }

            built = true;
        }
    }

    @Override
    public void execute() {
        this.build();

        for (UserOperation<?> childOperation : childOperations) {
            childOperation.execute();
        }
    }

    @Override
    public void undo() {
        if (!built) {
            throw new IllegalStateException("Operation instance has not been executed yet.");
        }

        for (int i = childOperations.size() - 1; i >= 0; i--) {
            childOperations.get(i).undo();
        }
    }

    @Override
    public void postDeserialize() {
        Class<T> type = getDataStructureType();
        this.dataStructure = Main.getDataStructure(type);

        if (this.atomicOperations == null) {
            this.atomicOperations = new ArrayList<>();
        }

        if (this.childOperations == null) {
            this.childOperations = new ArrayList<>();
        }

    }
}
