package com.trytodupe.operation;

import com.trytodupe.Main;
import com.trytodupe.datastructure.DataStructure;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeUserOperation<T extends DataStructure> extends UserOperation<T> {

    protected transient List<UserOperation<?>> childOperations;

    public CompositeUserOperation (T dataStructure) {
        super(dataStructure);

        this.childOperations = new ArrayList<>();
    }


    @Override
    public void execute() {
        if (!built) {
            buildOperations();
            built = true;
        }

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

        if (this.childOperations == null) {
            this.childOperations = new ArrayList<>();
        }
    }
}
