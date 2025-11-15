package com.trytodupe.gui;

public class ArrowInfo {
    public enum ArrowType {
        SINGLE_DIRECTIONAL, // 单向箭头
        DOUBLE_DIRECTIONAL  // 双向箭头
    }

    // 箭头的起点和终点。可以是 UUID (节点) 或 Integer (数组索引)
    // 为了通用性，我们使用 String 来存储 ID，渲染时再解析
    public String fromId;
    public String toId;
    public ArrowType type;

    public ArrowInfo(String fromId, String toId, ArrowType type) {
        this.fromId = fromId;
        this.toId = toId;
        this.type = type;
    }
}
