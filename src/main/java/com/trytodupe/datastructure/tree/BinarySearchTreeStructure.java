package com.trytodupe.datastructure.tree;

import com.trytodupe.datastructure.tree.node.BinaryTreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BinarySearchTreeStructure<E> extends BinaryTreeStructure<E> {
    
    public BinarySearchTreeStructure() {
    }

    public BinaryTreeNode<E> getInsertParent(E value) {
        if (root == null) {
            return null;
        }

        BinaryTreeNode<E> current = root;
        while (true) {
            @SuppressWarnings("unchecked")
            int comparison = ((Comparable<E>) value).compareTo(current.getValue());

            if (comparison < 0) {
                // Value should go to the left
                if (current.getLeft() == null) {
                    return current;
                }
                current = current.getLeft();
            } else {
                // Value should go to the right (>= for duplicates)
                if (current.getRight() == null) {
                    return current;
                }
                current = current.getRight();
            }
        }
    }

    public List<UUID> findSearchPath(BinaryTreeNode<E> target) {
        List<UUID> path = new ArrayList<>();
        BinaryTreeNode<E> current = root;
        while (current != null) {
            path.add(current.getUUID());
            if (target == current.getValue()) break;

            @SuppressWarnings("unchecked")
            int comparison = ((Comparable<E>) target.getValue()).compareTo(current.getValue());

            current = comparison < 0 ? current.getLeft() : current.getRight();
        }
        return path;
    }


    public BinaryTreeNode<E> getSuccessor(BinaryTreeNode<E> node) {
        if (node == null) {
            return null;
        }

        if (node.getRight() != null) {
            BinaryTreeNode<E> current = node.getRight();
            while (current.getLeft() != null) {
                current = current.getLeft();
            }
            return current;
        }

        BinaryTreeNode<E> successor = null;
        BinaryTreeNode<E> current = root;

        while (current != null) {
            @SuppressWarnings("unchecked")
            int comparison = ((Comparable<E>) node.getValue()).compareTo(current.getValue());

            if (comparison < 0) {
                successor = current;
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        return successor;
    }

}
