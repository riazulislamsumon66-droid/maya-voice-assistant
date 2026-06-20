package com.myra.assistant.services;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 )2\u00020\u0001:\u0001)B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\b\u0010\u0016\u001a\u00020\u0015H\u0002J\u0010\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0005H\u0002J\u000e\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u001a\u001a\u00020\u0005J\u0006\u0010\u001b\u001a\u00020\u0015J\b\u0010\u001c\u001a\u00020\u0005H\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0002J\b\u0010\u001f\u001a\u00020\u0015H\u0002J\"\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#2\u0006\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020!H\u0016J\u0014\u0010&\u001a\u0004\u0018\u00010\'2\b\u0010\"\u001a\u0004\u0018\u00010#H\u0016J\b\u0010(\u001a\u00020\u0015H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/myra/assistant/services/ForegroundVoiceService;", "Landroid/app/Service;", "<init>", "()V", "TAG", "", "job", "Lkotlinx/coroutines/CompletableJob;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "audioRecorder", "Lcom/myra/assistant/voice/AudioRecorder;", "audioPlayer", "Lcom/myra/assistant/voice/AudioPlayer;", "vad", "Lcom/myra/assistant/voice/VoiceActivityDetector;", "audioFocus", "Lcom/myra/assistant/voice/AudioFocusManager;", "geminiClient", "Lcom/myra/assistant/websocket/GeminiWebSocketClient;", "onCreate", "", "initComponents", "connectGemini", "apiKey", "sendTextToGemini", "text", "reconnectGemini", "buildSystemPrompt", "buildNotification", "Landroid/app/Notification;", "createChannel", "onStartCommand", "", "intent", "Landroid/content/Intent;", "flags", "startId", "onBind", "Landroid/os/IBinder;", "onDestroy", "Companion", "app_debug"})
public final class ForegroundVoiceService extends android.app.Service {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "VOICE_SVC";
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CompletableJob job = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    private com.myra.assistant.voice.AudioRecorder audioRecorder;
    private com.myra.assistant.voice.AudioPlayer audioPlayer;
    private com.myra.assistant.voice.VoiceActivityDetector vad;
    private com.myra.assistant.voice.AudioFocusManager audioFocus;
    @org.jetbrains.annotations.Nullable()
    private com.myra.assistant.websocket.GeminiWebSocketClient geminiClient;
    @org.jetbrains.annotations.Nullable()
    private static com.myra.assistant.services.ForegroundVoiceService instance;
    private static boolean isRunning = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.services.ForegroundVoiceService.Companion Companion = null;
    
    public ForegroundVoiceService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    private final void initComponents() {
    }
    
    private final void connectGemini(java.lang.String apiKey) {
    }
    
    public final void sendTextToGemini(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    public final void reconnectGemini() {
    }
    
    private final java.lang.String buildSystemPrompt() {
        return null;
    }
    
    private final android.app.Notification buildNotification() {
        return null;
    }
    
    private final void createChannel() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/myra/assistant/services/ForegroundVoiceService$Companion;", "", "<init>", "()V", "instance", "Lcom/myra/assistant/services/ForegroundVoiceService;", "getInstance", "()Lcom/myra/assistant/services/ForegroundVoiceService;", "setInstance", "(Lcom/myra/assistant/services/ForegroundVoiceService;)V", "isRunning", "", "()Z", "setRunning", "(Z)V", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.myra.assistant.services.ForegroundVoiceService getInstance() {
            return null;
        }
        
        public final void setInstance(@org.jetbrains.annotations.Nullable()
        com.myra.assistant.services.ForegroundVoiceService p0) {
        }
        
        public final boolean isRunning() {
            return false;
        }
        
        public final void setRunning(boolean p0) {
        }
    }
}