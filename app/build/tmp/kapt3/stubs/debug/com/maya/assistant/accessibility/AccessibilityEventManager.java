package com.myra.assistant.accessibility;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u0006\u0010\f\u001a\u00020\u0005J\u0006\u0010\r\u001a\u00020\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/myra/assistant/accessibility/AccessibilityEventManager;", "", "<init>", "()V", "lastPackage", "", "lastEventType", "", "onEvent", "", "e", "Landroid/view/accessibility/AccessibilityEvent;", "getCurrentPackage", "getLastEventType", "app_debug"})
public final class AccessibilityEventManager {
    @org.jetbrains.annotations.NotNull()
    private static java.lang.String lastPackage = "";
    private static int lastEventType = -1;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.accessibility.AccessibilityEventManager INSTANCE = null;
    
    private AccessibilityEventManager() {
        super();
    }
    
    public final void onEvent(@org.jetbrains.annotations.NotNull()
    android.view.accessibility.AccessibilityEvent e) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCurrentPackage() {
        return null;
    }
    
    public final int getLastEventType() {
        return 0;
    }
}