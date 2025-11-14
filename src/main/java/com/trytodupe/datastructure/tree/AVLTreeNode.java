package com.trytodupe.datastructure.tree;

import java.util.UUID;

public class AVLTreeNode<E> extends BinaryTreeNode<E, AVLTreeNode<E>> {

    private int height;

    public AVLTreeNode (UUID uuid) {
        super(uuid);
    }

    public AVLTreeNode (UUID uuid, E value) {
        super(uuid, value);
    }

    public int getHeight () {
        return height;
    }

    public void setHeight (int height) {
        this.height = height;
    }

    public int computeHeight () {
        return Math.max(height(getLeft()), height(getRight())) + 1;
    }

    public static int height(AVLTreeNode<?> n) {
        return n == null ? 0 : n.getHeight();
    }

}
