package com.trytodupe.operation.avltree.user;

import com.trytodupe.datastructure.tree.AVLTreeNode;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeLeftRotateAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeRightRotateAtomicOperation;
import com.trytodupe.operation.avltree.atomic.AVLTreeUpdateNodeAtomicOperation;

import java.util.UUID;

public class AVLTreeBalanceUserOperation extends UserOperation<AVLTreeStructure<Integer>> {

    private final String insertedUUID;
    private String unbalancedUUID;

    public AVLTreeBalanceUserOperation (AVLTreeStructure<Integer> avlTreeStructure, String insertedUUID) {
        super(avlTreeStructure);
        this.insertedUUID = insertedUUID;
    }

    @Override
    protected void buildOperations () {
        AVLTreeNode<Integer> insertedNode = dataStructure.getNode(UUID.fromString(insertedUUID));
        AVLTreeNode<Integer> unbalancedNode = dataStructure.findFirstUnbalancedNode(insertedNode);

        if (unbalancedNode == null) return; // 树已经平衡，无需旋转
        unbalancedUUID = UUID.randomUUID().toString();

        int balance = dataStructure.getBalance(UUID.fromString(unbalancedUUID));

        if (balance > 1) { // 左子树过高
            AVLTreeNode<Integer> leftChild = unbalancedNode.getLeft();
            if (dataStructure.getBalance(leftChild.getUUID()) >= 0) {
                // LL 情况
                atomicOperations.add(new AVLTreeRightRotateAtomicOperation<>(unbalancedUUID));
            } else {
                // LR 情况
                atomicOperations.add(new AVLTreeLeftRotateAtomicOperation<>(leftChild.getUUID().toString()));
                atomicOperations.add(new AVLTreeRightRotateAtomicOperation<>(unbalancedUUID));
            }
        } else if (balance < -1) { // 右子树过高
            AVLTreeNode<Integer> rightChild = unbalancedNode.getRight();
            if (dataStructure.getBalance(rightChild.getUUID()) <= 0) {
                // RR 情况
                atomicOperations.add(new AVLTreeLeftRotateAtomicOperation<>(unbalancedUUID));
            } else {
                // RL 情况
                atomicOperations.add(new AVLTreeRightRotateAtomicOperation<>(rightChild.getUUID().toString()));
                atomicOperations.add(new AVLTreeLeftRotateAtomicOperation<>(unbalancedUUID));
            }
        }
        // 高度更新操作
        AVLTreeNode<Integer> current = unbalancedNode;
        while (current != null) {
            int newHeight = current.computeHeight();
            atomicOperations.add(new AVLTreeUpdateNodeAtomicOperation<>(current.getUUID().toString(), newHeight));
            current = (AVLTreeNode<Integer>) super.dataStructure.getParent(UUID.fromString(insertedUUID));
        }
    }
}
