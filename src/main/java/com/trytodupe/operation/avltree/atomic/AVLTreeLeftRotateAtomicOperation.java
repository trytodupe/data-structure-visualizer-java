package com.trytodupe.operation.avltree.atomic;

import com.trytodupe.datastructure.tree.AVLTreeNode;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class AVLTreeLeftRotateAtomicOperation<E> extends AtomicOperation<AVLTreeStructure<E>> {

    private final String pivotUUID;
    private String oldParentUUID;
    private String newRootUUID;
    private boolean pivotWasLeftChild;

    public AVLTreeLeftRotateAtomicOperation (String pivotUUID) {
        this.pivotUUID = pivotUUID;
    }

    @Override
    public void execute (AVLTreeStructure<E> avlTreeStructure) {
        AVLTreeNode<E> pivot = avlTreeStructure.getNode(UUID.fromString(pivotUUID));
        AVLTreeNode<E> newRoot = pivot.getRight();
        if (newRoot == null) {
            throw new IllegalStateException("Cannot rotate left because right child is null");
        }

        // Store state for undo
        newRootUUID = newRoot.getUUID().toString();
        AVLTreeNode<E> parent = avlTreeStructure.getParent(UUID.fromString(pivotUUID));

        if (parent != null) {
            oldParentUUID = parent.getUUID().toString();
            pivotWasLeftChild = parent.getLeft() == pivot;
        } else {
            // pivot is the root
            oldParentUUID = null;
            pivotWasLeftChild = false;
        }

        // Perform the rotation
        AVLTreeNode<E> newRootLeftChild = newRoot.getLeft();

        // Step 1: Move newRoot's left child to pivot's right
        pivot.setRight(newRootLeftChild);

        // Step 2: Move pivot to newRoot's left
        newRoot.setLeft(pivot);

        // Step 3: Update parent's reference
        if (parent != null) {
            // Replace pivot with newRoot in parent
            if (pivotWasLeftChild) {
                parent.setLeft(newRoot);
            } else {
                parent.setRight(newRoot);
            }
        } else {
            // pivot was the root, so newRoot becomes the new root
            avlTreeStructure.setRoot(newRoot);
        }
    }

    @Override
    public void undo (AVLTreeStructure<E> avlTreeStructure) {
        AVLTreeNode<E> newRoot = avlTreeStructure.getNode(UUID.fromString(newRootUUID));
        AVLTreeNode<E> pivot = newRoot.getLeft();

        if (pivot == null) {
            throw new IllegalStateException("Cannot undo rotation: pivot node is null");
        }

        // Reverse the rotation
        AVLTreeNode<E> pivotRightChild = pivot.getRight();

        // Step 1: Move pivot's right child back to newRoot's left
        newRoot.setLeft(pivotRightChild);

        // Step 2: Move newRoot back to pivot's right
        pivot.setRight(newRoot);

        // Step 3: Update parent's reference
        AVLTreeNode<E> parent = oldParentUUID != null ?
            avlTreeStructure.getNode(UUID.fromString(oldParentUUID)) : null;

        if (parent != null) {
            // Restore pivot as parent's child
            if (pivotWasLeftChild) {
                parent.setLeft(pivot);
            } else {
                parent.setRight(pivot);
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
}
