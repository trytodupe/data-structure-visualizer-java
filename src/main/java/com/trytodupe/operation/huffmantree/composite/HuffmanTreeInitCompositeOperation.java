package com.trytodupe.operation.huffmantree.composite;

import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.operation.CompositeUserOperation;
import com.trytodupe.operation.huffmantree.user.HuffmanTreeInitUserOperation;
import com.trytodupe.operation.huffmantree.user.HuffmanTreeMergeUserOperation;
import com.trytodupe.utils.CharFreqencyPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuffmanTreeInitCompositeOperation extends CompositeUserOperation<HuffmanTreeStructure<Character>> {

    private final String string;

    public HuffmanTreeInitCompositeOperation (HuffmanTreeStructure<Character> dataStructure, String string) {
        super(dataStructure);
        this.string = string.replaceAll(" ", "");
    }

    @Override
    protected void buildOperations () {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : string.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        List<CharFreqencyPair> result = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            result.add(new CharFreqencyPair(entry.getKey(), entry.getValue()));
        }

        super.childOperations.add(new HuffmanTreeInitUserOperation(super.dataStructure, result));

        // n leaf requires n-1 merges
        for (int i = 1; i < result.size(); i++) {
            super.childOperations.add(new HuffmanTreeMergeUserOperation<>(super.dataStructure));
        }

    }
}
