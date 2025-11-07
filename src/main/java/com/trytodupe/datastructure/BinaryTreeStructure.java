package com.trytodupe.datastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BinaryTreeStructure<E> extends DataStructure {

    protected final Map<UUID, BinaryTreeNode<E>> nodes = new HashMap<>();
    protected BinaryTreeNode<E> root;
    protected BinaryTreeNode<E> tempNode;

    public BinaryTreeStructure () {
    }

    public BinaryTreeNode<E> getRoot () {
        return root;
    }

    public void setRoot (BinaryTreeNode<E> root) {
        this.root = root;
    }

    public BinaryTreeNode<E> getTempNode () {
        return tempNode;
    }

    // state of temp node: null -> not null
    public void pushTempNode (BinaryTreeNode<E> node) {
        if (this.tempNode != null)
            throw new IllegalStateException("Temp node is already set.");

        this.tempNode = node;
    }

    // state of temp node: not null -> null
     public BinaryTreeNode<E> popTempNode () {
        if (this.tempNode == null)
            throw new IllegalStateException("Temp node is not set.");

        BinaryTreeNode<E> node = this.tempNode;
        this.tempNode = null;
        return node;
    }

    public BinaryTreeNode<E> getNode (UUID uuid) {
        BinaryTreeNode<E> result = nodes.get(uuid);
//        if (result == null)
//            throw new IllegalArgumentException("Node not found: " + uuid );

        return result;
    }

    public BinaryTreeNode<E> addNode (UUID uuid) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        BinaryTreeNode<E> node = new BinaryTreeNode<>(uuid);
        nodes.put(uuid, node);
        return node;
    }

    public BinaryTreeNode<E> addNode (UUID uuid, E value) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        BinaryTreeNode<E> node = new BinaryTreeNode<>(uuid, value);
        nodes.put(uuid, node);
        return node;
    }


    @Override
    public void draw () {

    }

    @Override
    public void printValue () {
        BinaryTreeNode.traversePreOrder(root);
        System.out.println();
    }
}
