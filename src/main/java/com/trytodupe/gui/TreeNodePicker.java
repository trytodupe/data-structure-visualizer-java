package com.trytodupe.gui;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Handles node pick interactions from the tree visualization.
 */
public class TreeNodePicker {

    private String activeContext;
    private Consumer<String> callback;

    public void begin(String context, Consumer<String> callback) {
        cancel();
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(callback, "callback");
        this.activeContext = context;
        this.callback = callback;
    }

    public void cancel() {
        this.activeContext = null;
        this.callback = null;
    }

    public boolean isPicking() {
        return activeContext != null;
    }

    public boolean isPicking(String context) {
        return Objects.equals(activeContext, context);
    }

    public void complete(String uuid) {
        if (callback != null) {
            callback.accept(uuid);
        }
        cancel();
    }

    public String getActiveContext() {
        return activeContext;
    }
}
