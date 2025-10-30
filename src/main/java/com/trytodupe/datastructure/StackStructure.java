package com.trytodupe.datastructure;

import com.trytodupe.Main;

import java.util.Arrays;

public class StackStructure extends DataStructure {

    private final int[] data;

    private static final int MAX_SIZE = 100;

    private int top;

    public StackStructure() {
        this.data = new int[MAX_SIZE];
        this.top = -1;
    }

    public int peak() {
        if (top == -1) {
            throw new IndexOutOfBoundsException("Stack is empty");
        }
        return data[top];
    }

    public void push(int value) {
        if (top == MAX_SIZE - 1) {
            throw new IndexOutOfBoundsException("Stack is full");
        }
        data[++top] = value;

        if (Main.DEBUG)
            printValue();
    }

    public int pop() {
        if (top == -1) {
            throw new IndexOutOfBoundsException("Stack is empty");
        }
        return data[top--];
    }

    public void draw() {

    }

    public void printValue() {
        System.out.println("data = " + Arrays.toString(Arrays.copyOf(data, top + 1)));
    }

}
