package com.myra.assistant.voice;

/**
 * Receives audio chunks from Gemini and routes to AudioPlayer
 */
@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0006\u0010\n\u001a\u00020\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/myra/assistant/voice/AudioStreamReceiver;", "", "player", "Lcom/myra/assistant/voice/AudioPlayer;", "<init>", "(Lcom/myra/assistant/voice/AudioPlayer;)V", "receive", "", "data", "", "clear", "app_debug"})
public final class AudioStreamReceiver {
    @org.jetbrains.annotations.NotNull()
    private final com.myra.assistant.voice.AudioPlayer player = null;
    
    public AudioStreamReceiver(@org.jetbrains.annotations.NotNull()
    com.myra.assistant.voice.AudioPlayer player) {
        super();
    }
    
    public final void receive(@org.jetbrains.annotations.NotNull()
    byte[] data) {
    }
    
    public final void clear() {
    }
}