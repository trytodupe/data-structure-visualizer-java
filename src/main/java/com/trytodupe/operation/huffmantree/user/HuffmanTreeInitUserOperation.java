package com.trytodupe.operation.huffmantree.user;

import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeAddAtomicOperation;
import com.trytodupe.utils.CharFreqencyPair;

import java.util.List;

public class HuffmanTreeInitUserOperation extends UserOperation<HuffmanTreeStructure<Character>> {

    private final List<CharFreqencyPair> freqPairs;

    public HuffmanTreeInitUserOperation (HuffmanTreeStructure<Character> dataStructure, List<CharFreqencyPair> freqPairs) {
        super(dataStructure);
        this.freqPairs = freqPairs;
    }

    @Override
    protected void buildOperations () {
        for (CharFreqencyPair pair : freqPairs) {
            super.atomicOperations.add(new HuffmanTreeAddAtomicOperation<>(null, pair.getCharacter(), pair.getFreqency()));
        }
    }

}
