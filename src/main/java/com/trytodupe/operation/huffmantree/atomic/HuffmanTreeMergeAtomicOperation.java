package com.trytodupe.operation.huffmantree.atomic;

import com.trytodupe.datastructure.tree.HuffmanNode;
import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class HuffmanTreeMergeAtomicOperation<E> extends AtomicOperation<HuffmanTreeStructure<E>> {

    private final String mergedUUID;
    private final String leftUUID;
    private final String rightUUID;
    private final int mergedWeight;

    public HuffmanTreeMergeAtomicOperation (String mergedUUID, String leftUUID, String rightUUID, int mergedWeight) {
        this.mergedUUID = mergedUUID;
        this.leftUUID = leftUUID;
        this.rightUUID = rightUUID;
        this.mergedWeight = mergedWeight;
    }

    @Override
    public void execute (HuffmanTreeStructure<E> huffmanTreeStructure) {
        HuffmanNode<E> leftNode = huffmanTreeStructure.getNode(UUID.fromString(leftUUID));
        HuffmanNode<E> rightNode = huffmanTreeStructure.getNode(UUID.fromString(rightUUID));

        huffmanTreeStructure.mergeNodes(UUID.fromString(mergedUUID), leftNode, rightNode);
    }

    @Override
    public void undo (HuffmanTreeStructure<E> huffmanTreeStructure) {
        huffmanTreeStructure.unmergeNodes(UUID.fromString(mergedUUID));
    }

    @Override
    public String getDescription () {
        return "Merge to create a new node with weight " + mergedWeight;
    }

}
