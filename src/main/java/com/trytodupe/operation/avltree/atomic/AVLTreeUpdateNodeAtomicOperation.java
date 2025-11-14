package com.trytodupe.operation.avltree.atomic;

import com.trytodupe.datastructure.tree.AVLTreeNode;
import com.trytodupe.datastructure.tree.AVLTreeStructure;
import com.trytodupe.operation.AtomicOperation;

import java.util.UUID;

public class AVLTreeUpdateNodeAtomicOperation<E> extends AtomicOperation<AVLTreeStructure<E>> {
    
    private final String uuid;
    private int oldHeight;
    private final int newHeight;

    public AVLTreeUpdateNodeAtomicOperation (String uuid, int newHeight) {
        this.uuid = uuid;
        this.newHeight = newHeight;
    }

    @Override
    public void execute (AVLTreeStructure<E> avlTreeStructure) {
        AVLTreeNode<E> node = avlTreeStructure.getNode(UUID.fromString(uuid));
        oldHeight = node.getHeight();
        node.setHeight(newHeight);
    }

    @Override
    public void undo (AVLTreeStructure<E> avlTreeStructure) {
        AVLTreeNode<E> node = avlTreeStructure.getNode(UUID.fromString(uuid));
        node.setHeight(oldHeight);
    }

    @Override
    public String getDescription () {
        return "Update weight for node " + uuid;
    }
}
