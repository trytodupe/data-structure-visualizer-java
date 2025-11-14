package com.trytodupe.operation.binarytree.user;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;

public class BinaryTreeUpdateValueUserOperation<E> extends UserOperation<BinaryTreeStructure<E>> {

    private final String nodeUUID;
    private final E newValue;

    public BinaryTreeUpdateValueUserOperation(BinaryTreeStructure<E> binaryTreeStructure,
                                              String nodeUUID,
                                              E newValue) {
        super(binaryTreeStructure);
        this.nodeUUID = nodeUUID;
        this.newValue = newValue;
        this.description = "Update value of node " + nodeUUID + " to " + newValue;
    }

    @Override
    protected void buildOperations () {
        super.atomicOperations.add(new BinaryTreeUpdateValueAtomicOperation<>(nodeUUID, newValue));
    }
}
