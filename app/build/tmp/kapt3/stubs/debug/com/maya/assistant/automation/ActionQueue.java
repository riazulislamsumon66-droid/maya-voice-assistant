package com.myra.assistant.automation;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006J\b\u0010\n\u001a\u0004\u0018\u00010\u0006J\u0006\u0010\u000b\u001a\u00020\bJ\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\u000fR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/myra/assistant/automation/ActionQueue;", "", "<init>", "()V", "queue", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "Lcom/myra/assistant/models/ActionModel;", "enqueue", "", "action", "dequeue", "isEmpty", "clear", "", "size", "", "app_debug"})
public final class ActionQueue {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.concurrent.ConcurrentLinkedQueue<com.myra.assistant.models.ActionModel> queue = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.automation.ActionQueue INSTANCE = null;
    
    private ActionQueue() {
        super();
    }
    
    public final boolean enqueue(@org.jetbrains.annotations.NotNull()
    com.myra.assistant.models.ActionModel action) {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.myra.assistant.models.ActionModel dequeue() {
        return null;
    }
    
    public final boolean isEmpty() {
        return false;
    }
    
    public final void clear() {
    }
    
    public final int size() {
        return 0;
    }
}