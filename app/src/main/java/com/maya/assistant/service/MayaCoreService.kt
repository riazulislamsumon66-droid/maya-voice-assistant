package com.maya.assistant.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.maya.assistant.R
import com.maya.assistant.ui.main.MainActivity
import kotlinx.coroutines.*

/**
 * MayaCoreService — Always-on background service
 * 
 * Handles:
 * 1. Hotword detection ("Hey MAYA")
 * 2. Face detection (front camera)
 * 3. Voice authentication (only Jaan's voice)
 * 4. Command execution only when authenticated
 */
class MayaCoreService : Service() {

    companion object {
        private const val TAG = "MayaCoreService"
        private const val CHANNEL_ID = "maya_core_service"
        private const val NOTIFICATION_ID = 2001
        
        // Authentication states
        const val AUTH_NONE = 0
        const val AUTH_VOICE = 1
        const val AUTH_FACE = 2
        const val AUTH_BOTH = 3
        
        var authState = AUTH_NONE
            private set
        
        var isRunning = false
            private set
        
        var onAuthenticated: ((Int) -> Unit)? = null
        var onCommandReceived: ((String) -> Unit)? = null

        var instance: MayaCoreService? = null
            private set

        fun canExecuteCommandsInstance(): Boolean {
            return instance?.canExecuteCommandsInstance() ?: true
        }
    }

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var prefs: SharedPreferences
    
    // Sub-services
    private var hotwordDetector: HotwordDetector? = null
    private var faceDetector: FaceDetector? = null
    private var voiceAuthManager: VoiceAuthManager? = null
    
    // State
    private var isHotwordActive = false
    private var isFaceDetecting = false
    private var lastFaceDetected = 0L
    private var lastVoiceDetected = 0L
    private var isAuthenticated = false
    private var authTimeoutJob: Job? = null

    inner class LocalBinder : Binder() {
        fun getService(): MayaCoreService = this@MayaCoreService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MayaCoreService created")
        prefs = getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
        isRunning = true
        instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "MayaCoreService started")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Initialize sub-services
        initHotwordDetector()
        initFaceDetector()
        initVoiceAuthenticator()
        
        // Start listening
        startListening()
        
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MAYA Core Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Always-on voice assistant"
                setShowBadge(false)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val authStatusText = when (authState) {
            AUTH_NONE -> "🔴 Waiting..."
            AUTH_VOICE -> "🟡 Voice detected"
            AUTH_FACE -> "🟡 Face detected"
            AUTH_BOTH -> "🟢 Authenticated"
            else -> "🔴 Waiting..."
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MAYA Assistant")
            .setContentText(authStatusText)
            .setSmallIcon(R.drawable.ic_maya_notif)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    // ===== INITIALIZATION =====

    private fun initHotwordDetector() {
        hotwordDetector = HotwordDetector(this)
        hotwordDetector?.onHotwordDetected = {
            Log.d(TAG, "Hotword detected!")
            handleVoiceDetected()
        }
    }

    private fun initFaceDetector() {
        faceDetector = FaceDetector(this)
        faceDetector?.onFaceDetected = { isJaan ->
            Log.d(TAG, "Face detected: isJaan=$isJaan")
            if (isJaan) {
                handleFaceDetected()
            }
        }
    }

    private fun initVoiceAuthenticator() {
        voiceAuthManager = VoiceAuthManager(this)
    }

    // ===== AUTHENTICATION LOGIC =====

    private fun startListening() {
        // Start hotword detection
        hotwordDetector?.start()
        
        // Start periodic face detection (every 5 seconds)
        startPeriodicFaceDetection()
        
        Log.d(TAG, "Listening started — hotword + face detection active")
    }

    private fun startPeriodicFaceDetection() {
        serviceScope.launch {
            while (isRunning) {
                if (!isAuthenticated && authState != AUTH_VOICE) {
                    // Only detect face if not already voice-authenticated
                    faceDetector?.detectOnce()
                }
                delay(5000) // Check every 5 seconds
            }
        }
    }

    private fun handleVoiceDetected() {
        lastVoiceDetected = System.currentTimeMillis()
        
        // Verify voice against enrolled profile
        serviceScope.launch {
            val verified = voiceAuthManager?.verifyVoice() ?: true
            
            if (verified) {
                // Voice matched — full access
                val faceRecent = System.currentTimeMillis() - lastFaceDetected < 10000
                authState = if (faceRecent) AUTH_BOTH else AUTH_VOICE
                isAuthenticated = true
                
                Log.d(TAG, "Voice verified! Auth state: $authState")
                updateNotification()
                onAuthenticated?.invoke(authState)
                startCommandListening()
            } else {
                // Voice NOT matched — conversation only
                authState = AUTH_NONE
                isAuthenticated = false
                
                Log.d(TAG, "Voice NOT verified — conversation only")
                updateNotification()
                // Still start listening but commands won't work
                startCommandListening()
            }
            
            // Set auth timeout (30 seconds)
            authTimeoutJob?.cancel()
            authTimeoutJob = serviceScope.launch {
                delay(30000)
                resetAuth()
            }
        }
    }

    private fun handleFaceDetected() {
        lastFaceDetected = System.currentTimeMillis()
        
        // Check if voice is also detected recently (within 10 seconds)
        val voiceRecent = System.currentTimeMillis() - lastVoiceDetected < 10000
        
        authState = if (voiceRecent) {
            AUTH_BOTH
        } else {
            AUTH_FACE
        }
        
        isAuthenticated = true
        updateNotification()
        onAuthenticated?.invoke(authState)
        
        // Set auth timeout (30 seconds)
        authTimeoutJob?.cancel()
        authTimeoutJob = serviceScope.launch {
            delay(30000)
            if (authState != AUTH_BOTH) {
                resetAuth()
            }
        }
    }

    private fun resetAuth() {
        authState = AUTH_NONE
        isAuthenticated = false
        isHotwordActive = false
        updateNotification()
        Log.d(TAG, "Auth reset — waiting for hotword/face")
    }

    private fun startCommandListening() {
        // Now listen for actual command via Gemini Live
        // This triggers the main activity to start listening
        serviceScope.launch {
            // Wait for user to speak command
            delay(500)
            onCommandReceived?.invoke("LISTENING")
        }
    }

    private fun updateNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, createNotification())
    }

    // ===== PUBLIC API =====

    fun isServiceAuthenticated(): Boolean = isAuthenticated

    fun getAuthStateInt(): Int = authState

    fun setVoiceEnrolled() {
        serviceScope.launch {
            voiceAuthManager?.enrollVoice()
        }
    }

    fun setFaceEnrolled() {
        CoroutineScope(Dispatchers.IO).launch {
            faceDetector?.enrollFace(this@MayaCoreService)
        }
    }

    fun forceAuthenticate() {
        // For testing — skip auth
        authState = AUTH_BOTH
        isAuthenticated = true
        updateNotification()
    }

    fun logout() {
        resetAuth()
    }

    fun getVoiceAuthStatus(): String {
        return voiceAuthManager?.getStatusText() ?: "⚠️ Voice auth not initialized"
    }

    fun canExecuteCommandsInstance(): Boolean {
        return voiceAuthManager?.canExecuteCommands() ?: true
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        instance = null
        hotwordDetector?.stop()
        faceDetector?.stop()
        voiceAuthManager?.stop()
        serviceScope.cancel()
        Log.d(TAG, "MayaCoreService destroyed")
    }
}
