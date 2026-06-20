package com.myra.assistant.service;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bJ\u001e\u0010\r\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bJ.\u0010\u000e\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/myra/assistant/service/GestureExecutor;", "", "<init>", "()V", "TAG", "", "tap", "", "service", "Landroid/accessibilityservice/AccessibilityService;", "x", "", "y", "longTap", "swipe", "startX", "startY", "endX", "endY", "app_debug"})
public final class GestureExecutor {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_GESTURE";
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.service.GestureExecutor INSTANCE = null;
    
    private GestureExecutor() {
        super();
    }
    
    public final boolean tap(@org.jetbrains.annotations.NotNull()
    android.accessibilityservice.AccessibilityService service, int x, int y) {
        return false;
    }
    
    public final boolean longTap(@org.jetbrains.annotations.NotNull()
    android.accessibilityservice.AccessibilityService service, int x, int y) {
        return false;
    }
    
    public final boolean swipe(@org.jetbrains.annotations.NotNull()
    android.accessibilityservice.AccessibilityService service, int startX, int startY, int endX, int endY) {
        return false;
    }
}