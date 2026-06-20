package com.myra.assistant.voice;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\rR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/myra/assistant/voice/EchoCancellationManager;", "", "<init>", "()V", "TAG", "", "aec", "Landroid/media/audiofx/AcousticEchoCanceler;", "ns", "Landroid/media/audiofx/NoiseSuppressor;", "agc", "Landroid/media/audiofx/AutomaticGainControl;", "attach", "", "audioSessionId", "", "release", "app_debug"})
public final class EchoCancellationManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "ECHO";
    @org.jetbrains.annotations.Nullable()
    private static android.media.audiofx.AcousticEchoCanceler aec;
    @org.jetbrains.annotations.Nullable()
    private static android.media.audiofx.NoiseSuppressor ns;
    @org.jetbrains.annotations.Nullable()
    private static android.media.audiofx.AutomaticGainControl agc;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.voice.EchoCancellationManager INSTANCE = null;
    
    private EchoCancellationManager() {
        super();
    }
    
    public final void attach(int audioSessionId) {
    }
    
    public final void release() {
    }
}