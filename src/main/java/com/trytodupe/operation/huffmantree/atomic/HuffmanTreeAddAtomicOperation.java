package com.trytodupe.operation.huffmantree.atomic;

import com.trytodupe.datastructure.tree.HuffmanTreeStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

import java.util.UUID;

public class HuffmanTreeAddAtomicOperation<E> extends AtomicOperation<HuffmanTreeStructure<E>> {

    private final String uuid; // highlight
    private final E value;
    private final int weight;

    public HuffmanTreeAddAtomicOperation (String uuid, E value, int weight) {
        if (uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        } else {
            this.uuid = uuid;
        }

        this.value = value;
        this.weight = weight;
    }

    @Override
    public void execute (HuffmanTreeStructure<E> huffmanTreeStructure) {
        huffmanTreeStructure.addRoot(UUID.fromString(uuid), value, weight);
    }

    @Override
    public void undo (HuffmanTreeStructure<E> huffmanTreeStructure) {
        huffmanTreeStructure.removeRoot(UUID.fromString(uuid));
    }

    @Override
    public String getDescription () {
        return "Add a new Huffman node with value " + value + " and weight " + weight;
    }

    @Override
    public void accept (IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
