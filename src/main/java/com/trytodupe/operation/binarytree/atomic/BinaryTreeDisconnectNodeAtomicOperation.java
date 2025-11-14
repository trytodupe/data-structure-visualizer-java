package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.BinaryTreeNode;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.datastructure.tree.SimpleBinarySearchNode;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeDisconnectNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<SimpleBinarySearchNode<E>, E>> {

    private final String parentUUID;
    private final String childUUID;
    private BinaryTreeNode.ChildType childType;

    public BinaryTreeDisconnectNodeAtomicOperation (String parentUUID, String childUUID) {
        this.parentUUID = parentUUID;
        this.childUUID = childUUID;
    }

    @Override
    public void execute (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        if (parentUUID == null) {
            // push root node back to temp slot
            SimpleBinarySearchNode<E> node = binaryTreeStructure.getRoot();
            binaryTreeStructure.pushTempNode(node);
            binaryTreeStructure.setRoot(null);
            return;
        }

        SimpleBinarySearchNode<E> parent = binaryTreeStructure.getNode(UUID.fromString(parentUUID));
        SimpleBinarySearchNode<E> child = binaryTreeStructure.getNode(UUID.fromString(childUUID));

        childType = parent.isChild(child);

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
    public void undo (BinaryTreeStructure<SimpleBinarySearchNode<E>, E> binaryTreeStructure) {
        if (parentUUID == null) {
            // pop temp slot into root node
            SimpleBinarySearchNode<E> node = binaryTreeStructure.popTempNode();
            binaryTreeStructure.setRoot(node);
            return;
        }

        SimpleBinarySearchNode<E> parent = binaryTreeStructure.getNode(UUID.fromString(parentUUID));
        SimpleBinarySearchNode<E> child = binaryTreeStructure.popTempNode();

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
    public String getDescription () {
        return "Disconnect child node " + childUUID + " from parent node " + parentUUID + " and store it in temp slot";
    }
}
