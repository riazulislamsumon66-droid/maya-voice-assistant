package com.myra.assistant.automation;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002!\"B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\tJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\f\u001a\u00020\rJ&\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0014\u001a\u00020\u00152\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00130\u0017H\u0002J\u001c\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u0005J\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\f\u001a\u00020\rJ\u0012\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u001fJ\u0006\u0010 \u001a\u00020\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lcom/myra/assistant/automation/ScreenMonitor;", "", "<init>", "()V", "TAG", "", "lastUiTree", "lastPackage", "monitoringScope", "Lkotlinx/coroutines/CoroutineScope;", "startMonitoring", "", "service", "Landroid/accessibilityservice/AccessibilityService;", "scope", "captureScreenState", "Lcom/myra/assistant/automation/ScreenMonitor$ScreenState;", "extractElements", "", "Lcom/myra/assistant/automation/ScreenMonitor$UiElement;", "node", "Landroid/view/accessibility/AccessibilityNodeInfo;", "elements", "", "findRelevantElements", "command", "getClickableElements", "onAccessibilityEvent", "event", "Landroid/view/accessibility/AccessibilityEvent;", "getLastScreenState", "Lkotlin/Pair;", "stopMonitoring", "ScreenState", "UiElement", "app_debug"})
public final class ScreenMonitor {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_SCREEN_MONITOR";
    @org.jetbrains.annotations.NotNull()
    private static java.lang.String lastUiTree = "";
    @org.jetbrains.annotations.NotNull()
    private static java.lang.String lastPackage = "";
    @org.jetbrains.annotations.Nullable()
    private static kotlinx.coroutines.CoroutineScope monitoringScope;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.automation.ScreenMonitor INSTANCE = null;
    
    private ScreenMonitor() {
        super();
    }
    
    /**
     * Start continuous screen monitoring
     */
    public final void startMonitoring(@org.jetbrains.annotations.NotNull()
    android.accessibilityservice.AccessibilityService service, @org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineScope scope) {
    }
    
    /**
     * Capture current screen state
     */
    @org.jetbrains.annotations.Nullable()
    public final com.myra.assistant.automation.ScreenMonitor.ScreenState captureScreenState(@org.jetbrains.annotations.NotNull()
    android.accessibilityservice.AccessibilityService service) {
        return null;
    }
    
    /**
     * Extract all clickable and text elements from the screen
     */
    private final java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> extractElements(android.view.accessibility.AccessibilityNodeInfo node, java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> elements) {
        return null;
    }
    
    /**
     * Find interactive elements related to a command
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> findRelevantElements(@org.jetbrains.annotations.NotNull()
    android.accessibilityservice.AccessibilityService service, @org.jetbrains.annotations.NotNull()
    java.lang.String command) {
        return null;
    }
    
    /**
     * Get all clickable elements on screen
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> getClickableElements(@org.jetbrains.annotations.NotNull()
    android.accessibilityservice.AccessibilityService service) {
        return null;
    }
    
    /**
     * Update UI tree after event (called from accessibility service)
     */
    public final void onAccessibilityEvent(@org.jetbrains.annotations.NotNull()
    android.view.accessibility.AccessibilityEvent event, @org.jetbrains.annotations.NotNull()
    android.accessibilityservice.AccessibilityService service) {
    }
    
    /**
     * Get last captured screen state
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<java.lang.String, java.lang.String> getLastScreenState() {
        return null;
    }
    
    public final void stopMonitoring() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0006H\u00c6\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0003J7\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0001J\u0014\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010\u001b\u001a\u00020\u001cH\u00d6\u0081\u0004J\n\u0010\u001d\u001a\u00020\u0003H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001e"}, d2 = {"Lcom/myra/assistant/automation/ScreenMonitor$ScreenState;", "", "uiTree", "", "packageName", "timestamp", "", "elements", "", "Lcom/myra/assistant/automation/ScreenMonitor$UiElement;", "<init>", "(Ljava/lang/String;Ljava/lang/String;JLjava/util/List;)V", "getUiTree", "()Ljava/lang/String;", "getPackageName", "getTimestamp", "()J", "getElements", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class ScreenState {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String uiTree = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String packageName = null;
        private final long timestamp = 0L;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> elements = null;
        
        public ScreenState(@org.jetbrains.annotations.NotNull()
        java.lang.String uiTree, @org.jetbrains.annotations.NotNull()
        java.lang.String packageName, long timestamp, @org.jetbrains.annotations.NotNull()
        java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> elements) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getUiTree() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPackageName() {
            return null;
        }
        
        public final long getTimestamp() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> getElements() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        public final long component3() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.myra.assistant.automation.ScreenMonitor.ScreenState copy(@org.jetbrains.annotations.NotNull()
        java.lang.String uiTree, @org.jetbrains.annotations.NotNull()
        java.lang.String packageName, long timestamp, @org.jetbrains.annotations.NotNull()
        java.util.List<com.myra.assistant.automation.ScreenMonitor.UiElement> elements) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0014\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0007\u00a2\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0007H\u00c6\u0003JE\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0007H\u00c6\u0001J\u0014\u0010\u0019\u001a\u00020\u00072\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010\u001b\u001a\u00020\u001cH\u00d6\u0081\u0004J\n\u0010\u001d\u001a\u00020\u0003H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0010\u00a8\u0006\u001e"}, d2 = {"Lcom/myra/assistant/automation/ScreenMonitor$UiElement;", "", "text", "", "contentDesc", "className", "isClickable", "", "bounds", "isVisible", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Z)V", "getText", "()Ljava/lang/String;", "getContentDesc", "getClassName", "()Z", "getBounds", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
    public static final class UiElement {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String text = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String contentDesc = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String className = null;
        private final boolean isClickable = false;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String bounds = null;
        private final boolean isVisible = false;
        
        public UiElement(@org.jetbrains.annotations.NotNull()
        java.lang.String text, @org.jetbrains.annotations.NotNull()
        java.lang.String contentDesc, @org.jetbrains.annotations.NotNull()
        java.lang.String className, boolean isClickable, @org.jetbrains.annotations.NotNull()
        java.lang.String bounds, boolean isVisible) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getText() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getContentDesc() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getClassName() {
            return null;
        }
        
        public final boolean isClickable() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getBounds() {
            return null;
        }
        
        public final boolean isVisible() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        public final boolean component4() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component5() {
            return null;
        }
        
        public final boolean component6() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.myra.assistant.automation.ScreenMonitor.UiElement copy(@org.jetbrains.annotations.NotNull()
        java.lang.String text, @org.jetbrains.annotations.NotNull()
        java.lang.String contentDesc, @org.jetbrains.annotations.NotNull()
        java.lang.String className, boolean isClickable, @org.jetbrains.annotations.NotNull()
        java.lang.String bounds, boolean isVisible) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}