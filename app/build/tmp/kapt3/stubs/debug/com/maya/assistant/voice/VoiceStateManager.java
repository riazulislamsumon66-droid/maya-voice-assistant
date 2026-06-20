package com.myra.assistant.voice;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u0012J\u0006\u0010\u0014\u001a\u00020\u0012J\u0006\u0010\u0015\u001a\u00020\u0012J\u000e\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0006J\u000e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\rJ\u0006\u0010\u001a\u001a\u00020\u0010J\u0006\u0010\u001b\u001a\u00020\u0010R\u001f\u0010\u0004\u001a\u0010\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001f\u0010\n\u001a\u0010\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u001f\u0010\f\u001a\u0010\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\r0\r0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\tR\u001f\u0010\u000f\u001a\u0010\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00100\u00100\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\t\u00a8\u0006\u001c"}, d2 = {"Lcom/myra/assistant/voice/VoiceStateManager;", "", "<init>", "()V", "state", "Landroidx/lifecycle/MutableLiveData;", "", "kotlin.jvm.PlatformType", "getState", "()Landroidx/lifecycle/MutableLiveData;", "statusMessage", "getStatusMessage", "amplitude", "", "getAmplitude", "isMicMuted", "", "setListening", "", "setThinking", "setSpeaking", "setIdle", "setError", "msg", "updateAmplitude", "rms", "isAiSpeaking", "isListening", "app_debug"})
public final class VoiceStateManager {
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.String> state = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.String> statusMessage = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.Float> amplitude = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.Boolean> isMicMuted = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.voice.VoiceStateManager INSTANCE = null;
    
    private VoiceStateManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.String> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.String> getStatusMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Float> getAmplitude() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> isMicMuted() {
        return null;
    }
    
    public final void setListening() {
    }
    
    public final void setThinking() {
    }
    
    public final void setSpeaking() {
    }
    
    public final void setIdle() {
    }
    
    public final void setError(@org.jetbrains.annotations.NotNull()
    java.lang.String msg) {
    }
    
    public final void updateAmplitude(float rms) {
    }
    
    public final boolean isAiSpeaking() {
        return false;
    }
    
    public final boolean isListening() {
        return false;
    }
}