package com.myra.assistant.automation;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0010B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\t\u001a\u00020\nJ\u0006\u0010\u000b\u001a\u00020\nJ\u0006\u0010\f\u001a\u00020\nJ\u0006\u0010\r\u001a\u00020\nJ\u0006\u0010\u000e\u001a\u00020\u000fR\u001e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\u0011"}, d2 = {"Lcom/myra/assistant/automation/AutomationStateManager;", "", "<init>", "()V", "value", "Lcom/myra/assistant/automation/AutomationStateManager$State;", "state", "getState", "()Lcom/myra/assistant/automation/AutomationStateManager$State;", "setRunning", "", "setIdle", "setPaused", "setError", "isRunning", "", "State", "app_debug"})
public final class AutomationStateManager {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.NotNull()
    private static volatile com.myra.assistant.automation.AutomationStateManager.State state = com.myra.assistant.automation.AutomationStateManager.State.IDLE;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.automation.AutomationStateManager INSTANCE = null;
    
    private AutomationStateManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.myra.assistant.automation.AutomationStateManager.State getState() {
        return null;
    }
    
    public final void setRunning() {
    }
    
    public final void setIdle() {
    }
    
    public final void setPaused() {
    }
    
    public final void setError() {
    }
    
    public final boolean isRunning() {
        return false;
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lcom/myra/assistant/automation/AutomationStateManager$State;", "", "<init>", "(Ljava/lang/String;I)V", "IDLE", "RUNNING", "PAUSED", "ERROR", "app_debug"})
    public static enum State {
        /*public static final*/ IDLE /* = new IDLE() */,
        /*public static final*/ RUNNING /* = new RUNNING() */,
        /*public static final*/ PAUSED /* = new PAUSED() */,
        /*public static final*/ ERROR /* = new ERROR() */;
        
        State() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.myra.assistant.automation.AutomationStateManager.State> getEntries() {
            return null;
        }
    }
}