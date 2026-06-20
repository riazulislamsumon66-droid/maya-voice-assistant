package com.maya.assistant.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.maya.assistant.R
import com.maya.assistant.ai.AIResponseManager
import com.maya.assistant.ai.ConversationMemory
import com.maya.assistant.ai.DynamicDecisionEngine
import com.maya.assistant.ai.IntentAnalyzer
import com.maya.assistant.models.CommandType
import com.maya.assistant.ui.main.MainActivity
import com.maya.assistant.utils.Constants
import com.maya.assistant.utils.Logger
import com.maya.assistant.utils.prefs
import com.maya.assistant.voice.AudioFocusManager
import com.maya.assistant.voice.AudioPlayer
import com.maya.assistant.voice.AudioRecorder
import com.maya.assistant.voice.VoiceActivityDetector
import com.maya.assistant.voice.VoiceStateManager
import com.maya.assistant.websocket.GeminiWebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ForegroundVoiceService : Service() {
    private val TAG = "VOICE_SVC"
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var audioRecorder: AudioRecorder
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var vad: VoiceActivityDetector
    private lateinit var audioFocus: AudioFocusManager
    private var geminiClient: GeminiWebSocketClient? = null

    companion object {
        var instance: ForegroundVoiceService? = null
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        isRunning = true
        startForeground(Constants.NOTIF_ID_VOICE, buildNotification())
        initComponents()
    }

    private fun initComponents() {
        audioFocus = AudioFocusManager(this)
        audioPlayer = AudioPlayer()

        audioPlayer.onPlaybackStarted = {
            VoiceStateManager.setSpeaking()
        }
        audioPlayer.onPlaybackFinished = {
            VoiceStateManager.setListening()
        }

        vad = VoiceActivityDetector(
            onSpeechStart = {
                VoiceStateManager.setListening()
            },
            onSpeechEnd = {
                VoiceStateManager.setThinking()
            }
        )

        audioRecorder = AudioRecorder(this) { chunk ->
            // Don't send audio while AI is speaking
            if (!VoiceStateManager.isAiSpeaking()) {
                vad.processChunk(chunk)
                geminiClient?.sendAudioChunk(chunk)
            }
        }

        val apiKey = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
        if (apiKey.isNotEmpty()) {
            connectGemini(apiKey)
        }
    }

    private fun connectGemini(apiKey: String) {
        geminiClient = GeminiWebSocketClient(
            apiKey = apiKey,
            systemPrompt = buildSystemPrompt(),
            onConnected = {
                VoiceStateManager.setListening()
                audioRecorder.start()
            },
            onAudioReceived = { data ->
                audioPlayer.playChunk(data)
            },
            onTextReceived = { text ->
                val clean = AIResponseManager.clean(text)
                if (clean.isNotBlank()) {
                    ConversationMemory.addAssistant(clean)
                    
                    // Try structured command first
                    val cmd = AIResponseManager.extractCommand(clean)
                    if (cmd != null) {
                        scope.launch {
                            val intent = IntentAnalyzer.analyze(cmd)
                            DynamicDecisionEngine.execute(this@ForegroundVoiceService, intent)
                        }
                    } else {
                        // Direct natural language action detection
                        scope.launch {
                            val intent = IntentAnalyzer.analyze(clean)
                            if (intent.type != CommandType.CONVERSATION) {
                                DynamicDecisionEngine.execute(this@ForegroundVoiceService, intent)
                            }
                        }
                    }
                    
                    // Broadcast to UI
                    sendBroadcast(Intent("MAYA_RESPONSE").putExtra("text", clean))
                }
            },
            onTurnComplete = {
                // Keep listening after AI finishes
                if (!audioRecorder.isActive()) audioRecorder.start()
            },
            onError = { msg ->
                Logger.e(TAG, "Gemini error: $msg")
                VoiceStateManager.setError("পুনরায় Connected হচ্ছে…")
                // Auto-reconnect after delay
                scope.launch {
                    kotlinx.coroutines.delay(3000)
                    geminiClient?.connect()
                }
            }
        )
        geminiClient?.connect()
    }

    fun sendTextToGemini(text: String) {
        ConversationMemory.addUser(text)
        VoiceStateManager.setThinking()
        geminiClient?.sendTextMessage(text)
    }

    // Direct action execution — bypasses Gemini for known commands
    fun executeDirectAction(userText: String) {
        val lower = userText.lowercase().trim()
        
        // Direct app open detection
        val openPatterns = listOf("open", "kholo", "khol", "launch", "start", "খোলো", "খোল", "চালু", "ওপেন")
        for (pattern in openPatterns) {
            if (lower.contains(pattern)) {
                val appName = lower.substringAfter(pattern).trim()
                if (appName.isNotEmpty()) {
                    AppLauncher.launch(this@ForegroundVoiceService, appName)
                    Log.d(TAG, "DIRECT OPEN: $appName")
                    return
                }
            }
        }
        
        // Direct volume control
        if (lower.contains("volume up") || lower.contains("ভলিউম বাড়াও") || lower.contains("জোরে")) {
            val am = getSystemService(AUDIO_SERVICE) as? android.media.AudioManager
            am?.adjustVolume(android.media.AudioManager.ADJUST_RAISE, android.media.AudioManager.FLAG_SHOW_UI)
            Log.d(TAG, "DIRECT VOLUME UP")
            return
        }
        
        if (lower.contains("volume down") || lower.contains("ভলিউম কমাও") || lower.contains("কম")) {
            val am = getSystemService(AUDIO_SERVICE) as? android.media.AudioManager
            am?.adjustVolume(android.media.AudioManager.ADJUST_LOWER, android.media.AudioManager.FLAG_SHOW_UI)
            Log.d(TAG, "DIRECT VOLUME DOWN")
            return
        }
        
        // Go through Gemini if no direct match
        sendTextToGemini(userText)
    }

    fun reconnectGemini() {
        val apiKey = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
        if (apiKey.isNotEmpty()) {
            geminiClient?.disconnect()
            connectGemini(apiKey)
        }
    }

    private fun buildSystemPrompt(): String {
        val userName = prefs().getString(Constants.KEY_USER_NAME, "Boss") ?: "Boss"
        val personality = prefs().getString(Constants.KEY_PERSONALITY, "friendly") ?: "friendly"
        return """
YOU ARE MAYA - My Yours Responsive Assistant.
User's name is $userName.
Personality: $personality.

## CRITICAL RULES:
- For ANY device/app action, you MUST respond with ONLY the command in this exact format:
  OPEN_APP <app_name> | CALL <name> | WHATSAPP_CALL <name>
  WHATSAPP_MSG <name> <message> | YOUTUBE_PLAY <query>
  SPOTIFY_PLAY <query> | FLASHLIGHT_ON | FLASHLIGHT_OFF
  VOLUME_UP | VOLUME_DOWN | SMS <name> <message>
- Do NOT add any explanation, thinking, or extra text before/after the command
- Do NOT say "Responding to", "I've registered", "Formulating", "Interpreting", "Processing"
- For conversation only (no action needed): Reply short and natural in Banglish
- Address user as $userName
- Be warm, witty, and human-like

## EXAMPLES:
User: "YouTube kholo"
You: OPEN_APP YouTube

User: "WhatsApp e message pathao Rahul ke"
You: WHATSAPP_MSG Rahul Hello, how are you?

User: "Call karo Sumon ke"
You: CALL Sumon

User: "Volume badao"
You: VOLUME_UP

User: "Kya haal hai?"
You: Hey $userName! Sab badhiya, aap batao? ❤️
        """.trimIndent()
    }

    private fun buildNotification(): Notification {
        createChannel()
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, Constants.NOTIF_CHANNEL_VOICE)
            .setContentTitle("MAYA Listening ❤️")
            .setContentText("সবসময় ready for you")
            .setSmallIcon(R.drawable.ic_maya_notif)
            .setContentIntent(pi)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                Constants.NOTIF_CHANNEL_VOICE,
                "MAYA Voice Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply { setShowBadge(false) }
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        isRunning = false
        instance = null
        audioRecorder.stop()
        audioPlayer.release()
        geminiClient?.disconnect()
        job.cancel()
        super.onDestroy()
    }
}
