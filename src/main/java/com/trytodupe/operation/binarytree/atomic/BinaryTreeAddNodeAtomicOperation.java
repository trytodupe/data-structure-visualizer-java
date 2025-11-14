package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.SimpleBinarySearchNode;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeAddNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<SimpleBinarySearchNode<E>, E>> {

    // use String to avoid serialization issues
    private final String uuid;

    public BinaryTreeAddNodeAtomicOperation (String uuid) {
        if (uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        } else {
            this.uuid = uuid;
        }
    }

    @Override
    public void execute (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        SimpleBinarySearchNode<E> node = binaryTreeStructure.addNode(UUID.fromString(uuid));
        binaryTreeStructure.pushTempNode(node);
    }

    @Override
    public void undo (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        SimpleBinarySearchNode<E> node = binaryTreeStructure.popTempNode();
        binaryTreeStructure.removeNode(node.getUUID());
    }

    @Override
    public String getDescription () {
        return "Create a new node at temp slot";
    }

    public String getUUID() {
        return this.uuid;
    }
}
