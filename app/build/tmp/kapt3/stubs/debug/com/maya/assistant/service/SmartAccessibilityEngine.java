package com.myra.assistant.service;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\'B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005J\u0010\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0005H\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0005H\u0002J\u0010\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0005H\u0002J\u0010\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0005H\u0002J\u0012\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u0005H\u0002J\u0018\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0005H\u0002J*\u0010\u001c\u001a\u00020\u00122\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u0005J\u0010\u0010\u001f\u001a\u00020\u00122\u0006\u0010\u0010\u001a\u00020\u0005H\u0002J\u0010\u0010 \u001a\u00020\u00122\u0006\u0010!\u001a\u00020\u0005H\u0002J\u0012\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010$\u001a\u00020#H\u0002J\b\u0010%\u001a\u00020\u0012H\u0002J\b\u0010&\u001a\u00020\u0012H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b\u00a8\u0006("}, d2 = {"Lcom/myra/assistant/service/SmartAccessibilityEngine;", "", "<init>", "()V", "TAG", "", "service", "Landroid/accessibilityservice/AccessibilityService;", "getService", "()Landroid/accessibilityservice/AccessibilityService;", "setService", "(Landroid/accessibilityservice/AccessibilityService;)V", "execute", "Lcom/myra/assistant/service/SmartAccessibilityEngine$Result;", "rawCommand", "cleanCommand", "text", "runSmartAutomation", "", "command", "handleOpenApp", "handlePlayMusic", "findLaunchIntent", "Landroid/content/Intent;", "appName", "launchPackage", "packageName", "tag", "click", "contentDesc", "id", "genericClick", "genericSearch", "query", "findEditableNode", "Landroid/view/accessibility/AccessibilityNodeInfo;", "node", "volumeUp", "volumeDown", "Result", "app_debug"})
public final class SmartAccessibilityEngine {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_SMART";
    @org.jetbrains.annotations.Nullable()
    private static android.accessibilityservice.AccessibilityService service;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.service.SmartAccessibilityEngine INSTANCE = null;
    
    private SmartAccessibilityEngine() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.accessibilityservice.AccessibilityService getService() {
        return null;
    }
    
    public final void setService(@org.jetbrains.annotations.Nullable()
    android.accessibilityservice.AccessibilityService p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.myra.assistant.service.SmartAccessibilityEngine.Result execute(@org.jetbrains.annotations.NotNull()
    java.lang.String rawCommand) {
        return null;
    }
    
    private final java.lang.String cleanCommand(java.lang.String text) {
        return null;
    }
    
    private final boolean runSmartAutomation(java.lang.String command) {
        return false;
    }
    
    private final boolean handleOpenApp(java.lang.String command) {
        return false;
    }
    
    private final boolean handlePlayMusic(java.lang.String command) {
        return false;
    }
    
    private final android.content.Intent findLaunchIntent(java.lang.String appName) {
        return null;
    }
    
    private final boolean launchPackage(java.lang.String packageName, java.lang.String tag) {
        return false;
    }
    
    public final boolean click(@org.jetbrains.annotations.Nullable()
    java.lang.String text, @org.jetbrains.annotations.Nullable()
    java.lang.String contentDesc, @org.jetbrains.annotations.Nullable()
    java.lang.String id) {
        return false;
    }
    
    private final boolean genericClick(java.lang.String text) {
        return false;
    }
    
    private final boolean genericSearch(java.lang.String query) {
        return false;
    }
    
    private final android.view.accessibility.AccessibilityNodeInfo findEditableNode(android.view.accessibility.AccessibilityNodeInfo node) {
        return null;
    }
    
    private final boolean volumeUp() {
        return false;
    }
    
    private final boolean volumeDown() {
        return false;
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0014\u0010\u000f\u001a\u00020\u00032\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010\u0011\u001a\u00020\u0012H\u00d6\u0081\u0004J\n\u0010\u0013\u001a\u00020\u0005H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0014"}, d2 = {"Lcom/myra/assistant/service/SmartAccessibilityEngine$Result;", "", "success", "", "message", "", "<init>", "(ZLjava/lang/String;)V", "getSuccess", "()Z", "getMessage", "()Ljava/lang/String;", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
    public static final class Result {
        private final boolean success = false;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String message = null;
        
        public Result(boolean success, @org.jetbrains.annotations.NotNull()
        java.lang.String message) {
            super();
        }
        
        public final boolean getSuccess() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMessage() {
            return null;
        }
        
        public final boolean component1() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.myra.assistant.service.SmartAccessibilityEngine.Result copy(boolean success, @org.jetbrains.annotations.NotNull()
        java.lang.String message) {
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