package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

import java.util.UUID;

public class BinaryTreeSwapValueAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {

    private final String uuid1, uuid2; // highlight

    public BinaryTreeSwapValueAtomicOperation (String uuid1, String uuid2) {
        this.uuid1 = uuid1;
        this.uuid2 = uuid2;
    }

    public String getUuid1 () {
        return uuid1;
    }

    public String getUuid2 () {
        return uuid2;
    }

    @Override
    public void execute (BinaryTreeStructure<E> binaryTreeStructure) {
        BinaryTreeNode<E> node1 = binaryTreeStructure.getNode(UUID.fromString(uuid1));
        BinaryTreeNode<E> node2 = binaryTreeStructure.getNode(UUID.fromString(uuid2));
        E tmp = node1.getValue();
        node1.setValue(node2.getValue());
        node2.setValue(tmp);
    }

    @Override
    public void undo (BinaryTreeStructure<E> binaryTreeStructure) {
        execute(binaryTreeStructure); // swapping again will revert to original
    }

    @Override
    public String getDescription () {
        return "Swap values between nodes " + uuid1 + " and " + uuid2;
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
