package com.trytodupe.operation.huffmantree.user;

import com.trytodupe.datastructure.tree.HuffmanNode;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeMergeAtomicOperation;

import java.util.UUID;

public class HuffmanTreeMergeUserOperation<E> extends UserOperation<HuffmanTreeStructure<E>> {

    private final String mergedUUID;
    private String leftUUID;
    private String rightUUID;
    private int mergedWeight;

    public HuffmanTreeMergeUserOperation (HuffmanTreeStructure<E> huffmanTreeStructure) {
        super(huffmanTreeStructure);
        this.mergedUUID = UUID.randomUUID().toString();
    }

    @Override
    protected void buildOperations () {
        UUID[] tempNodeUUIDs = super.dataStructure.getSmallestTwoRoots();
        leftUUID = tempNodeUUIDs[0].toString();
        rightUUID = tempNodeUUIDs[1].toString();
        HuffmanNode<E> leftNode = super.dataStructure.getNode(tempNodeUUIDs[0]);
        HuffmanNode<E> rightNode = super.dataStructure.getNode(tempNodeUUIDs[1]);
        mergedWeight = leftNode.getWeight() + rightNode.getWeight();

        super.atomicOperations.add(new HuffmanTreeMergeAtomicOperation<>(mergedUUID, leftUUID, rightUUID, mergedWeight));

    }


}
