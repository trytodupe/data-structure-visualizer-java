package com.trytodupe.gui.history;

import com.trytodupe.operation.UserOperation;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class OperationHistoryEntry {

    private final UUID id = UUID.randomUUID();
    private final long timestamp = Instant.now().toEpochMilli();
    private final UserOperation<?> operation;
    private OperationHistoryStatus status = OperationHistoryStatus.IDLE;

    public OperationHistoryEntry(UserOperation<?> operation) {
        this.operation = Objects.requireNonNull(operation, "operation");
    }

    public UUID getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public UserOperation<?> getOperation() {
        return operation;
    }

    public OperationHistoryStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return operation.getDescription();
    }

    public void markInProgress() {
        status = OperationHistoryStatus.IN_PROGRESS;
    }

    public void markDone() {
        status = OperationHistoryStatus.DONE;
    }

    public void markUndone() {
        status = OperationHistoryStatus.UNDONE;
    }

    public void markIdle() {
        status = OperationHistoryStatus.IDLE;
    }
}
