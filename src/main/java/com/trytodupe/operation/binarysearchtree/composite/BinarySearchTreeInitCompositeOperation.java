package com.trytodupe.operation.binarysearchtree.composite;

import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.binarysearchtree.user.BinarySearchTreeInsertUserOperation;

import java.util.Arrays;
import java.util.List;

/**
 * Initialize binary search tree with given values.
 * Values are inserted in order using BST insertion logic.
 */
public class BinarySearchTreeInitCompositeOperation<E extends Comparable<E>> extends CompositeUserOperation<BinarySearchTreeStructure<E>> {

    private final List<E> values;

    public BinarySearchTreeInitCompositeOperation(BinarySearchTreeStructure<E> dataStructure, E[] values) {
        super(dataStructure);
        this.values = Arrays.asList(values);
    }

    @Override
    protected void buildOperations() {
        dataStructure.clear();

        for (E value : values) {
            super.childOperations.add(new BinarySearchTreeInsertUserOperation<>(dataStructure, value));
        }
    }

    @Override
    public String getDescription() {
        return "Initialize binary search tree with given values";
    }
}
