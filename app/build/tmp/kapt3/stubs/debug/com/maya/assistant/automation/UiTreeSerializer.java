package com.myra.assistant.automation;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005H\u0002J\u0010\u0010\b\u001a\u00020\u00052\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u001e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\n0\u00112\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0012\u001a\u00020\u0005J&\u0010\u0013\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u00052\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\u0015H\u0002J\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\n0\u00112\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u001e\u0010\u0017\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\u0015H\u0002J\u001a\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u001a0\u00192\u0006\u0010\r\u001a\u00020\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/myra/assistant/automation/UiTreeSerializer;", "", "<init>", "()V", "TAG", "", "normalize", "text", "serialize", "root", "Landroid/view/accessibility/AccessibilityNodeInfo;", "traverse", "", "node", "array", "Lorg/json/JSONArray;", "findMatchingElements", "", "query", "findMatchingElementsRecursive", "results", "", "findClickableElements", "findClickableElementsRecursive", "getNodeCenter", "Lkotlin/Pair;", "", "app_debug"})
public final class UiTreeSerializer {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_UI_TREE";
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.automation.UiTreeSerializer INSTANCE = null;
    
    private UiTreeSerializer() {
        super();
    }
    
    private final java.lang.String normalize(java.lang.String text) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String serialize(@org.jetbrains.annotations.Nullable()
    android.view.accessibility.AccessibilityNodeInfo root) {
        return null;
    }
    
    private final void traverse(android.view.accessibility.AccessibilityNodeInfo node, org.json.JSONArray array) {
    }
    
    /**
     * Find elements matching a query in the UI tree
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<android.view.accessibility.AccessibilityNodeInfo> findMatchingElements(@org.jetbrains.annotations.Nullable()
    android.view.accessibility.AccessibilityNodeInfo root, @org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    private final void findMatchingElementsRecursive(android.view.accessibility.AccessibilityNodeInfo node, java.lang.String query, java.util.List<android.view.accessibility.AccessibilityNodeInfo> results) {
    }
    
    /**
     * Get all clickable elements
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<android.view.accessibility.AccessibilityNodeInfo> findClickableElements(@org.jetbrains.annotations.Nullable()
    android.view.accessibility.AccessibilityNodeInfo root) {
        return null;
    }
    
    private final void findClickableElementsRecursive(android.view.accessibility.AccessibilityNodeInfo node, java.util.List<android.view.accessibility.AccessibilityNodeInfo> results) {
    }
    
    /**
     * Get node center coordinates
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<java.lang.Integer, java.lang.Integer> getNodeCenter(@org.jetbrains.annotations.NotNull()
    android.view.accessibility.AccessibilityNodeInfo node) {
        return null;
    }
}