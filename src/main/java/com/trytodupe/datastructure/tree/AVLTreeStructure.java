package com.trytodupe.datastructure.tree;

import java.util.UUID;

public class AVLTreeStructure<E> extends BinaryTreeStructure<AVLTreeNode<E>, E>  {

    public AVLTreeStructure (Class<AVLTreeNode<E>> nodeClass) {
        super(nodeClass);
    }

    public int getBalance(UUID uuid) {
        AVLTreeNode<E> n =  getNode(uuid);
        return AVLTreeNode.height(n.getLeft()) - AVLTreeNode.height(n.getRight());
    }

    /**
     * 沿插入路径往上，找到第一个平衡因子不在 [-1,1] 的节点
     */
    public AVLTreeNode<E> findFirstUnbalancedNode(AVLTreeNode<E> node) {
        AVLTreeNode<E> current = getParent(node.getUUID());
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
