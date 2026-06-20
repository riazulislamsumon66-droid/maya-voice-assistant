package com.myra.assistant.accessibility;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J\u0006\u0010\u0006\u001a\u00020\u0005J\u0010\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u0002J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\tH\u0002\u00a8\u0006\r"}, d2 = {"Lcom/myra/assistant/accessibility/ScrollController;", "", "<init>", "()V", "scrollDown", "", "scrollUp", "scroll", "action", "", "doScroll", "node", "Landroid/view/accessibility/AccessibilityNodeInfo;", "app_debug"})
public final class ScrollController {
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.accessibility.ScrollController INSTANCE = null;
    
    private ScrollController() {
        super();
    }
    
    public final boolean scrollDown() {
        return false;
    }
    
    public final boolean scrollUp() {
        return false;
    }
    
    private final boolean scroll(int action) {
        return false;
    }
    
    private final boolean doScroll(android.view.accessibility.AccessibilityNodeInfo node, int action) {
        return false;
    }
}