package com.trytodupe.operation.binarytree;

import com.trytodupe.datastructure.BinaryTreeNode;
import com.trytodupe.datastructure.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeAddNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {

    // use String to avoid serialization issues
    private final String uuid;

    public BinaryTreeAddNodeAtomicOperation () {
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public void execute (BinaryTreeStructure<E> binaryTreeStructure) {
        BinaryTreeNode<E> node = binaryTreeStructure.addNode(UUID.fromString(uuid));
        binaryTreeStructure.pushTempNode(node);
    }

    @Override
    public void undo (BinaryTreeStructure<E> binaryTreeStructure) {
        binaryTreeStructure.popTempNode();
    }

    @Override
    public String getDescription () {
        return "Create a new node at temp slot";
    }

    public String getUUID() {
        return this.uuid;
    }
}
