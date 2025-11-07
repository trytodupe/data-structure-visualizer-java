package com.trytodupe.operation.binarytree.user;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;

public class BinaryTreeUpdateValueUserOperation extends UserOperation<BinaryTreeStructure<Integer>> {

    private final String nodeUUID;
    private final Integer newValue;

    public BinaryTreeUpdateValueUserOperation(BinaryTreeStructure<Integer> binaryTreeStructure,
                                              String nodeUUID,
                                              Integer newValue) {
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
