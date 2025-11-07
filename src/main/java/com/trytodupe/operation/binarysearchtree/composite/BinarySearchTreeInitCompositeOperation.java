package com.trytodupe.operation.binarysearchtree.composite;

import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;

public class BinarySearchTreeInitCompositeOperation extends CompositeUserOperation<BinarySearchTreeStructure<Integer>> {

    // values should not contain nulls for BST
    private final Integer[] values;

    public BinarySearchTreeInitCompositeOperation (BinarySearchTreeStructure<Integer> dataStructure, Integer[] values) {
        super(dataStructure);

        this.values = values;
    }

    @Override
    protected void buildOperations () {
        for (Integer value : values) {
            super.childOperations.add(new BinarySearchTreeInsertUserOperation(dataStructure, value));
        }
    }

    @Override
    public String getDescription() {
        return "Initialize binary search tree with given values";
    }
}
