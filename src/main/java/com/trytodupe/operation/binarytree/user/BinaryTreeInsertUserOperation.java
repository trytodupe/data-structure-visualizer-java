package com.trytodupe.operation.binarytree.user;

import com.trytodupe.datastructure.BinaryTreeNode;
import com.trytodupe.datastructure.BinaryTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeAddNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeConnectNodeAtomicOperation;
import com.trytodupe.operation.binarytree.atomic.BinaryTreeUpdateValueAtomicOperation;

import java.util.UUID;

public class BinaryTreeInsertUserOperation extends UserOperation<BinaryTreeStructure<Integer>> {

    private final String parentUUID;
    private final String uuid;
    private final Integer value;
    private final BinaryTreeNode.ChildType childType;

    public BinaryTreeInsertUserOperation(BinaryTreeStructure<Integer> binaryTreeStructure,
                                        String parentUUID,
                                        Integer value,
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

