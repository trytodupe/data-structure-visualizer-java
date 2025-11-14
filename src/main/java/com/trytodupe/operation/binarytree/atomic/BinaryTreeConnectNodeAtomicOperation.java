package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeConnectNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<SimpleBinarySearchNode<E>, E>> {

    // a null string represents root node
    private final String parentUUID;
    private String childUUID;
    private final BinaryTreeNode.ChildType childType;

    public BinaryTreeConnectNodeAtomicOperation (String parentUUID, BinaryTreeNode.ChildType childType) {
        this.parentUUID = parentUUID;
        this.childType = childType;
    }

    @Override
    public void execute (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        if (parentUUID == null) {
            // pop temp slot into root node
            SimpleBinarySearchNode<E> node = binaryTreeStructure.popTempNode();
            binaryTreeStructure.setRoot(node);
            return;
        }

        SimpleBinarySearchNode<E> parent = binaryTreeStructure.getNode(UUID.fromString(parentUUID));
        SimpleBinarySearchNode<E> child = binaryTreeStructure.popTempNode();
        childUUID = child.getUUID().toString();

        // no need to store original child since it should be null
        switch (this.childType) {
            case LEFT:
                parent.setLeft(child);
                break;
            case RIGHT:
                parent.setRight(child);
                break;
        }
    }

    @Override
    public void undo (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        if (parentUUID == null) {
            // push root node back to temp slot
            SimpleBinarySearchNode<E> node = binaryTreeStructure.getRoot();
            binaryTreeStructure.pushTempNode(node);
            binaryTreeStructure.setRoot(null);
            return;
        }

        SimpleBinarySearchNode<E> parent = binaryTreeStructure.getNode(UUID.fromString(parentUUID));
        SimpleBinarySearchNode<E> child = binaryTreeStructure.getNode(UUID.fromString(childUUID));

        switch (this.childType) {
            case LEFT:
                parent.clearLeft();
                break;
            case RIGHT:
                parent.clearRight();
                break;
        }

        binaryTreeStructure.pushTempNode(child);

    }

    @Override
    public String getDescription () {
        return "Connect temp node to parent node " + parentUUID;
    }
}
