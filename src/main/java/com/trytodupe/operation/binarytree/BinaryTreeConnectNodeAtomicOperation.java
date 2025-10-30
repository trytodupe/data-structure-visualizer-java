package com.trytodupe.operation.binarytree;

import com.trytodupe.datastructure.BinaryTreeNode;
import com.trytodupe.datastructure.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class BinaryTreeConnectNodeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {
    
    private final String parentUUID;
    private String childUUID;
    private final BinaryTreeNode.ChildType childType;

    public BinaryTreeConnectNodeAtomicOperation (String parentUUID, BinaryTreeNode.ChildType childType) {
        this.parentUUID = parentUUID;
        this.childType = childType;
    }

    @Override
    public void execute (BinaryTreeStructure<E> binaryTreeStructure) {
        BinaryTreeNode<E> parent = binaryTreeStructure.getNode(UUID.fromString(parentUUID));
        BinaryTreeNode<E> child = binaryTreeStructure.popTempNode();
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
    public void undo (BinaryTreeStructure<E> binaryTreeStructure) {
        BinaryTreeNode<E> parent = binaryTreeStructure.getNode(UUID.fromString(parentUUID));
        BinaryTreeNode<E> child = binaryTreeStructure.getNode(UUID.fromString(childUUID));

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
        return "Connect temp node to parent node " + parentUUID + " as " + childType.toString();
    }
}
