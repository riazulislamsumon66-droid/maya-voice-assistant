package com.myra.assistant.services;

/**
 * Facade for the primary assistant service layer.
 * Routes to ForegroundVoiceService + SmartAccessibilityEngine.
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000bJ\u0006\u0010\f\u001a\u00020\r\u00a8\u0006\u000e"}, d2 = {"Lcom/myra/assistant/services/MainAssistantService;", "", "<init>", "()V", "initialize", "", "context", "Landroid/content/Context;", "shutdown", "speak", "text", "", "isActive", "", "app_debug"})
public final class MainAssistantService {
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.services.MainAssistantService INSTANCE = null;
    
    private MainAssistantService() {
        super();
    }
    
    public final void initialize(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void shutdown(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void speak(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    public final boolean isActive() {
        return false;
    }
}