package com.trytodupe.datastructure;

import com.trytodupe.Main;

import java.util.UUID;

public class BinaryTreeNode<E> {

    private final UUID uuid;
    private E value;
    private BinaryTreeNode<E> left;
    private BinaryTreeNode<E> right;

    public BinaryTreeNode (UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID () {
        return uuid;
    }

    public E getValue () {
        return value;
    }

    public void setValue (E value) {
        this.value = value;
        if (Main.DEBUG) {
            if (Main.getDataStructure(BinaryTreeStructure.class).getNode(uuid) != null) {
                traversePreOrder(Main.getDataStructure(BinaryTreeStructure.class).getRoot());
            } else if (Main.getDataStructure(BinarySearchTreeStructure.class).getNode(uuid) != null) {
                traversePreOrder(Main.getDataStructure(BinarySearchTreeStructure.class).getRoot());
            }
            System.out.println();
        }
    }

    public BinaryTreeNode<E> getLeft () {
        return left;
    }

    public void setLeft (BinaryTreeNode<E> left) {
        if (this.left != null)
            throw new IllegalStateException("Left child is already set.");

        this.left = left;
    }

    public void clearLeft () {
        if (this.left == null)
            throw new IllegalStateException("Left child is not set.");

        if (!this.left.isLeaf())
            throw new IllegalStateException("Left child is not a leaf node.");

        this.left = null;
    }

    public BinaryTreeNode<E> getRight () {
        return right;
    }

    public void setRight (BinaryTreeNode<E> right) {
        if (this.right != null)
            throw new IllegalStateException("Right child is already set.");

        this.right = right;
    }

    public void clearRight () {
        if (this.right == null)
            throw new IllegalStateException("Right child is not set.");

        if (!this.right.isLeaf())
            throw new IllegalStateException("Right child is not a leaf node.");

        this.right = null;
    }

    public boolean isLeaf () {
        return left == null && right == null;
    }


    public ChildType isChild (BinaryTreeNode<E> child) {
        if (this.getLeft() == child)
            return ChildType.LEFT;
        else if (this.getRight() == child)
            return ChildType.RIGHT;
        else
            throw new IllegalArgumentException("The specified node is not a child of the parent node.");
    }

    public enum ChildType {
        LEFT,
        RIGHT
    }

    public static void traversePreOrder (BinaryTreeNode<?> node) {
        if (node == null) return;
        System.out.print(node.getValue() + " ");
        traversePreOrder(node.getLeft());
        traversePreOrder(node.getRight());
    }

}
