package com.myra.assistant.ai;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u000e\u0010\r\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\bJ\u0006\u0010\u0011\u001a\u00020\nJ\b\u0010\u0012\u001a\u00020\nH\u0002J\u0006\u0010\u0013\u001a\u00020\fR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/myra/assistant/ai/ConversationMemory;", "", "<init>", "()V", "history", "", "Lcom/myra/assistant/ai/Turn;", "MAX_TURNS", "", "addUser", "", "text", "", "addAssistant", "getRecent", "", "n", "clear", "trim", "getContextSummary", "app_debug"})
public final class ConversationMemory {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.myra.assistant.ai.Turn> history = null;
    private static final int MAX_TURNS = 20;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.ai.ConversationMemory INSTANCE = null;
    
    private ConversationMemory() {
        super();
    }
    
    public final void addUser(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    public final void addAssistant(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.myra.assistant.ai.Turn> getRecent(int n) {
        return null;
    }
    
    public final void clear() {
    }
    
    private final void trim() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getContextSummary() {
        return null;
    }
}