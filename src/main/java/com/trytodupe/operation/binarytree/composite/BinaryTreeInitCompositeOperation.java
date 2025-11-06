package com.trytodupe.operation.binarytree.composite;

import com.trytodupe.datastructure.BinaryTreeNode;
import com.trytodupe.datastructure.BinaryTreeStructure;
import com.trytodupe.utils.ValueUUIDPair;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.binarytree.user.BinaryTreeInsertUserOperation;

import java.util.ArrayList;
import java.util.List;

public class BinaryTreeInitCompositeOperation extends CompositeUserOperation<BinaryTreeStructure<Integer>> {

    // List of <value, uuid> pairs where value is null for placeholder
    private final List<ValueUUIDPair> values;

    public BinaryTreeInitCompositeOperation (BinaryTreeStructure<Integer> dataStructure, Integer[] values) {
        super(dataStructure);

        List<ValueUUIDPair> valuesList = new ArrayList<>();
        for (Integer value : values) {
            valuesList.add(new ValueUUIDPair(value, null));
        }
        this.values = valuesList;
    }


    @Override
    protected void buildOperations () {
        for (int i = 0; i < values.size(); i++) {
            ValueUUIDPair pair = values.get(i);
            Integer value = pair.getValue();

            if (value == null) {
                // null is a placeholder, skip it but maintain index correspondence
                continue;
            }

            BinaryTreeInsertUserOperation insertOp;

            if (i == 0) {
                // root node
                insertOp = new BinaryTreeInsertUserOperation(super.dataStructure, null, value, null);
                super.childOperations.add(insertOp);
                // Update the UUID in the ValueUUIDPair
                values.set(i, new ValueUUIDPair(value, insertOp.getUUID()));
                continue;
            }

            // Calculate parent index and child type for non-root nodes
            // In a complete binary tree, for node at index i:
            // parent index = (i - 1) / 2
            // if i is odd, it's a left child; if i is even, it's a right child
            int parentIndex = (i - 1) / 2;
            BinaryTreeNode.ChildType childType = (i % 2 == 1) ? BinaryTreeNode.ChildType.LEFT : BinaryTreeNode.ChildType.RIGHT;

            // Get parent UUID directly from the pair
            String parentUUID = values.get(parentIndex).getUUID();

            if (parentUUID != null) {
                insertOp = new BinaryTreeInsertUserOperation(super.dataStructure, parentUUID, value, childType);
                super.childOperations.add(insertOp);
                // Update the UUID in the ValueUUIDPair
                values.set(i, new ValueUUIDPair(value, insertOp.getUUID()));
            }
        }
    }

    @Override
    public String getDescription() {
        return "Initialize binary tree with given values";
    }

}
