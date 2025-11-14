package com.trytodupe.operation.huffmantree.composite;

import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.huffmantree.user.HuffmanTreeInitUserOperation;
import com.trytodupe.operation.huffmantree.user.HuffmanTreeMergeUserOperation;
import com.trytodupe.utils.CharFreqencyPair;

import java.util.*;

/**
 * Initialize Huffman tree by building frequency map and merging nodes.
 */
public class HuffmanTreeInitCompositeOperation<E> extends CompositeUserOperation<HuffmanTreeStructure<E>> {

    private final String string;

    public HuffmanTreeInitCompositeOperation(HuffmanTreeStructure<E> dataStructure, String string) {
        super(dataStructure);
        this.string = string.replaceAll(" ", "");
    }

    @Override
    protected void buildOperations() {
        // Build frequency map from input string
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : string.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // Create frequency pairs
        List<CharFreqencyPair> result = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            result.add(new CharFreqencyPair(entry.getKey(), entry.getValue()));
        }

        // Initialize leaf nodes with frequencies
        super.childOperations.add(new HuffmanTreeInitUserOperation<>(dataStructure, result));

        // Merge nodes: n leaf nodes require n-1 merges to form a complete tree
        for (int i = 1; i < result.size(); i++) {
            super.childOperations.add(new HuffmanTreeMergeUserOperation<>(dataStructure));
        }
    }

    @Override
    public String getDescription() {
        return "Initialize Huffman tree from string: " + string;
    }
}
