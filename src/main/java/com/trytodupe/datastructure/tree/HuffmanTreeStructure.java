package com.trytodupe.datastructure.tree;

import com.trytodupe.utils.MyArrayList;

import java.util.List;
import java.util.UUID;

// todo: generic node type
public class HuffmanTreeStructure<E> extends BinaryTreeStructure<HuffmanNode<E>, E>{
    protected List<HuffmanNode<E>> roots = new MyArrayList<>();


    public HuffmanTreeStructure (Class<HuffmanNode<E>> nodeClass) {
        super(nodeClass);
    }

    public void addRoot (UUID uuid, E value, int weight) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        HuffmanNode<E> node = new HuffmanNode<>(uuid, value, weight);
        nodes.put(uuid, node);
        roots.add(node);
    }

    public void removeRoot (UUID uuid) {
        HuffmanNode<E> node = nodes.get(uuid);
        if (node == null)
            throw new IllegalArgumentException("Node does not exist: " + uuid);

        nodes.remove(uuid);
        roots.remove(node);
    }

    public List<HuffmanNode<E>> getRoots () {
        return roots;
    }

    public UUID[] getSmallestTwoRoots () {
        UUID[] result = new UUID[2];
        if (roots.size() < 2)
            throw new IllegalStateException("Not enough roots to get smallest two.");

        HuffmanNode<E> first = null;
        HuffmanNode<E> second = null;

        for (HuffmanNode<E> node : roots) {
            if (first == null || node.getWeight() < first.getWeight()) {
                second = first;
                first = node;
            } else if (second == null || node.getWeight() < second.getWeight()) {
                second = node;
            }
        }

        result[0] = first.getUUID();
        result[1] = second.getUUID();
        return result;
    }

    public void mergeNodes (UUID uuid, HuffmanNode<E> left, HuffmanNode<E> right) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        int mergedWeight = left.getWeight() + right.getWeight();
        HuffmanNode<E> node = new HuffmanNode<>(uuid, null, mergedWeight);
        node.setLeft(left);
        node.setRight(right);
        nodes.put(uuid, node);
        roots.add(node);
        roots.remove(left);
        roots.remove(right);
    }

    public void unmergeNodes (UUID uuid) {
        HuffmanNode<E> node = nodes.get(uuid);
        if (node == null)
            throw new IllegalArgumentException("Node does not exist: " + uuid);

        HuffmanNode<E> left = node.getLeft();
        HuffmanNode<E> right = node.getRight();
        nodes.remove(uuid);
        roots.remove(node);
        roots.add(left);
        roots.add(right);
    }
}
