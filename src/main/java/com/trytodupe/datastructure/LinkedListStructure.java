package com.trytodupe.datastructure;

import com.trytodupe.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LinkedListStructure extends DataStructure {

    public static class Node {
        private final String uuid;
        private int value;
        private Node next;

        public Node(int value) {
            this.uuid = UUID.randomUUID().toString();
            this.value = value;
        }

        private Node(String uuid, int value) {
            this.uuid = uuid;
            this.value = value;
        }

        public String getUuid() {
            return uuid;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public Node getNext() {
            return next;
        }
    }

    private Node head;
    private int size;
    private final Map<String, Node> index = new HashMap<>();

    public int size() {
        return size;
    }

    public Node getHead() {
        return head;
    }

    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList<>();
        Node current = head;
        while (current != null) {
            nodes.add(current);
            current = current.next;
        }
        return nodes;
    }

    public Node insertAt(int position, int value) {
        Node node = new Node(value);
        return insertExistingAt(position, node);
    }

    public Node insertExistingAt(int position, Node node) {
        if (position < 0 || position > size) {
            throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + size);
        }
        if (position == 0) {
            node.next = head;
            head = node;
        } else {
            Node prev = getNode(position - 1);
            node.next = prev.next;
            prev.next = node;
        }
        index.put(node.uuid, node);
        size++;
        if (Main.DEBUG) {
            printValue();
        }
        return node;
    }

    public Node removeAt(int position) {
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + size);
        }
        Node removed;
        if (position == 0) {
            removed = head;
            head = head.next;
        } else {
            Node prev = getNode(position - 1);
            removed = prev.next;
            prev.next = removed.next;
        }
        removed.next = null;
        index.remove(removed.uuid);
        size--;
        if (Main.DEBUG) {
            printValue();
        }
        return removed;
    }

    public Node removeByUUID(String uuid) {
        Node target = index.get(uuid);
        if (target == null) {
            return null;
        }
        if (head == target) {
            head = head.next;
        } else {
            Node prev = head;
            while (prev != null && prev.next != target) {
                prev = prev.next;
            }
            if (prev != null) {
                prev.next = target.next;
            }
        }
        target.next = null;
        index.remove(uuid);
        size--;
        if (size < 0) {
            size = 0;
        }
        if (Main.DEBUG) {
            printValue();
        }
        return target;
    }

    private Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    @Override
    public void printValue() {
        List<Integer> values = new ArrayList<>();
        Node current = head;
        while (current != null) {
            values.add(current.value);
            current = current.next;
        }
        System.out.println("linked list = " + values);
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
        index.clear();
    }
}
