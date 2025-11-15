package com.trytodupe.operation.utils;

import com.trytodupe.datastructure.DataStructure;
import com.trytodupe.operation.AtomicOperation;
import com.trytodupe.operation.IOperationVisitor;

import java.util.List;

public class VisualizePathAtomicOperation extends AtomicOperation<DataStructure> {

    private final List<String> nodeUUIDs;

    public VisualizePathAtomicOperation(List<String> nodeUUIDs) {
        this.nodeUUIDs = nodeUUIDs;
    }

    public List<String> getNodeUUIDs() {
        return nodeUUIDs;
    }

    @Override
    public void execute(DataStructure dataStructure) {
        // This operation does not change the state of the data structure.
        // Its purpose is purely for visualization.
    }

    @Override
    public void undo(DataStructure dataStructure) {
        // This operation does not change the state of the data structure.
    }

    @Override
    public String getDescription() {
        return "Visualizing a path of " + nodeUUIDs.size() + " nodes.";
    }

    @Override
    public void accept(IOperationVisitor visitor) {
        visitor.visit(this);
    }
}
