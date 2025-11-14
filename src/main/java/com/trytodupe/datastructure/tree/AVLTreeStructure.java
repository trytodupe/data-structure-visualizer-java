package com.trytodupe.datastructure.tree;

import com.trytodupe.datastructure.tree.node.AVLNodeExtension;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;

import java.util.UUID;

public class AVLTreeStructure<E> extends BinarySearchTreeStructure<E>  {

    public AVLTreeStructure () {
    }

    @Override
    public BinaryTreeNode<E> addNode (UUID uuid) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        BinaryTreeNode<E> node = new BinaryTreeNode<>(uuid);
        node.setExtension(new AVLNodeExtension(0));
        nodes.put(uuid, node);
        return node;
    }

    @Override
    public BinaryTreeNode<E> addNode (UUID uuid, E value) {
        if (nodes.containsKey(uuid))
            throw new IllegalArgumentException("Node already exists: " + uuid);

        BinaryTreeNode<E> node = new BinaryTreeNode<>(uuid, value);
        node.setExtension(new AVLNodeExtension(0));
        nodes.put(uuid, node);
        return node;
    }

    public int getBalance(UUID uuid) {
        BinaryTreeNode<E> n = getNode(uuid);
        return height(n.getLeft()) - height(n.getRight());
    }

    public int computeHeight (BinaryTreeNode<E> node) {
        return Math.max(height(node.getLeft()), height(node.getRight())) + 1;
    }

    public static int height(BinaryTreeNode<?> node) {
        if (node == null) {
            return 0;
        }
        AVLNodeExtension ext = (AVLNodeExtension) node.getExtension();
        return ext == null ? 0 : ext.getHeight();
    }


    /**
     * 沿插入路径往上，找到第一个平衡因子不在 [-1,1] 的节点
     */
    public BinaryTreeNode<E> findFirstUnbalancedNode(BinaryTreeNode<E> node) {
        BinaryTreeNode<E> current = getParent(node.getUUID());
        while (current != null) {
            int balance = getBalance(current.getUUID());
            if (balance > 1 || balance < -1) {
                return current;
            }
            current = getParent(current.getUUID());
        }
        return null;
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
