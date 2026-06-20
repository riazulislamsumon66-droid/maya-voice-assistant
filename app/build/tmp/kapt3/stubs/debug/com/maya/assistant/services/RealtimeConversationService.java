package com.myra.assistant.services;

/**
 * Convenience helper to manage the ForegroundVoiceService lifecycle.
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\u0005\u00a8\u0006\u000f"}, d2 = {"Lcom/myra/assistant/services/RealtimeConversationService;", "", "<init>", "()V", "start", "", "context", "Landroid/content/Context;", "stop", "isRunning", "", "sendMessage", "text", "", "reconnect", "app_debug"})
public final class RealtimeConversationService {
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.services.RealtimeConversationService INSTANCE = null;
    
    private RealtimeConversationService() {
        super();
    }
    
    public final void start(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void stop(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final boolean isRunning() {
        return false;
    }
    
    public final void sendMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    public final void reconnect() {
    }
}