package com.trytodupe.operation.binarysearchtree.user;

import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeAddNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeConnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;
import com.trytodupe.operation.utils.VisualizePathAtomicOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BST-specific insert operation.
 * Automatically finds the correct parent and child type based on BST properties.
 */
public class BinarySearchTreeInsertUserOperation<E extends Comparable<E>> extends UserOperation<BinarySearchTreeStructure<E>> {

    private final String uuid;
    private final E value;
    private String parentUUID;
    private BinaryTreeNode.ChildType childType;

    public BinarySearchTreeInsertUserOperation(BinarySearchTreeStructure<E> binarySearchTreeStructure, E value) {
        super(binarySearchTreeStructure);
        this.uuid = UUID.randomUUID().toString();
        this.value = value;
        this.description = "Insert " + value + " into BST";
    }

    @Override
    protected void buildOperations () {
        // Find the insertion path and parent
        List<String> path = new ArrayList<>();
        BinaryTreeNode<E> parent = super.dataStructure.getInsertParent(value, path);

        super.atomicOperations.add(new BinaryTreeAddNodeAtomicOperation<>(uuid));

        // Resolve parent and child type for BST insertion
        if (parentUUID == null) {
            // check if is root
            if (parent != null) {
                parentUUID = parent.getUUID().toString();
                childType = value.compareTo(super.dataStructure.getNode(UUID.fromString(parentUUID)).getValue()) < 0
                        ? BinaryTreeNode.ChildType.LEFT : BinaryTreeNode.ChildType.RIGHT;
            } else {
                parentUUID = null;
            }
        }

        super.atomicOperations.add(new VisualizePathAtomicOperation(path));

        super.atomicOperations.add(new BinaryTreeConnectNodeAtomicOperation<>(parentUUID, childType));

        super.atomicOperations.add(new BinaryTreeUpdateValueAtomicOperation<>(uuid, value));
    }

    public String getUUID() {
        return this.uuid;
    }
}
