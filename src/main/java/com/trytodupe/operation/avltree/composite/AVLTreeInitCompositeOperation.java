package com.trytodupe.operation.avltree.composite;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.avltree.user.AVLTreeBalanceUserOperation;
import com.trytodupe.operation.avltree.user.AVLTreeUpdateTreeUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;

import java.util.Arrays;
import java.util.List;

/**
 * Initialize AVL tree with given values.
 * Each insertion is followed by tree update and balancing.
 */
public class AVLTreeInitCompositeOperation<E extends Comparable<E>> extends CompositeUserOperation<AVLTreeStructure<E>> {

    private final List<E> values;

    public AVLTreeInitCompositeOperation(AVLTreeStructure<E> avlTreeStructure, E[] values) {
        super(avlTreeStructure);
        this.values = Arrays.asList(values);
    }

    @Override
    protected void buildOperations() {
        for (E value : values) {
            // Insert using BST logic (AVL is a BST)
            BinarySearchTreeInsertUserOperation<E> insertOp = new BinarySearchTreeInsertUserOperation<>(dataStructure, value);
            String insertedUUID = insertOp.getUUID();
            super.childOperations.add(insertOp);

            // Update heights from inserted node to root
            super.childOperations.add(new AVLTreeUpdateTreeUserOperation<>(dataStructure, insertedUUID));

            // Rebalance the tree if necessary
            super.childOperations.add(new AVLTreeBalanceUserOperation<>(dataStructure, insertedUUID));
        }
    }

    @Override
    public String getDescription() {
        return "Initialize AVL tree with given values";
    }
}
