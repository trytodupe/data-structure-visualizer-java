package com.trytodupe.operation.avltree.composite;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.avltree.user.AVLTreeBalanceUserOperation;
import com.trytodupe.operation.avltree.user.AVLTreeUpdateTreeUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;

public class AVLTreeInitCompositeOperation extends CompositeUserOperation<AVLTreeStructure<Integer>> {

    private final Integer[] values;

    public AVLTreeInitCompositeOperation (AVLTreeStructure<Integer> avlTreeStructure, Integer[] values) {
        super(avlTreeStructure);

        this.values = values;
    }

    @Override
    protected void buildOperations () {
        for (Integer value : values) {
            BinarySearchTreeInsertUserOperation insertOp = new BinarySearchTreeInsertUserOperation(super.dataStructure, value);
            String insertedUUID = insertOp.getUUID();
            super.childOperations.add(insertOp);

            super.childOperations.add(new AVLTreeUpdateTreeUserOperation(dataStructure, insertedUUID));

            AVLTreeBalanceUserOperation balanceOp = new AVLTreeBalanceUserOperation(dataStructure, insertedUUID);
            super.childOperations.add(balanceOp);
        }
    }

    @Override
    public String getDescription() {
        return "Initialize AVL tree with given values";
    }
}
