package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.BinaryTreeNode;
import com.trytodupe.datastructure.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeDisconnectNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {

    private final String parentUUID;
    private final String childUUID;
    private BinaryTreeNode.ChildType childType;

    public BinaryTreeDisconnectNodeAtomicOperation (String parentUUID, String childUUID) {
        this.parentUUID = parentUUID;
        this.childUUID = childUUID;
    }

    @Override
    public void execute (BinaryTreeStructure<E> binaryTreeStructure) {
        BinaryTreeNode<E> parent = binaryTreeStructure.getNode(UUID.fromString(parentUUID));
        BinaryTreeNode<E> child = binaryTreeStructure.getNode(UUID.fromString(childUUID));

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
    public void undo (BinaryTreeStructure<E> binaryTreeStructure) {
        BinaryTreeNode<E> parent = binaryTreeStructure.getNode(UUID.fromString(parentUUID));
        BinaryTreeNode<E> child = binaryTreeStructure.popTempNode();

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
