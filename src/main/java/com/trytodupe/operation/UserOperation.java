package com.trytodupe.operation;

import com.trytodupe.Main;
import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.serialization.ISerializable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class UserOperation<T extends DataStructure> implements ISerializable {

    protected transient T dataStructure;

    protected String description;

    protected transient List<AtomicOperation<? super T>> atomicOperations;

    protected transient boolean built = false;


    public UserOperation(T dataStructure) {
        this.dataStructure = dataStructure;
        this.atomicOperations = new ArrayList<>();
    }

    protected abstract void buildOperations ();

    public void execute() {
        if (!built) {
            buildOperations();
            built = true;
        }

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

    public String getDescription() {
        return description;
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
