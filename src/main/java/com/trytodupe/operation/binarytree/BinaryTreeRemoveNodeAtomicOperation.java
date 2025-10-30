package com.trytodupe.operation.binarytree;

import com.trytodupe.datastructure.BinaryTreeNode;
import com.trytodupe.datastructure.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeRemoveNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {

    private String uuid;

    public BinaryTreeRemoveNodeAtomicOperation () {
    }

    @Override
    public void execute (BinaryTreeStructure<E> binaryTreeStructure) {
        uuid = binaryTreeStructure.popTempNode().getUUID().toString();
    }

    @Override
    public void undo (BinaryTreeStructure<E> binaryTreeStructure) {
        BinaryTreeNode<E> node = binaryTreeStructure.addNode(UUID.fromString(uuid));
        binaryTreeStructure.pushTempNode(node);
    }

    @Override
    public String getDescription () {
        return "Remove node at temp slot";
    }
}
