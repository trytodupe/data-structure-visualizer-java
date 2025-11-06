package com.trytodupe.operation.binarytree.user;

import com.trytodupe.datastructure.BinaryTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeAddNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeConnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;

import java.util.Arrays;

public class BinaryTreeInitUserOperation extends UserOperation<BinaryTreeStructure<java.lang.Integer>> {

    // -1 serve as null indicator
    private final java.lang.Integer[] values;

    public BinaryTreeInitUserOperation (BinaryTreeStructure<java.lang.Integer> binaryTreeStructure, java.lang.Integer[] values) {
        super(binaryTreeStructure);

        if (values == null)
            throw new IllegalArgumentException("Initial values array cannot be null.");

        this.values = values;
        this.description = "Initialize binary tree to: " + Arrays.toString(values);
    }

    @Override
    protected void buildAtomicOperations () {
        // We will create nodes in breadth-first order. For each non-null value:
        // 1) add node (pushes to temp)
        // 2) connect node to parent (pops temp)
        // 3) update node value
        // This ensures at most one temp (orphan) exists at any time.

        BinaryTreeAddNodeAtomicOperation<Integer>[] addOps = new BinaryTreeAddNodeAtomicOperation[values.length];

        for (int i = 0; i < values.length; i++) {
            java.lang.Integer v = values[i];

            // Treat -1 as null indicator
            if (v != null && v != -1) {
                // 1) create the node (temp)
                BinaryTreeAddNodeAtomicOperation<java.lang.Integer> addOp = new BinaryTreeAddNodeAtomicOperation<>();
                addOps[i] = addOp;
                super.atomicOperations.add(addOp);

                // 2) connect to parent (parent index = (i-1)/2). parentUUID == null for root (i == 0)
                String parentUUID = null;
                if (i != 0) {
                    int parentIdx = (i - 1) / 2;
                    if (parentIdx >= 0 && addOps[parentIdx] != null) {
                        parentUUID = addOps[parentIdx].getUUID();
                    } else {
                        // Parent does not exist in the provided array (child without parent) - skip connecting.
                        // This can leave a node in temp if not connected; but since we add and then skip connect,
                        // the next iteration must not add another node (to keep at most one temp). However in a
                        // well-formed breadth-first array the parent will always appear before the child.
                    }
                }

                // Only add connect op if parentUUID is known (or it's root where parentUUID == null)
                if (i == 0 || parentUUID != null) {
                    BinaryTreeConnectNodeAtomicOperation<Integer> connectOp = getIntegerBinaryTreeConnectNodeAtomicOperation(parentUUID, i);

                    super.atomicOperations.add(connectOp);

                    // 3) update value of the newly created node
                    String childUUID = addOp.getUUID();
                    BinaryTreeUpdateValueAtomicOperation<Integer> updateOp =
                            new BinaryTreeUpdateValueAtomicOperation<>(childUUID, v);
                    super.atomicOperations.add(updateOp);
                }
            }
        }
    }

    private static BinaryTreeConnectNodeAtomicOperation<Integer> getIntegerBinaryTreeConnectNodeAtomicOperation (String parentUUID, int i) {
        BinaryTreeConnectNodeAtomicOperation<Integer> connectOp;

        // We need to determine whether this node is left or right child for non-root nodes
        if (i != 0) {
            int parentIdx = (i - 1) / 2;
            // left child if i == 2*parentIdx + 1
            if (i == 2 * parentIdx + 1) {
                connectOp = new BinaryTreeConnectNodeAtomicOperation<>(parentUUID, com.trytodupe.datastructure.BinaryTreeNode.ChildType.LEFT);
            } else {
                connectOp = new BinaryTreeConnectNodeAtomicOperation<>(parentUUID, com.trytodupe.datastructure.BinaryTreeNode.ChildType.RIGHT);
            }
        } else {
            // root: parentUUID == null, childType value doesn't matter
            connectOp = new BinaryTreeConnectNodeAtomicOperation<>(null, com.trytodupe.datastructure.BinaryTreeNode.ChildType.LEFT);
        }
        return connectOp;
    }

}
