package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.SimpleBinarySearchNode;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeRemoveNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<SimpleBinarySearchNode<E>, E>> {

    private String uuid;
    private E value;

    public BinaryTreeRemoveNodeAtomicOperation () {
    }

    @Override
    public void execute (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        uuid = binaryTreeStructure.popTempNode().getUUID().toString();
        value = binaryTreeStructure.getNode(UUID.fromString(uuid)).getValue();
        binaryTreeStructure.removeNode(UUID.fromString(uuid));
    }

    @Override
    public void undo (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        SimpleBinarySearchNode<E> node = binaryTreeStructure.addNode(UUID.fromString(uuid), value);
        binaryTreeStructure.pushTempNode(node);
    }

    @Override
    public String getDescription () {
        return "Remove node at temp slot";
    }
}
