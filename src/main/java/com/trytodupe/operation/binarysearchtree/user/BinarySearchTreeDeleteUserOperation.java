package com.trytodupe.operation.binarysearchtree.user;

import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeDisconnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeRemoveNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeReplaceSubtreeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeSwapValueAtomicOperation;

import java.util.UUID;

public class BinarySearchTreeDeleteUserOperation<E extends Comparable<E>> extends UserOperation<BinarySearchTreeStructure<E>> {

    private final String uuid;
    private String parentUUID;
    private int childCount = -1;

    public BinarySearchTreeDeleteUserOperation (BinarySearchTreeStructure<E> dataStructure, String uuid) {
        super(dataStructure);
        this.uuid = uuid;
    }

    @Override
    protected void buildOperations () {
        BinaryTreeNode<E> node = super.dataStructure.getNode(UUID.fromString(uuid));
        if (childCount == -1) {
            childCount = node.getChildCount();
        }

        BinaryTreeNode<E> parent = super.dataStructure.getParent(UUID.fromString(uuid));
        parentUUID = (parent == null) ? null : parent.getUUID().toString();

        switch (childCount) {
            case 0:
                atomicOperations.add(new BinaryTreeDisconnectNodeAtomicOperation<>(parentUUID, uuid));
                atomicOperations.add(new BinaryTreeRemoveNodeAtomicOperation<>());
                break;
            case 1:
                BinaryTreeNode<E> child = node.getLeft() != null ? node.getLeft() : node.getRight();

                atomicOperations.add(new BinaryTreeReplaceSubtreeAtomicOperation<>(uuid, child.getUUID().toString()));
                atomicOperations.add(new BinaryTreeDisconnectNodeAtomicOperation<>(parentUUID, uuid));
                atomicOperations.add(new BinaryTreeRemoveNodeAtomicOperation<>());
                break;
            case 2:
                BinaryTreeNode<E> successor = super.dataStructure.getSuccessor(node);
                String successorUUID = successor.getUUID().toString();
                BinaryTreeNode<E> successorParent = super.dataStructure.getParent(UUID.fromString(successorUUID));

                // todo: animation
                atomicOperations.add(new BinaryTreeSwapValueAtomicOperation<>(uuid, successor.getUUID().toString()));

                // deleting successor
                if (successor.getRight() != null) {
                    // back to case 1
                    atomicOperations.add(new BinaryTreeReplaceSubtreeAtomicOperation<>
                            (successor.getUUID().toString(), successor.getRight().getUUID().toString()));
                    atomicOperations.add(new BinaryTreeDisconnectNodeAtomicOperation<>(successorParent.getUUID().toString(), successorUUID));
                    atomicOperations.add(new BinaryTreeRemoveNodeAtomicOperation<>());
                } else {
                    // back to case 0
                    atomicOperations.add(new BinaryTreeDisconnectNodeAtomicOperation<>
                            (successorParent.getUUID().toString(), successor.getUUID().toString()));
                    atomicOperations.add(new BinaryTreeRemoveNodeAtomicOperation<>());
                }

                break;
            default:
                throw new IllegalStateException("Node has invalid number of children: " + childCount);
        }

    }
}
