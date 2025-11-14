package com.trytodupe.operation.binarysearchtree.user;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeNode;
import com.trytodupe.datastructure.tree.SimpleBinarySearchNode;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeAddNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeConnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;

import java.util.UUID;

public class BinarySearchTreeInsertUserOperation extends UserOperation<BinarySearchTreeStructure<Integer>> {

    private final String uuid;
    private final Integer value;
    private String parentUUID;
    private BinaryTreeNode.ChildType childType;
    public BinarySearchTreeInsertUserOperation(BinarySearchTreeStructure<Integer> binarySearchTreeStructure, Integer value) {
        super(binarySearchTreeStructure);
        this.uuid = UUID.randomUUID().toString();
        this.value = value;
        this.description = "Insert " + value + " into BST";
    }

    @Override
    protected void buildOperations () {
        super.atomicOperations.add(new BinaryTreeAddNodeAtomicOperation<>(uuid));

        // check if initialised
        if (parentUUID == null) {
            SimpleBinarySearchNode<Integer> parent = super.dataStructure.getInsertParent(value);

            // check if is root
            if (parent != null) {
                parentUUID = parent.getUUID().toString();
                childType = value < super.dataStructure.getNode(UUID.fromString(parentUUID)).getValue()
                        ? BinaryTreeNode.ChildType.LEFT : BinaryTreeNode.ChildType.RIGHT;
            } else {
                parentUUID = null;
            }
        }

        super.atomicOperations.add(new BinaryTreeConnectNodeAtomicOperation<>(parentUUID, childType));

        super.atomicOperations.add(new BinaryTreeUpdateValueAtomicOperation<>(uuid, value));
    }

    public String getUUID() {
        return this.uuid;
    }
}
