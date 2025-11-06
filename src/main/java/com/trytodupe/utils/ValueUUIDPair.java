package com.trytodupe.utils;

/**
 * A simple pair class to hold a value and its corresponding UUID.
 * Used for binary tree initialization with value-uuid associations.
 */
public class ValueUUIDPair {
    private final Integer value;  // null for placeholder
    private final String uuid;    // UUID of the node, null initially

    public ValueUUIDPair(Integer value, String uuid) {
        this.value = value;
        this.uuid = uuid;
    }

    public Integer getValue() {
        return value;
    }

    public String getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return "ValueUUIDPair{" +
                "value=" + value +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}

