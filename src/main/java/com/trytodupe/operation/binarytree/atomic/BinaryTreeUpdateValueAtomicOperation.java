package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

import java.util.UUID;

public class BinaryTreeUpdateValueAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {

    private final String uuid; // highlight
    private E oldValue;
    private final E newValue;

    public BinaryTreeUpdateValueAtomicOperation (String uuid, E newValue) {
        this.uuid = uuid;
        this.newValue = newValue;
    }

    @Override
    public void execute (BinaryTreeStructure<E> dataStructure) {
        BinaryTreeNode<E> node = dataStructure.getNode(UUID.fromString(uuid));
        oldValue = node.getValue();
        node.setValue(newValue);
    }

    @Override
    public void undo (BinaryTreeStructure<E> dataStructure) {
        BinaryTreeNode<E> node = dataStructure.getNode(UUID.fromString(uuid));
        node.setValue(oldValue);
    }

    @Override
    public String getDescription () {
        return "Update value of node " + uuid + " to " + newValue;
    }

    @Override
    public void accept (IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
