package com.trytodupe.datastructure.tree.node;

import com.trytodupe.Main;
import com.trytodupe.datastructure.tree.BinarySearchTreeStructure;
import com.trytodupe.datastructure.tree.BinaryTreeStructure;

import java.util.*;
import java.util.UUID;

public class BinaryTreeNode<E> {

    protected final UUID uuid;
    protected E value;
    private BinaryTreeNode<E> left;
    private BinaryTreeNode<E> right;
    private INodeExtension extension;

    public BinaryTreeNode (UUID uuid) {
        this.uuid = uuid;
    }

    public BinaryTreeNode (UUID uuid, E value) {
        this.uuid = uuid;
        this.value = value;
    }

    public INodeExtension getExtension () {
        return extension;
    }

    public void setExtension (INodeExtension extension) {
        this.extension = extension;
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
                printTree(Main.getDataStructure(BinaryTreeStructure.class).getRoot());
            } else if (Main.getDataStructure(BinarySearchTreeStructure.class).getNode(uuid) != null) {
                printTree(Main.getDataStructure(BinarySearchTreeStructure.class).getRoot());
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
        return getChildCount() == 0;
    }

    public int getChildCount () {
        int count = 0;
        if (left != null) count++;
        if (right != null) count++;
        return count;
    }


    public ChildType isChild (BinaryTreeNode<E> child) {
        if (this.getLeft() == child)
            return ChildType.LEFT;
        else if (this.getRight() == child)
            return ChildType.RIGHT;
        else
            throw new IllegalArgumentException("The specified node is not a child of the parent node.");
    }

    public BinaryTreeNode<E> getChildFromType (ChildType childType) {
        switch (childType) {
            case LEFT:
                return this.getLeft();
            case RIGHT:
                return this.getRight();
            default:
                throw new IllegalArgumentException("Invalid child type: " + childType);
        }
    }

    public void setChildFromType (ChildType childType, BinaryTreeNode<E> child) {
        switch (childType) {
            case LEFT:
                this.setLeft(child);
                break;
            case RIGHT:
                this.setRight(child);
                break;
            default:
                throw new IllegalArgumentException("Invalid child type: " + childType);
        }
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

    // New method: printTree
    public static void printTree(BinaryTreeNode<?> node) {
        if (node == null) return;

        // Collect nodes: compute inorder indices and depths, and track max widths
        Map<BinaryTreeNode<?>, Integer> indexMap = new IdentityHashMap<>();
        Map<BinaryTreeNode<?>, Integer> depthMap = new IdentityHashMap<>();
        int[] counter = new int[]{0};
        int[] maxDepth = new int[]{0};
        int[] maxValLen = new int[]{0};

        // compute maxValLen while assigning indices and depths
        assignIndicesAndDepths(node, 0, counter, indexMap, depthMap, maxDepth, maxValLen);

        int maxIndex = counter[0];
        int spacing = Math.max(3, maxValLen[0] + 2); // horizontal spacing
        int rows = maxDepth[0] * 2 + 1;
        int cols = (maxIndex + 1) * spacing + spacing; // extra padding

        // initialize canvas
        char[][] canvas = new char[rows][cols];
        for (int r = 0; r < rows; r++) Arrays.fill(canvas[r], ' ');

        // place nodes
        List<BinaryTreeNode<?>> allNodes = new ArrayList<>(indexMap.keySet());
        // sort by depth ascending then index to make deterministic
        allNodes.sort(Comparator.comparingInt(depthMap::get).thenComparingInt(indexMap::get));

        for (BinaryTreeNode<?> n : allNodes) {
            int idx = indexMap.get(n);
            int depth = depthMap.get(n);
            String s = String.valueOf(n.getValue());
            int colCenter = idx * spacing + spacing / 2;
            int row = depth * 2;
            int start = colCenter - s.length() / 2;
            if (start < 0) start = 0;
            for (int i = 0; i < s.length() && start + i < cols; i++) {
                canvas[row][start + i] = s.charAt(i);
            }

            // draw connector to left child
            BinaryTreeNode<?> left = n.getLeft();
            if (left != null && indexMap.containsKey(left)) {
                int childIdx = indexMap.get(left);
                int childCol = childIdx * spacing + spacing / 2;
                int connRow = row + 1;
                int mid = (childCol + colCenter) / 2;
                if (mid >= 0 && mid < cols) canvas[connRow][mid] = '/';
            }
            // draw connector to right child
            BinaryTreeNode<?> right = n.getRight();
            if (right != null && indexMap.containsKey(right)) {
                int childIdx = indexMap.get(right);
                int childCol = childIdx * spacing + spacing / 2;
                int connRow = row + 1;
                int mid = (childCol + colCenter) / 2;
                if (mid >= 0 && mid < cols) canvas[connRow][mid] = '\\';
            }
        }

        // print canvas trimming trailing spaces per line
        for (int r = 0; r < rows; r++) {
            int lastNonSpace = cols - 1;
            while (lastNonSpace >= 0 && canvas[r][lastNonSpace] == ' ') lastNonSpace--;
            if (lastNonSpace < 0) System.out.println();
            else System.out.println(new String(canvas[r], 0, lastNonSpace + 1));
        }
    }

    // helper to assign inorder index and depth; updates counter, maps, and max values
    private static void assignIndicesAndDepths(BinaryTreeNode<?> node,
                                               int depth,
                                               int[] counter,
                                               Map<BinaryTreeNode<?>, Integer> indexMap,
                                               Map<BinaryTreeNode<?>, Integer> depthMap,
                                               int[] maxDepth,
                                               int[] maxValLen) {
        if (node == null) return;
        assignIndicesAndDepths(node.getLeft(), depth + 1, counter, indexMap, depthMap, maxDepth, maxValLen);
        counter[0]++;
        indexMap.put(node, counter[0]);
        depthMap.put(node, depth);
        maxDepth[0] = Math.max(maxDepth[0], depth);
        String s = String.valueOf(node.getValue());
        maxValLen[0] = Math.max(maxValLen[0], s.length());
        assignIndicesAndDepths(node.getRight(), depth + 1, counter, indexMap, depthMap, maxDepth, maxValLen);
    }


}
