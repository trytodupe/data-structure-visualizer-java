package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

import java.util.UUID;

public class BinaryTreeAddNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {

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
    public void execute (BinaryTreeStructure<E> binaryTreeStructure) {
        binaryTreeStructure.addNode(UUID.fromString(uuid));
        binaryTreeStructure.pushTempNode(binaryTreeStructure.getNode(UUID.fromString(uuid)));
    }

    @Override
    public void undo (BinaryTreeStructure<E> binaryTreeStructure) {
        binaryTreeStructure.popTempNode();
        binaryTreeStructure.removeNode(UUID.fromString(uuid));
    }

    @Override
    public String getDescription () {
        return "Create a new node at temp slot";
    }

    public String getUUID() {
        return this.uuid;
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
