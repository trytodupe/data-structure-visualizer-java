package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.SimpleBinarySearchNode;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeUpdateValueAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<SimpleBinarySearchNode<E>, E>> {


    private final String uuid;
    private E oldValue;
    private final E newValue;

    public BinaryTreeUpdateValueAtomicOperation (String uuid, E newValue) {
        this.uuid = uuid;
        this.newValue = newValue;
    }

    @Override
    public void execute (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> dataStructure) {
        SimpleBinarySearchNode<E> node = dataStructure.getNode(UUID.fromString(uuid));
        oldValue = node.getValue();
        node.setValue(newValue);
    }

    @Override
    public void undo (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> dataStructure) {
        SimpleBinarySearchNode<E> node = dataStructure.getNode(UUID.fromString(uuid));
        node.setValue(oldValue);
    }

    @Override
    public String getDescription () {
        return "Update value of node " + uuid + " to " + newValue;
    }
}
