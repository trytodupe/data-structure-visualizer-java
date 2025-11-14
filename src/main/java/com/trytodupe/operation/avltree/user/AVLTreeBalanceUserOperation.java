package com.trytodupe.operation.avltree.user;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeLeftRotateAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeRightRotateAtomicOperation;

import java.util.UUID;

/**
 * Rebalance AVL tree after insertion by finding unbalanced nodes and performing rotations.
 * Handles LL, LR, RR, and RL cases.
 */
public class AVLTreeBalanceUserOperation<E extends Comparable<E>> extends UserOperation<AVLTreeStructure<E>> {

    private final String insertedUUID;
    private String unbalancedUUID;

    public AVLTreeBalanceUserOperation(AVLTreeStructure<E> avlTreeStructure, String insertedUUID) {
        super(avlTreeStructure);
        this.insertedUUID = insertedUUID;
        this.description = "Rebalance tree after inserting " + insertedUUID;
    }

    @Override
    protected void buildOperations() {
        BinaryTreeNode<E> insertedNode = dataStructure.getNode(UUID.fromString(insertedUUID));
        BinaryTreeNode<E> unbalancedNode = dataStructure.findFirstUnbalancedNode(insertedNode);

        if (unbalancedNode == null) {
            return; // Tree is already balanced
        }

        unbalancedUUID = unbalancedNode.getUUID().toString();
        int balance = dataStructure.getBalance(unbalancedNode.getUUID());

        if (balance > 1) {
            // Left subtree is too high
            BinaryTreeNode<E> leftChild = unbalancedNode.getLeft();
            if (dataStructure.getBalance(leftChild.getUUID()) >= 0) {
                // LL case: single right rotation
                atomicOperations.add(new AVLTreeRightRotateAtomicOperation<>(unbalancedUUID));
            } else {
                // LR case: left-right rotation
                atomicOperations.add(new AVLTreeLeftRotateAtomicOperation<>(leftChild.getUUID().toString()));
                atomicOperations.add(new AVLTreeRightRotateAtomicOperation<>(unbalancedUUID));
            }
        } else if (balance < -1) {
            // Right subtree is too high
            BinaryTreeNode<E> rightChild = unbalancedNode.getRight();
            if (dataStructure.getBalance(rightChild.getUUID()) <= 0) {
                // RR case: single left rotation
                atomicOperations.add(new AVLTreeLeftRotateAtomicOperation<>(unbalancedUUID));
            } else {
                // RL case: right-left rotation
                atomicOperations.add(new AVLTreeRightRotateAtomicOperation<>(rightChild.getUUID().toString()));
                atomicOperations.add(new AVLTreeLeftRotateAtomicOperation<>(unbalancedUUID));
            }
        }
    }
}
