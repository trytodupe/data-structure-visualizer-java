package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeRemoveNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {

    private String uuid;
    private E value;

    public BinaryTreeRemoveNodeAtomicOperation () {
    }

    @Override
    public void execute (BinaryTreeStructure<E> binaryTreeStructure) {
        uuid = binaryTreeStructure.popTempNode().getUUID().toString();
        value = binaryTreeStructure.getNode(UUID.fromString(uuid)).getValue();
        binaryTreeStructure.removeNode(UUID.fromString(uuid));
    }

    @Override
    public void undo (BinaryTreeStructure<E> binaryTreeStructure) {
        binaryTreeStructure.addNode(UUID.fromString(uuid), value);
        binaryTreeStructure.pushTempNode(binaryTreeStructure.getNode(UUID.fromString(uuid)));
    }

    @Override
    public String getDescription () {
        return "Remove node at temp slot";
    }
}
