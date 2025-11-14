package com.trytodupe.operation.avltree.atomic;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class AVLTreeRightRotateAtomicOperation<E extends Comparable<E>> extends AtomicOperation<AVLTreeStructure<E>> {

    private final String pivotUUID;
    private String oldParentUUID;
    private String newRootUUID;
    private boolean pivotWasLeftChild;

    public AVLTreeRightRotateAtomicOperation (String pivotUUID) {
        this.pivotUUID = pivotUUID;
    }

    @Override
    public void execute (AVLTreeStructure<E> avlTreeStructure) {
        BinaryTreeNode<E> pivot = avlTreeStructure.getNode(UUID.fromString(pivotUUID));
        BinaryTreeNode<E> newRoot = pivot.getLeft();
        if (newRoot == null) {
            throw new IllegalStateException("Cannot rotate right because left child is null");
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
        BinaryTreeNode<E> newRootRightChild = newRoot.getRight();

        // Step 1: Move newRoot's right child to pivot's left
        pivot.setLeft(newRootRightChild);

        // Step 2: Move pivot to newRoot's right
        newRoot.setRight(pivot);

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
        BinaryTreeNode<E> newRoot = avlTreeStructure.getNode(UUID.fromString(newRootUUID));
        BinaryTreeNode<E> pivot = newRoot.getRight();

        if (pivot == null) {
            throw new IllegalStateException("Cannot undo rotation: pivot node is null");
        }

        // Reverse the rotation
        BinaryTreeNode<E> pivotLeftChild = pivot.getLeft();

        // Step 1: Move pivot's left child back to newRoot's right
        newRoot.setRight(pivotLeftChild);

        // Step 2: Move newRoot back to pivot's left
        pivot.setLeft(newRoot);

        // Step 3: Update parent's reference
        BinaryTreeNode<E> parent = oldParentUUID != null ?
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
        return "AVL Tree Right Rotation on pivot: " + pivotUUID;
    }
}
