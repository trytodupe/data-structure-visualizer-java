package com.trytodupe.operation.avltree.user;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeUpdateNodeAtomicOperation;

import java.util.UUID;

public class AVLTreeUpdateTreeUserOperation extends UserOperation<AVLTreeStructure<Integer>> {

    private final String insertedUUID;

    public AVLTreeUpdateTreeUserOperation (AVLTreeStructure<Integer> avlTreeStructure, String insertedUUID) {
        super(avlTreeStructure);
        this.insertedUUID = insertedUUID;
    }

    @Override
    protected void buildOperations () {
        AVLTreeNode<Integer> current = super.dataStructure.getNode(UUID.fromString(insertedUUID));

        while (current != null) {
            int newHeight = current.computeHeight();
            super.atomicOperations.add(new AVLTreeUpdateNodeAtomicOperation<>(current.getUUID().toString(), newHeight));

            current = (AVLTreeNode<Integer>) super.dataStructure.getParent(UUID.fromString(insertedUUID));
        }

    }

    @Override
    public String getDescription () {
        return "Update Tree after inserting " + insertedUUID;
    }
}
