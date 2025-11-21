package com.trytodupe.operation;

import com.trytodupe.Main;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.serialization.ISerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CompositeUserOperation<T extends DataStructure> extends UserOperation<T> implements ISerializable {

    protected transient List<UserOperation<?>> childOperations;
    protected transient int activeChildIndex = 0;

    public CompositeUserOperation (T dataStructure) {
        super(dataStructure);

        this.childOperations = new ArrayList<>();
    }

    @Override
    public void build() {
        if (!built) {
            buildOperations();

            for (UserOperation<?> childOperation : childOperations) {
                childOperation.build();
            }

            activeChildIndex = 0;
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
    public int stepForward() {
        this.build();
        UserOperation<?> child = findNextChildWithSteps();
        if (child == null) {
            throw new IllegalStateException("No more steps to execute.");
        }
        child.stepForward();
        currentStep++;
        return currentStep;
    }

    @Override
    public int stepBackward() {
        if (currentStep < 0) {
            throw new IllegalStateException("No more steps to undo.");
        }
        UserOperation<?> child = findPreviousChildWithHistory();
        if (child == null) {
            throw new IllegalStateException("No more steps to undo.");
        }
        child.stepBackward();
        currentStep--;
        return currentStep;
    }

    @Override
    public int getTotalStep() {
        this.build();
        int total = 0;
        for (UserOperation<?> child : childOperations) {
            total += child.getTotalStep();
        }
        return total;
    }

    @Override
    public AtomicOperation<? super T> peekNextAtomic() {
        this.build();
        for (int i = Math.min(activeChildIndex, childOperations.size()); i < childOperations.size(); i++) {
            @SuppressWarnings("unchecked")
            AtomicOperation<? super T> next = (AtomicOperation<? super T>) childOperations.get(i).peekNextAtomic();
            if (next != null) {
                return next;
            }
        }
        return null;
    }

    public List<Integer> getTotalStepList() {
        List<Integer> steps = new ArrayList<>();
        for (UserOperation<?> childOperation : childOperations) {
            steps.add(childOperation.getTotalStep());
        }
        return steps;
    }

    public List<UserOperation<?>> getChildOperations() {
        return Collections.unmodifiableList(childOperations);
    }

    public int getActiveChildIndex() {
        return Math.min(activeChildIndex, childOperations.size() - 1);
    }

    private UserOperation<?> findNextChildWithSteps() {
        int idx = activeChildIndex;
        while (idx < childOperations.size()) {
            UserOperation<?> child = childOperations.get(idx);
            if (child.getTotalStep() == 0 || child.getCurrentStep() >= child.getTotalStep() - 1) {
                idx++;
                continue;
            }
            activeChildIndex = idx;
            return child;
        }
        activeChildIndex = childOperations.size();
        return null;
    }

    private UserOperation<?> findPreviousChildWithHistory() {
        int idx = Math.min(activeChildIndex, childOperations.size() - 1);
        while (idx >= 0) {
            UserOperation<?> child = childOperations.get(idx);
            if (child.getCurrentStep() >= 0) {
                activeChildIndex = idx;
                return child;
            }
            idx--;
        }
        activeChildIndex = 0;
        return null;
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
        this.activeChildIndex = 0;

    }
}
