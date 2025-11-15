package com.trytodupe.datastructure.tree;

import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BinaryTreeStructure<E> extends DataStructure {
    protected final Map<UUID, BinaryTreeNode<E>> nodes = new HashMap<>();
    protected BinaryTreeNode<E> root;
    protected BinaryTreeNode<E> tempNode;
    protected List<BinaryTreeNode<E>> detactchedRoots = new ArrayList<>();

    public BinaryTreeStructure() {
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
        return nodes.get(uuid);
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

    public void removeNode (UUID uuid) {
        nodes.remove(uuid);
    }

    public BinaryTreeNode<E> getParent (UUID childUUID) {
        if (childUUID == null) throw new IllegalArgumentException("childUUID must not be null");

        // If there's no root or the child is the root, there is no parent.
        if (root == null) return null;
        if (root.getUUID().equals(childUUID)) return null;

        // Iterative traversal from root using a stack (pre-order style).
        ArrayDeque<BinaryTreeNode<E>> stack = new java.util.ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            BinaryTreeNode<E> node = stack.pop();
            BinaryTreeNode<E> left = node.getLeft();
            BinaryTreeNode<E> right = node.getRight();

            if (left != null && left.getUUID().equals(childUUID)) return node;
            if (right != null && right.getUUID().equals(childUUID)) return node;

            // Push right first so left is processed next (pre-order)
            if (right != null) stack.push(right);
            if (left != null) stack.push(left);
        }

        return null;
    }

    public void addDetatchedRoot (BinaryTreeNode<E> node) {
        detactchedRoots.add(node);
    }

    public void removeDetatchedRoot (BinaryTreeNode<E> node) {
        detactchedRoots.remove(node);
    }

    public List<BinaryTreeNode<E>> getDetatchedRoots () {
        return detactchedRoots;
    }

    @Override
    public void printValue () {
        BinaryTreeNode.printTree(root);
        System.out.println();
    }
}
