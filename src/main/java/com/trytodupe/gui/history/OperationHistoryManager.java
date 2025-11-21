package com.trytodupe.gui.history;

import com.trytodupe.operation.UserOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OperationHistoryManager {

    private final List<OperationHistoryEntry> entries = new ArrayList<>();
    private int lastDoneIndex = -1;
    private OperationHistoryEntry inProgressEntry = null;

    public OperationHistoryEntry add(UserOperation<?> operation) {
        // drop redo tail
        while (entries.size() - 1 > lastDoneIndex) {
            entries.remove(entries.size() - 1);
        }
        OperationHistoryEntry entry = new OperationHistoryEntry(operation);
        entry.markInProgress();
        entries.add(entry);
        inProgressEntry = entry;
        return entry;
    }

    public void markDone(OperationHistoryEntry entry) {
        if (entry != null) {
            entry.markDone();
            lastDoneIndex = entries.indexOf(entry);
            inProgressEntry = null;
        }
    }

    public void markIdle(OperationHistoryEntry entry) {
        if (entry != null) {
            entry.markIdle();
        }
    }

    public boolean canUndo() {
        return lastDoneIndex >= 0 && entries.get(lastDoneIndex).getStatus() == OperationHistoryStatus.DONE && inProgressEntry == null;
    }

    public boolean canRedo() {
        return inProgressEntry == null && lastDoneIndex + 1 < entries.size()
            && entries.get(lastDoneIndex + 1).getStatus() == OperationHistoryStatus.UNDONE;
    }

    public List<OperationHistoryEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public Optional<OperationHistoryEntry> find(UUID id) {
        return entries.stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public OperationHistoryEntry undoLatest() {
        if (!canUndo()) {
            return null;
        }
        OperationHistoryEntry entry = entries.get(lastDoneIndex);
        entry.getOperation().undo();
        entry.markUndone();
        lastDoneIndex--;
        return entry;
    }

    public OperationHistoryEntry redoNext() {
        if (!canRedo()) {
            return null;
        }
        OperationHistoryEntry entry = entries.get(lastDoneIndex + 1);
        entry.getOperation().execute();
        entry.markDone();
        lastDoneIndex++;
        return entry;
    }

    public OperationHistoryEntry getInProgressEntry() {
        return inProgressEntry;
    }

    public void clearAll() {
        entries.clear();
        lastDoneIndex = -1;
        inProgressEntry = null;
    }
}
