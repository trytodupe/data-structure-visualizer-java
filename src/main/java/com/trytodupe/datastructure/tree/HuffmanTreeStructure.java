package com.trytodupe.datastructure.tree;

import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.datastructure.tree.node.HuffmanNodeExtension;
import com.trytodupe.utils.MyArrayList;

import java.util.List;
import java.util.UUID;

// todo: generic node type
public class HuffmanTreeStructure<E> extends BinaryTreeStructure<E>{
    protected List<BinaryTreeNode<E>> roots = new MyArrayList<>();

    public HuffmanTreeStructure () {
    }

    public void addRoot (UUID uuid, E value, int weight) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        BinaryTreeNode<E> node = new BinaryTreeNode<>(uuid, value);
        node.setExtension(new HuffmanNodeExtension(weight));
        nodes.put(uuid, node);
        roots.add(node);
    }

    public void removeRoot (UUID uuid) {
        BinaryTreeNode<E> node = nodes.get(uuid);
        if (node == null)
            throw new IllegalArgumentException("Node does not exist: " + uuid);

        nodes.remove(uuid);
        roots.remove(node);
    }

    public List<BinaryTreeNode<E>> getRoots () {
        return roots;
    }

    public UUID[] getSmallestTwoRoots () {
        UUID[] result = new UUID[2];
        if (roots.size() < 2)
            throw new IllegalStateException("Not enough roots to get smallest two.");

        BinaryTreeNode<E> first = null;
        BinaryTreeNode<E> second = null;

        for (BinaryTreeNode<E> node : roots) {
            if (first == null || ((HuffmanNodeExtension) node.getExtension()).getWeight() < ((HuffmanNodeExtension) first.getExtension()).getWeight()) {
                second = first;
                first = node;
            } else if (second == null || ((HuffmanNodeExtension) node.getExtension()).getWeight() < ((HuffmanNodeExtension) second.getExtension()).getWeight()) {
                second = node;
            }
        }

        result[0] = first.getUUID();
        result[1] = second.getUUID();
        return result;
    }

    public void mergeNodes (UUID uuid, BinaryTreeNode<E> left, BinaryTreeNode<E> right) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        int mergedWeight = ((HuffmanNodeExtension) left.getExtension()).getWeight() + ((HuffmanNodeExtension) right.getExtension()).getWeight();
        BinaryTreeNode<E> node = new BinaryTreeNode<>(uuid, null);
        node.setExtension(new HuffmanNodeExtension(mergedWeight));
        node.setLeft(left);
        node.setRight(right);
        nodes.put(uuid, node);
        roots.add(node);
        roots.remove(left);
        roots.remove(right);
    }

    public void unmergeNodes (UUID uuid) {
        BinaryTreeNode<E> node = nodes.get(uuid);
        if (node == null)
            throw new IllegalArgumentException("Node does not exist: " + uuid);

        BinaryTreeNode<E> left = node.getLeft();
        BinaryTreeNode<E> right = node.getRight();
        nodes.remove(uuid);
        roots.remove(node);
        roots.add(left);
        roots.add(right);
    }
}
