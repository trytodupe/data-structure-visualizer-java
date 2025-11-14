package com.trytodupe.operation.binarytree.user;

import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeAddNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeConnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;

import java.util.UUID;

/**
 * Generic insert operation for binary trees.
 * Can be used directly for unbalanced binary trees.
 * Subclasses can override to provide specialized behavior (BST, AVL, etc.)
 */
public class BinaryTreeInsertUserOperation<E> extends UserOperation<BinaryTreeStructure<E>> {

    protected String parentUUID;
    protected final String uuid;
    protected final E value;
    protected BinaryTreeNode.ChildType childType;

    /**
     * Insert with explicit parent and child type (for manual tree construction)
     */
    public BinaryTreeInsertUserOperation(BinaryTreeStructure<E> binaryTreeStructure,
                                        String parentUUID,
                                        E value,
                                        BinaryTreeNode.ChildType childType) {
        super(binaryTreeStructure);
        this.parentUUID = parentUUID;
        this.uuid = UUID.randomUUID().toString();
        this.value = value;
        this.childType = childType;
        this.description = "Insert " + value + " as " + childType + " child of " + (parentUUID != null ? parentUUID : "root");
    }

    @Override
    protected void buildOperations () {
        super.atomicOperations.add(new BinaryTreeAddNodeAtomicOperation<>(uuid));
        super.atomicOperations.add(new BinaryTreeConnectNodeAtomicOperation<>(parentUUID, childType));
        super.atomicOperations.add(new BinaryTreeUpdateValueAtomicOperation<>(uuid, value));
    }

    public String getUUID() {
        return this.uuid;
    }
}
