package com.trytodupe.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HighlightInfo {
    public Set<UUID> nodeUUIDs = new HashSet<>();
    public Set<Integer> arrayIndices = new HashSet<>();
    public boolean highlightTempSlot = false;
    public boolean highlightEntireArray = false;
    public List<ArrowInfo> arrows = new ArrayList<>(); // 新增：存储箭头信息
    // 可以根据需要添加其他高亮类型，例如：
    // public Set<String> edgeUUIDs = new HashSet<>();
    // public UUID rootNodeUUID = null;

    public boolean isEmpty() {
        return nodeUUIDs.isEmpty() && arrayIndices.isEmpty() && !highlightTempSlot && !highlightEntireArray && arrows.isEmpty();
    }
}
