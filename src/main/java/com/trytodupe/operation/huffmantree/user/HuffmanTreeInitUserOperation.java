package com.trytodupe.operation.huffmantree.user;

import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.operation.huffmantree.atomic.HuffmanTreeAddAtomicOperation;
import com.trytodupe.utils.CharFreqencyPair;

import java.util.List;

/**
 * Initialize Huffman tree leaf nodes with frequency information.
 * Each character-frequency pair becomes a separate root node.
 */
public class HuffmanTreeInitUserOperation<E> extends UserOperation<HuffmanTreeStructure<E>> {

    private final List<CharFreqencyPair> freqPairs;

    public HuffmanTreeInitUserOperation(HuffmanTreeStructure<E> dataStructure, List<CharFreqencyPair> freqPairs) {
        super(dataStructure);
        this.freqPairs = freqPairs;
        this.description = "Initialize Huffman tree with " + freqPairs.size() + " leaf nodes";
    }

    @Override
    protected void buildOperations() {
        for (CharFreqencyPair pair : freqPairs) {
            @SuppressWarnings("unchecked")
            E value = (E) (Object) pair.getCharacter();
            super.atomicOperations.add(new HuffmanTreeAddAtomicOperation<>(null, value, pair.getFreqency()));
        }
    }
}
