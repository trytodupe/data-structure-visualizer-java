package com.trytodupe.datastructure.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BinarySearchTreeStructure<E> extends BinaryTreeStructure<SimpleBinarySearchNode<E>, E> {
    
    public BinarySearchTreeStructure(Class<SimpleBinarySearchNode<E>> nodeClass) {
        super(nodeClass);
    }

    public SimpleBinarySearchNode<E> getInsertParent(E value) {
        if (root == null) {
            return null;
        }

        SimpleBinarySearchNode<E> current = root;
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

    public List<UUID> findSearchPath(SimpleBinarySearchNode<E> target) {
        List<UUID> path = new ArrayList<>();
        SimpleBinarySearchNode<E> current = root;
        while (current != null) {
            path.add(current.getUUID());
            if (target == current.getValue()) break;

            @SuppressWarnings("unchecked")
            int comparison = ((Comparable<E>) target.getValue()).compareTo(current.getValue());

            current = comparison < 0 ? current.getLeft() : current.getRight();
        }
        return path;
    }


    public SimpleBinarySearchNode<E> getSuccessor(SimpleBinarySearchNode<E> node) {
        if (node == null) {
            return null;
        }

        if (node.getRight() != null) {
            SimpleBinarySearchNode<E> current = node.getRight();
            while (current.getLeft() != null) {
                current = current.getLeft();
            }
            return current;
        }

        SimpleBinarySearchNode<E> successor = null;
        SimpleBinarySearchNode<E> current = root;

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
