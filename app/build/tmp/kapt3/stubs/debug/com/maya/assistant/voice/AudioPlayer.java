package com.myra.assistant.voice;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u000e\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u001b\u001a\u00020\u0013H\u0002J\b\u0010\u001c\u001a\u00020\u0013H\u0002J\u000e\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u001e\u001a\u00020\fJ\u0006\u0010\u001f\u001a\u00020\u0013J\u0006\u0010 \u001a\u00020\u0013R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\"\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\u0012X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\"\u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\u0012X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0015\"\u0004\b\u001a\u0010\u0017\u00a8\u0006!"}, d2 = {"Lcom/myra/assistant/voice/AudioPlayer;", "", "<init>", "()V", "TAG", "", "SAMPLE_RATE", "", "audioTrack", "Landroid/media/AudioTrack;", "queue", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "", "isPlaying", "", "playThread", "Ljava/lang/Thread;", "onPlaybackStarted", "Lkotlin/Function0;", "", "getOnPlaybackStarted", "()Lkotlin/jvm/functions/Function0;", "setOnPlaybackStarted", "(Lkotlin/jvm/functions/Function0;)V", "onPlaybackFinished", "getOnPlaybackFinished", "setOnPlaybackFinished", "initTrack", "startPlayThread", "playChunk", "data", "clearAndStop", "release", "app_debug"})
public final class AudioPlayer {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "PLAYER";
    private final int SAMPLE_RATE = 24000;
    @org.jetbrains.annotations.Nullable()
    private android.media.AudioTrack audioTrack;
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentLinkedQueue<byte[]> queue = null;
    @kotlin.jvm.Volatile()
    private volatile boolean isPlaying = false;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Thread playThread;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function0<kotlin.Unit> onPlaybackStarted;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function0<kotlin.Unit> onPlaybackFinished;
    
    public AudioPlayer() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnPlaybackStarted() {
        return null;
    }
    
    public final void setOnPlaybackStarted(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnPlaybackFinished() {
        return null;
    }
    
    public final void setOnPlaybackFinished(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    private final void initTrack() {
    }
    
    private final void startPlayThread() {
    }
    
    public final void playChunk(@org.jetbrains.annotations.NotNull()
    byte[] data) {
    }
    
    public final void clearAndStop() {
    }
    
    public final void release() {
    }
}