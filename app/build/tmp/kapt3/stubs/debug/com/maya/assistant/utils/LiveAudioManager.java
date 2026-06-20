package com.myra.assistant.utils;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0010\u0002\n\u0002\b\r\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u0018H\u0002J\u000e\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u0010J\u000e\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u0007J\u000e\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u001f\u001a\u00020\u0010J\u0006\u0010 \u001a\u00020\u0018J\u0006\u0010!\u001a\u00020\u0018J\u0006\u0010\"\u001a\u00020\u0018J\u0010\u0010#\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u0010H\u0002J\u0006\u0010$\u001a\u00020\u0018R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00100\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/myra/assistant/utils/LiveAudioManager;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "TAG", "", "SAMPLE_RATE", "", "CHANNEL_CONFIG", "AUDIO_FORMAT", "audioTrack", "Landroid/media/AudioTrack;", "audioQueue", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "", "isPlaying", "", "playbackThread", "Ljava/lang/Thread;", "audioBuffer", "", "initAudioTrack", "", "startPlaybackThread", "playChunk", "data", "playAudioFromPath", "path", "playMp3Data", "mp3Data", "stop", "pause", "resume", "isBase64", "clearQueue", "app_debug"})
public final class LiveAudioManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "MYRA_AUDIO";
    private final int SAMPLE_RATE = 24000;
    private final int CHANNEL_CONFIG = android.media.AudioFormat.CHANNEL_OUT_MONO;
    private final int AUDIO_FORMAT = android.media.AudioFormat.ENCODING_PCM_16BIT;
    @org.jetbrains.annotations.Nullable()
    private android.media.AudioTrack audioTrack;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentLinkedQueue<byte[]> audioQueue = null;
    private boolean isPlaying = false;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Thread playbackThread;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<byte[]> audioBuffer = null;
    
    public LiveAudioManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    private final void initAudioTrack() {
    }
    
    private final void startPlaybackThread() {
    }
    
    /**
     * Play audio chunk - for PCM data from Gemini Live API
     */
    public final void playChunk(@org.jetbrains.annotations.NotNull()
    byte[] data) {
    }
    
    /**
     * Play MP3 audio from URL or file path
     */
    public final void playAudioFromPath(@org.jetbrains.annotations.NotNull()
    java.lang.String path) {
    }
    
    /**
     * Play MP3 from byte array
     */
    public final void playMp3Data(@org.jetbrains.annotations.NotNull()
    byte[] mp3Data) {
    }
    
    public final void stop() {
    }
    
    public final void pause() {
    }
    
    public final void resume() {
    }
    
    private final boolean isBase64(byte[] data) {
        return false;
    }
    
    public final void clearQueue() {
    }
}