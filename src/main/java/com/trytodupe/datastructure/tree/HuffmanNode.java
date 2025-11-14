package com.trytodupe.datastructure.tree;

import java.util.UUID;

public class HuffmanNode<E> extends BinaryTreeNode<E> {

    private int weight;
    private HuffmanNode<E> left;
    private HuffmanNode<E> right;

    public HuffmanNode(UUID uuid) {
        super(uuid);
    }

    public HuffmanNode (UUID uuid, E value, int weight) {
        super(uuid, value);
        this.weight = weight;
    }

    public int getWeight () {
        return weight;
    }

    public void setWeight (int weight) {
        this.weight = weight;
    }

    public String toString() {
        return "Value: " + getValue() + " Weight: " + getWeight();
    }
}

