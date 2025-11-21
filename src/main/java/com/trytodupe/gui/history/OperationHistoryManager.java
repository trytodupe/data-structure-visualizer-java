package com.trytodupe.gui.history;

import com.trytodupe.operation.UserOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OperationHistoryManager {

    private final List<OperationHistoryEntry> entries = new ArrayList<>();

    public OperationHistoryEntry add(UserOperation<?> operation) {
        OperationHistoryEntry entry = new OperationHistoryEntry(operation);
        entry.markInProgress();
        entries.add(0, entry);
        return entry;
    }

    public void markDone(OperationHistoryEntry entry) {
        if (entry != null) {
            entry.markDone();
        }
    }

    public void markIdle(OperationHistoryEntry entry) {
        if (entry != null) {
            entry.markIdle();
        }
    }

    public List<OperationHistoryEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public Optional<OperationHistoryEntry> find(UUID id) {
        return entries.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public void undo(OperationHistoryEntry entry) {
        if (entry == null || entry.getStatus() == OperationHistoryStatus.IN_PROGRESS) {
            return;
        }
        entry.getOperation().undo();
        entry.markUndone();
    }

    public void redo(OperationHistoryEntry entry) {
        if (entry == null || entry.getStatus() == OperationHistoryStatus.IN_PROGRESS) {
            return;
        }
        entry.getOperation().execute();
        entry.markDone();
    }
}
