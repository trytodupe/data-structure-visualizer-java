package com.trytodupe.operation.huffmantree.user;

import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.datastructure.tree.node.HuffmanNodeExtension;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeMergeAtomicOperation;

import java.util.UUID;

/**
 * Merge the two smallest Huffman tree roots into a new parent node.
 * Used repeatedly during Huffman tree construction.
 */
public class HuffmanTreeMergeUserOperation<E> extends UserOperation<HuffmanTreeStructure<E>> {

    private final String mergedUUID;
    private String leftUUID;
    private String rightUUID;
    private int mergedWeight;

    public HuffmanTreeMergeUserOperation(HuffmanTreeStructure<E> huffmanTreeStructure) {
        super(huffmanTreeStructure);
        this.mergedUUID = UUID.randomUUID().toString();
    }

    @Override
    protected void buildOperations() {
        // Find the two smallest root nodes
        UUID[] tempNodeUUIDs = super.dataStructure.getSmallestTwoRoots();
        leftUUID = tempNodeUUIDs[0].toString();
        rightUUID = tempNodeUUIDs[1].toString();

        // Calculate merged weight
        BinaryTreeNode<E> leftNode = super.dataStructure.getNode(tempNodeUUIDs[0]);
        BinaryTreeNode<E> rightNode = super.dataStructure.getNode(tempNodeUUIDs[1]);

        HuffmanNodeExtension leftExt = (HuffmanNodeExtension) leftNode.getExtension();
        HuffmanNodeExtension rightExt = (HuffmanNodeExtension) rightNode.getExtension();
        mergedWeight = leftExt.getWeight() + rightExt.getWeight();

        // Create merge operation
        super.atomicOperations.add(new HuffmanTreeMergeAtomicOperation<>(mergedUUID, leftUUID, rightUUID, mergedWeight));
        this.description = "Merge nodes with weights " + leftExt.getWeight() + " and " + rightExt.getWeight();
    }
}
