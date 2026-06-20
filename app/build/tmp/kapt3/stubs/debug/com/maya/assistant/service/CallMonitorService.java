package com.myra.assistant.service;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 #2\u00020\u00012\u00020\u0002:\u0001#B\u0007\u00a2\u0006\u0004\b\u0003\u0010\u0004J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\u0010\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u000eH\u0016J\b\u0010\u0014\u001a\u00020\u0011H\u0002J\u0012\u0010\u0015\u001a\u00020\u00112\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0002J\u0012\u0010\u0018\u001a\u00020\u00172\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0002J\b\u0010\u0019\u001a\u00020\u0011H\u0002J\b\u0010\u001a\u001a\u00020\u0011H\u0002J\"\u0010\u001b\u001a\u00020\u000e2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\u0006\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u000eH\u0016J\u0014\u0010 \u001a\u0004\u0018\u00010!2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0016J\b\u0010\"\u001a\u00020\u0011H\u0016R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/myra/assistant/service/CallMonitorService;", "Landroid/app/Service;", "Landroid/speech/tts/TextToSpeech$OnInitListener;", "<init>", "()V", "telephonyManager", "Landroid/telephony/TelephonyManager;", "phoneListener", "Landroid/telephony/PhoneStateListener;", "tts", "Landroid/speech/tts/TextToSpeech;", "ttsReady", "", "lastState", "", "announced", "onCreate", "", "onInit", "status", "setupPhoneListener", "handleIncomingCall", "number", "", "resolveCallerName", "startForegroundService", "createNotificationChannel", "onStartCommand", "intent", "Landroid/content/Intent;", "flags", "startId", "onBind", "Landroid/os/IBinder;", "onDestroy", "Companion", "app_debug"})
public final class CallMonitorService extends android.app.Service implements android.speech.tts.TextToSpeech.OnInitListener {
    @org.jetbrains.annotations.Nullable()
    private android.telephony.TelephonyManager telephonyManager;
    @org.jetbrains.annotations.Nullable()
    private android.telephony.PhoneStateListener phoneListener;
    @org.jetbrains.annotations.Nullable()
    private android.speech.tts.TextToSpeech tts;
    private boolean ttsReady = false;
    private int lastState = android.telephony.TelephonyManager.CALL_STATE_IDLE;
    private boolean announced = false;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_CALL_ACTIVE = "com.myra.assistant.CALL_ACTIVE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_CALL_ENDED = "com.myra.assistant.CALL_ENDED";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_CALL_RINGING = "com.myra.assistant.CALL_RINGING";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_ID = "myra_call_channel";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "MYRA_CALL";
    @org.jetbrains.annotations.NotNull()
    public static final com.myra.assistant.service.CallMonitorService.Companion Companion = null;
    
    public CallMonitorService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public void onInit(int status) {
    }
    
    private final void setupPhoneListener() {
    }
    
    private final void handleIncomingCall(java.lang.String number) {
    }
    
    private final java.lang.String resolveCallerName(java.lang.String number) {
        return null;
    }
    
    private final void startForegroundService() {
    }
    
    private final void createNotificationChannel() {
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
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/myra/assistant/service/CallMonitorService$Companion;", "", "<init>", "()V", "ACTION_CALL_ACTIVE", "", "ACTION_CALL_ENDED", "ACTION_CALL_RINGING", "CHANNEL_ID", "TAG", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}