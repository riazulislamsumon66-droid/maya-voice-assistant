package com.myra.assistant.utils;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0014\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\n\u00a8\u0006\u000f"}, d2 = {"Lcom/myra/assistant/utils/AudioUtils;", "", "<init>", "()V", "getMinBufferSize", "", "sampleRate", "pcm16ToFloat", "", "pcm", "", "calculateRms", "", "pcmToBase64", "", "app_debug"})
public final class AudioUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.utils.AudioUtils INSTANCE = null;
    
    private AudioUtils() {
        super();
    }
    
    public final int getMinBufferSize(int sampleRate) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final float[] pcm16ToFloat(@org.jetbrains.annotations.NotNull()
    byte[] pcm) {
        return null;
    }
    
    public final float calculateRms(@org.jetbrains.annotations.NotNull()
    byte[] pcm) {
        return 0.0F;
    }
    
    /**
     * Convert PCM bytes to base64 for WebSocket transmission
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String pcmToBase64(@org.jetbrains.annotations.NotNull()
    byte[] pcm) {
        return null;
    }
}