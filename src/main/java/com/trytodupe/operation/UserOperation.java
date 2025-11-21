package com.trytodupe.operation;

import com.trytodupe.Main;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.serialization.ISerializable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class UserOperation<T extends DataStructure> implements ISerializable {

    protected transient T dataStructure;

    protected String description;

    protected transient List<AtomicOperation<? super T>> atomicOperations;

    protected transient boolean built = false;
    protected transient int currentStep = -1; // ranges from -1 to atomicOperations.size() - 1 (last executed step)


    public UserOperation(T dataStructure) {
        this.dataStructure = dataStructure;
        this.atomicOperations = new ArrayList<>();
    }

    protected abstract void buildOperations ();

    public void build() {
        if (!built) {
            buildOperations();
            built = true;
        }
    }

    public void execute() {
        this.build();

        for (AtomicOperation<? super T> atomicOperation : atomicOperations) {
            if (Main.DEBUG) {
                System.out.println(atomicOperation.getDescription());
            }
            atomicOperation.execute(dataStructure);
            if (Main.DEBUG) {
                Main.getDataStructure(AVLTreeStructure.class).printValue();
            }
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

    public int stepForward() {
        this.build();

        if (atomicOperations.isEmpty()) {
            throw new IllegalStateException("Operation contains no atomic steps.");
        }

        if (currentStep >= atomicOperations.size() - 1) {
            throw new IllegalStateException("No more steps to execute.");
        }

        currentStep++;
        AtomicOperation<? super T> atomicOperation = atomicOperations.get(currentStep);
        atomicOperation.execute(dataStructure);
        return currentStep;
    }

    /**
     * Kept for backward compatibility with older callers.
     * Prefer {@link #stepForward()}.
     */
    @Deprecated
    public int stepFoward() {
        return stepForward();
    }

    public int stepBackward() {
        if (currentStep < 0) {
            throw new IllegalStateException("No more steps to undo.");
        }

        AtomicOperation<? super T> atomicOperation = atomicOperations.get(currentStep);
        atomicOperation.undo(dataStructure);
        currentStep--;
        return currentStep;
    }

    public int getCurrentStep () {
        return currentStep;
    }

    public int getTotalStep () {
        return atomicOperations.size();
    }

    public List<AtomicOperation<? super T>> getAtomicOperations() {
        this.build();
        return Collections.unmodifiableList(atomicOperations);
    }

    public AtomicOperation<? super T> peekNextAtomic() {
        this.build();
        int nextIndex = currentStep + 1;
        if (nextIndex >= atomicOperations.size()) {
            return null;
        }
        return atomicOperations.get(nextIndex);
    }

    public void previewNext(IOperationVisitor visitor) {
        if (visitor == null) {
            return;
        }
        AtomicOperation<? super T> next = peekNextAtomic();
        if (next != null) {
            next.accept(visitor);
        }
    }

    public int jumpTo(int targetStep) {
        this.build();
        int maxStep = getTotalStep() - 1;
        int clamped = Math.max(-1, Math.min(targetStep, maxStep));

        while (currentStep < clamped) {
            stepForward();
        }
        while (currentStep > clamped) {
            stepBackward();
        }

        return currentStep;
    }

    public String getDescription() {
        return description;
    }

    public T getDataStructure() {
        return dataStructure;
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getDataStructureType() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Type typeArg = superClass.getActualTypeArguments()[0];

        if (typeArg instanceof Class<?>) {
            return (Class<T>) typeArg;
        } else if (typeArg instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) typeArg).getRawType();
        } else {
            throw new IllegalStateException("Unsupported type argument: " + typeArg.getTypeName());
        }
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
