package com.trytodupe.operation.binarytree.user;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeDisconnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeRemoveNodeAtomicOperation;

public class BinaryTreeDeleteUserOperation<E> extends UserOperation<BinaryTreeStructure<E>> {

    private final String parentUUID;
    private final String childUUID;

    public BinaryTreeDeleteUserOperation(BinaryTreeStructure<E> binaryTreeStructure,
                                        String parentUUID,
                                        String childUUID) {
        super(binaryTreeStructure);
        this.parentUUID = parentUUID;
        this.childUUID = childUUID;
        this.description = "Delete node " + childUUID + " from parent " + (parentUUID != null ? parentUUID : "root");
    }

    @Override
    protected void buildOperations () {
        super.atomicOperations.add(new BinaryTreeDisconnectNodeAtomicOperation<>(parentUUID, childUUID));
        super.atomicOperations.add(new BinaryTreeRemoveNodeAtomicOperation<>());
    }
}
