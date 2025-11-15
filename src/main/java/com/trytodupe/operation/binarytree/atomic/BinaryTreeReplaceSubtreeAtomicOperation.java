package com.trytodupe.operation.binarytree.atomic;

import com.trytodupe.datastructure.tree.node.BinaryTreeNode;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

import java.util.UUID;

public class BinaryTreeReplaceSubtreeAtomicOperation<E> extends AtomicOperation<BinaryTreeStructure<E>> {

    // parent1 -> subTree1; parent2 -> subTree2
    // to
    // parent1 -> subTree2; parent2 -> null; detached subTree1

    private String parent1UUID;
    private String parent2UUID;
    private final String subTree1UUID; // highlight
    private final String subTree2UUID; // highlight
    private BinaryTreeNode.ChildType childType1;
    private BinaryTreeNode.ChildType childType2;

    public BinaryTreeReplaceSubtreeAtomicOperation (String oldUUID, String newUUID) {
        this.subTree1UUID = oldUUID;
        this.subTree2UUID = newUUID;
    }

    @Override
    public void execute (BinaryTreeStructure<E> binaryTreeStructure) {
        String rootUUID = binaryTreeStructure.getRoot().getUUID().toString();
        if (rootUUID.equals(subTree2UUID)) {
            throw new IllegalArgumentException("Cannot replace a subtree with the root of the tree");
        }

        BinaryTreeNode<E> subTree1 = binaryTreeStructure.getNode(UUID.fromString(subTree1UUID));
        BinaryTreeNode<E> subTree2 = binaryTreeStructure.getNode(UUID.fromString(subTree2UUID));

        if (rootUUID.equals(subTree1UUID)) {
            binaryTreeStructure.addDetatchedRoot(subTree1);
            binaryTreeStructure.setRoot(subTree2);
            return;
        }


        BinaryTreeNode<E> parent1 = binaryTreeStructure.getParent(subTree1.getUUID());
        BinaryTreeNode<E> parent2 = binaryTreeStructure.getParent(subTree2.getUUID());

        parent1UUID = parent1.getUUID().toString();
        parent2UUID = parent2.getUUID().toString();
        childType1 = parent1.isChild(subTree1);
        childType2 = parent2.isChild(subTree2);

        parent1.setChildFromType(childType1, subTree2);
        parent2.setChildFromType(childType2, null);

        // we store them instead of deleting to allow undo
        // we also preserve the connections of old subtree entirely
        binaryTreeStructure.addDetatchedRoot(subTree1);
    }

    @Override
    public void undo (BinaryTreeStructure<E> binaryTreeStructure) {
        String rootUUID = binaryTreeStructure.getRoot().getUUID().toString();
        BinaryTreeNode<E> subTree1 = binaryTreeStructure.getNode(UUID.fromString(subTree1UUID));
        BinaryTreeNode<E> subTree2 = binaryTreeStructure.getNode(UUID.fromString(subTree2UUID));

        if (rootUUID.equals(subTree2UUID)) {
            binaryTreeStructure.setRoot(subTree1);
            binaryTreeStructure.removeDetatchedRoot(subTree1);
            return;
        }

        BinaryTreeNode<E> parent1 = binaryTreeStructure.getParent(subTree1.getUUID());
        BinaryTreeNode<E> parent2 = binaryTreeStructure.getParent(subTree2.getUUID());

        parent2.setChildFromType(childType2, subTree2);
        parent1.setChildFromType(childType1, subTree1);
        binaryTreeStructure.removeDetatchedRoot(subTree1);
    }

    @Override
    public String getDescription () {
        return "Replace subtree " + subTree1UUID + " with subtree " + subTree2UUID;
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
