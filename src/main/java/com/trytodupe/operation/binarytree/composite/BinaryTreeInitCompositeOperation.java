package com.trytodupe.operation.binarytree.composite;

import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.binarytree.user.BinaryTreeInsertUserOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Initialize binary tree with given values using array-based level-order construction.
 * Supports null values as placeholders to maintain tree structure.
 *
 * Example: Integer[] values = {1, 2, 3, 4, 5, null, 7}
 * Creates a tree like:
 *       1
 *      / \
 *     2   3
 *    / \ /
 *   4  5 7
 */
public class BinaryTreeInitCompositeOperation<E> extends CompositeUserOperation<BinaryTreeStructure<E>> {

    private final List<E> values;



    public BinaryTreeInitCompositeOperation(BinaryTreeStructure<E> dataStructure, E[] values) {
        super(dataStructure);
        this.values = Arrays.asList(values);
    }

    @Override
    protected void buildOperations() {
        // Store UUIDs of inserted nodes for parent lookup
        List<String> nodeUUIDs = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            E value = values.get(i);

            if (value == null) {
                // null is a placeholder, skip it but maintain index correspondence
                nodeUUIDs.add(null);
                continue;
            }

            BinaryTreeInsertUserOperation<E> insertOp;

            if (i == 0) {
                // root node - no parent, no child type needed
                insertOp = new BinaryTreeInsertUserOperation<>(super.dataStructure, null, value, null);
                super.childOperations.add(insertOp);
                nodeUUIDs.add(insertOp.getUUID());
                continue;
            }

            int parentIndex = (i - 1) / 2;
            BinaryTreeNode.ChildType childType = (i % 2 == 1) ? BinaryTreeNode.ChildType.LEFT : BinaryTreeNode.ChildType.RIGHT;

            String parentUUID = nodeUUIDs.get(parentIndex);

            if (parentUUID != null) {
                insertOp = new BinaryTreeInsertUserOperation<>(super.dataStructure, parentUUID, value, childType);
                super.childOperations.add(insertOp);
                nodeUUIDs.add(insertOp.getUUID());
            } else {
                throw new IllegalStateException("Parent UUID is null at index " + i);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Initialize binary tree with given values";
    }
}
