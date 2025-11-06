package com.trytodupe.datastructure;

public class BinarySearchTreeStructure<E> extends BinaryTreeStructure<E> {

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
}
