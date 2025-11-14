package com.trytodupe.operation.avltree.user;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.node.AVLNodeExtension;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeUpdateNodeAtomicOperation;

import java.util.UUID;

/**
 * Update heights of all nodes from the inserted node up to the root.
 * This is necessary before checking balance factors in AVL trees.
 */
public class AVLTreeUpdateTreeUserOperation<E extends Comparable<E>> extends UserOperation<AVLTreeStructure<E>> {

    private final String insertedUUID;

    public AVLTreeUpdateTreeUserOperation(AVLTreeStructure<E> avlTreeStructure, String insertedUUID) {
        super(avlTreeStructure);
        this.insertedUUID = insertedUUID;
        this.description = "Update heights for tree after inserting " + insertedUUID;
    }

    @Override
    protected void buildOperations() {
        BinaryTreeNode<E> current = super.dataStructure.getNode(UUID.fromString(insertedUUID));

        // Traverse from inserted node to root, updating heights
        while (current != null) {
            int newHeight = current.computeHeight();
            super.atomicOperations.add(new AVLTreeUpdateNodeAtomicOperation<>(current.getUUID().toString(), newHeight));

            current = (AVLTreeNode<Integer>) super.dataStructure.getParent(UUID.fromString(insertedUUID));
        }
    }
}
