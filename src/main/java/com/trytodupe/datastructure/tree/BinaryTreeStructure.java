package com.trytodupe.datastructure.tree;

import com.trytodupe.datastructure.DataStructure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BinaryTreeStructure<T extends BinaryTreeNode<E, T>, E> extends DataStructure {

    private final Class<T> nodeClass;

    protected final Map<UUID, T> nodes = new HashMap<>();
    protected T root;
    protected T tempNode;
    protected List<T> detactchedRoots = new ArrayList<>();

    public BinaryTreeStructure(Class<T> nodeClass) {
        this.nodeClass = nodeClass;
    }

    public T getRoot () {
        return root;
    }

    public void setRoot (T root) {
        this.root = root;
    }

    public T getTempNode () {
        return tempNode;
    }

    // state of temp node: null -> not null
    public void pushTempNode (T node) {
        if (this.tempNode != null)
            throw new IllegalStateException("Temp node is already set.");

        this.tempNode = node;
    }

    // state of temp node: not null -> null
     public T popTempNode () {
        if (this.tempNode == null)
            throw new IllegalStateException("Temp node is not set.");

        T node = this.tempNode;
        this.tempNode = null;
        return node;
    }

    public T getNode (UUID uuid) {
        return nodes.get(uuid);
    }

    public T createNodeInstance(UUID uuid, E value) {
        try {
            if (value == null) {
                return nodeClass.getConstructor(UUID.class).newInstance(uuid);
            } else {
                return nodeClass.getConstructor(UUID.class, Object.class).newInstance(uuid, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create node instance", e);
        }
    }

    public T addNode (UUID uuid) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        T node = createNodeInstance(uuid, null);
        nodes.put(uuid, node);
        return node;
    }

    public T addNode (UUID uuid, E value) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        T node = createNodeInstance(uuid, value);
        nodes.put(uuid, node);
        return node;
    }

    public void removeNode (UUID uuid) {
        nodes.remove(uuid);
    }

    public T getParent (UUID childUUID) {
        if (childUUID == null) throw new IllegalArgumentException("childUUID must not be null");

        // If there's no root or the child is the root, there is no parent.
        if (root == null) return null;
        if (root.getUUID().equals(childUUID)) return null;

        // Iterative traversal from root using a stack (pre-order style).
        ArrayDeque<T> stack = new java.util.ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            T node = stack.pop();
            T left = node.getLeft();
            T right = node.getRight();

            if (left != null && left.getUUID().equals(childUUID)) return node;
            if (right != null && right.getUUID().equals(childUUID)) return node;

            // Push right first so left is processed next (pre-order)
            if (right != null) stack.push(right);
            if (left != null) stack.push(left);
        }

        return null;
    }

    public void addDetatchedRoot (T node) {
        detactchedRoots.add(node);
    }

    public void removeDetatchedRoot (T node) {
        detactchedRoots.remove(node);
    }

    public List<T> getDetatchedRoots () {
        return detactchedRoots;
    }

    @Override
    public void draw () {

    }

    @Override
    public void printValue () {
        BinaryTreeNode.printTree(root);
        System.out.println();
    }
}
