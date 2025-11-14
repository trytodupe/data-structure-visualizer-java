package com.trytodupe.datastructure.tree.node;

public class AVLNodeExtension implements INodeExtension {
    private int height = 0;

    public AVLNodeExtension (int height) {
        this.height = height;
    }

    public int getHeight () {
        return height;
    }

    public void setHeight (int height) {
        this.height = height;
    }
}
