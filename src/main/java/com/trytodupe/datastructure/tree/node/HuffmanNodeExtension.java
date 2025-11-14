package com.trytodupe.datastructure.tree.node;

public class HuffmanNodeExtension implements INodeExtension {
    private int weight;

    public HuffmanNodeExtension (int weight) {
        this.weight = weight;
    }

    public int getWeight () {
        return weight;
    }

    public void setWeight (int weight) {
        this.weight = weight;
    }
}
