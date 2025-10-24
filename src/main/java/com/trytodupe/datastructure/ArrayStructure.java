package com.trytodupe.datastructure;

public class ArrayStructure extends DataStructure {

    private int[] data;
    private static final int MAX_SIZE = 100;
    private int currentSize;

    public ArrayStructure () {
        this.data = new int[MAX_SIZE];
        this.currentSize = 0;
    }

    public int getData(int i) {
        if (i < 0 || i >= currentSize) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + currentSize);
        }
        return data[i];
    }

    public void setData(int i, int value) {
        if (i < 0 || i >= currentSize) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + currentSize);
        }
        data[i] = value;
    }

    public int getSize() {
        return currentSize;
    }

    public void resize(int size) {
        if (size < 0 || size > MAX_SIZE) {
            throw new IllegalArgumentException("Size must be between 0 and " + MAX_SIZE);
        }
        currentSize = size;
    }

    @Override
    public void draw () {

    }
}
