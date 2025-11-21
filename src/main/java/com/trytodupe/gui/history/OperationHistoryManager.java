package com.trytodupe.gui.history;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trytodupe.operation.UserOperation;
import com.trytodupe.serialization.GsonProvider;
import com.trytodupe.serialization.ISerializable;

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

    public void setInProgressEntry(OperationHistoryEntry entry) {
        this.inProgressEntry = entry;
    }

    public void clearAll() {
        entries.clear();
        lastDoneIndex = -1;
        inProgressEntry = null;
    }

    public OperationHistoryEntry peekRedoEntry() {
        if (!canRedo()) {
            return null;
        }
        return entries.get(lastDoneIndex + 1);
    }

    public String exportAsJson() {
        JsonArray array = new JsonArray();
        for (OperationHistoryEntry entry : entries) {
            JsonObject item = new JsonObject();
            item.addProperty("description", entry.getDescription());
            item.addProperty("timestamp", entry.getTimestamp());
            item.addProperty("status", entry.getStatus().name());
            item.add("operation", entry.getOperation().toJson(GsonProvider.get()));
            array.add(item);
        }
        return GsonProvider.get().toJson(array);
    }

    public void importFromJson(String json) {
        entries.clear();
        lastDoneIndex = -1;
        inProgressEntry = null;

        if (json == null || json.trim().isEmpty()) {
            return;
        }

        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject item = array.get(i).getAsJsonObject();
            JsonObject opJson = item.getAsJsonObject("operation");
            if (opJson == null) {
                continue;
            }
            ISerializable deserialized = ISerializable.fromJson(GsonProvider.get(), opJson);
            if (deserialized instanceof UserOperation<?> userOp) {
                OperationHistoryEntry entry = new OperationHistoryEntry(userOp);
                entry.markUndone();
                entries.add(entry);
            }
        }
    }
}
