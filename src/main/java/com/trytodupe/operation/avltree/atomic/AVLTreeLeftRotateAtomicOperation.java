package com.trytodupe.operation.avltree.atomic;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

import java.util.UUID;

public class AVLTreeLeftRotateAtomicOperation<E extends Comparable<E>> extends AtomicOperation<AVLTreeStructure<E>> {

    private final String pivotUUID; // highlight
    private String oldParentUUID;
    private String newRootUUID;
    private boolean pivotWasLeftChild;

    public AVLTreeLeftRotateAtomicOperation (String pivotUUID) {
        this.pivotUUID = pivotUUID;
    }

    public String getPivotUUID () {
        return pivotUUID;
    }

    @Override
    public void execute (AVLTreeStructure<E> avlTreeStructure) {
        BinaryTreeNode<E> pivot = avlTreeStructure.getNode(UUID.fromString(pivotUUID));
        BinaryTreeNode<E> newRoot = pivot.getRight();
        if (newRoot == null) {
            throw new IllegalStateException("Cannot rotate left because right child is null");
        }

        // Store state for undo
        newRootUUID = newRoot.getUUID().toString();
        BinaryTreeNode<E> parent = avlTreeStructure.getParent(pivot.getUUID());

        if (parent != null) {
            oldParentUUID = parent.getUUID().toString();
            pivotWasLeftChild = parent.getLeft() == pivot;
        } else {
            // pivot is the root
            oldParentUUID = null;
            pivotWasLeftChild = false;
        }

        // Perform the rotation
        BinaryTreeNode<E> newRootLeftChild = newRoot.getLeft();

        // Step 1: Move newRoot's left child to pivot's right
        pivot.forceSetRight(newRootLeftChild);

        // Step 2: Move pivot to newRoot's left
        newRoot.forceSetLeft(pivot);

        // Step 3: Update parent's reference
        if (parent != null) {
            // Replace pivot with newRoot in parent
            if (pivotWasLeftChild) {
                parent.forceSetLeft(newRoot);
            } else {
                parent.forceSetRight(newRoot);
            }
        } else {
            // pivot was the root, so newRoot becomes the new root
            avlTreeStructure.setRoot(newRoot);
        }
    }

    @Override
    public void undo (AVLTreeStructure<E> avlTreeStructure) {
        BinaryTreeNode<E> newRoot = avlTreeStructure.getNode(UUID.fromString(newRootUUID));
        BinaryTreeNode<E> pivot = newRoot.getLeft();

        if (pivot == null) {
            throw new IllegalStateException("Cannot undo rotation: pivot node is null");
        }

        // Reverse the rotation
        BinaryTreeNode<E> pivotRightChild = pivot.getRight();

        // Step 1: Move pivot's right child back to newRoot's left
        newRoot.forceSetLeft(pivotRightChild);

        // Step 2: Move newRoot back to pivot's right
        pivot.forceSetRight(newRoot);

        // Step 3: Update parent's reference
        BinaryTreeNode<E> parent = oldParentUUID != null ?
            avlTreeStructure.getNode(UUID.fromString(oldParentUUID)) : null;

        if (parent != null) {
            // Restore pivot as parent's child
            if (pivotWasLeftChild) {
                parent.forceSetLeft(pivot);
            } else {
                parent.forceSetRight(pivot);
            }
        } else {
            // pivot was the root, restore it
            avlTreeStructure.setRoot(pivot);
        }
    }

    @Override
    public String getDescription () {
        return "AVL Tree Left Rotation on pivot: " + pivotUUID;
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
