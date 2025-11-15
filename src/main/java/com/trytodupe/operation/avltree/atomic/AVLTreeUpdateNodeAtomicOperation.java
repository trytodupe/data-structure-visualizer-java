package com.trytodupe.operation.avltree.atomic;

import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.datastructure.tree.node.AVLNodeExtension;
import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class AVLTreeUpdateNodeAtomicOperation<E extends Comparable<E>> extends AtomicOperation<AVLTreeStructure<E>> {
    
    private final String uuid; // highlight
    private int oldHeight;
    private int newHeight;

    public AVLTreeUpdateNodeAtomicOperation (String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void execute (AVLTreeStructure<E> avlTreeStructure) {
        BinaryTreeNode<E> node = avlTreeStructure.getNode(UUID.fromString(uuid));
        AVLNodeExtension ext = (AVLNodeExtension) node.getExtension();
        oldHeight = ext.getHeight();
        newHeight = avlTreeStructure.computeHeight(node);
        ext.setHeight(newHeight);
    }

    @Override
    public void undo (AVLTreeStructure<E> avlTreeStructure) {
        BinaryTreeNode<E> node = avlTreeStructure.getNode(UUID.fromString(uuid));
        AVLNodeExtension ext = (AVLNodeExtension) node.getExtension();
        ext.setHeight(oldHeight);
    }

    @Override
    public String getDescription () {
        return "Update height for node " + uuid;
    }
}
