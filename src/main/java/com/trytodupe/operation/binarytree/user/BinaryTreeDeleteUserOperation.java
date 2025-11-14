package com.trytodupe.operation.binarytree.user;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.SimpleBinarySearchNode;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeDisconnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeRemoveNodeAtomicOperation;

public class BinaryTreeDeleteUserOperation extends UserOperation<BinaryTreeStructure<SimpleBinarySearchNode<Integer>, Integer>> {

    private final String parentUUID;
    private final String childUUID;

    public BinaryTreeDeleteUserOperation(BinaryTreeStructure<SimpleBinarySearchNode<Integer>, Integer> binaryTreeStructure,
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
