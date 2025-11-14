package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeSwapValueAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<SimpleBinarySearchNode<E>, E>> {

    private final String uuid1, uuid2;

    public BinaryTreeSwapValueAtomicOperation (String uuid1, String uuid2) {
        this.uuid1 = uuid1;
        this.uuid2 = uuid2;
    }

    @Override
    public void execute (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        SimpleBinarySearchNode<E> node1 = binaryTreeStructure.getNode(UUID.fromString(uuid1));
        SimpleBinarySearchNode<E> node2 = binaryTreeStructure.getNode(UUID.fromString(uuid2));
        E tmp = node1.getValue();
        node1.setValue(node2.getValue());
        node2.setValue(tmp);
    }

    @Override
    public void undo (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        execute(binaryTreeStructure); // swapping again will revert to original
    }

    @Override
    public String getDescription () {
        return "";
    }
}
