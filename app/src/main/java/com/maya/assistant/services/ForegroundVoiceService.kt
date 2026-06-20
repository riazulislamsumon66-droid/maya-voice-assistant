package com.maya.assistant.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.নাtificationCompat
import com.maya.assistant.R
import com.maya.assistant.ai.AIResponseManager
import com.maya.assistant.ai.Conversationমেমোরি
import com.maya.assistant.ai.IntentAnalyzer
import com.maya.assistant.ai.DynamicDecisionEngine
import com.maya.assistant.ui.main.MainActivity
import com.maya.assistant.utils.Constants
import com.maya.assistant.utils.Logger
import com.maya.assistant.utils.prefs
import com.maya.assistant.voice.AudioFocusManager
import com.maya.assistant.voice.AudioPlayer
import com.maya.assistant.voice.AudioRecorder
import com.maya.assistant.voice.ভয়েসActivityDetector
import com.maya.assistant.voice.ভয়েসStateManager
import com.maya.assistant.websocket.GeminiWebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Foregroundভয়েসService : Service() {
    private val TAG = "VOICE_SVC"
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var audioRecorder: AudioRecorder
    private lateinit var audioPlayer: AudioPlayer
    private lateinit var vad: ভয়েসActivityDetector
    private lateinit var audioFocus: AudioFocusManager
    private var geminiClient: GeminiWebSocketClient? = null

    companion object {
        var instance: Foregroundভয়েসService? = null
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        isRunning = true
        startForeground(Constants.NOTIF_ID_VOICE, buildনাtification())
        initComponents()
    }

    private fun initComponents() {
        audioFocus = AudioFocusManager(this)
        audioPlayer = AudioPlayer()

        audioPlayer.onPlaybackশুরু করোed = {
            ভয়েসStateManager.setবলছে…()
        }
        audioPlayer.onPlaybackFinished = {
            ভয়েসStateManager.setশুনছে…()
        }

        vad = ভয়েসActivityDetector(
            onSpeechশুরু করো = {
                ভয়েসStateManager.setশুনছে…()
            },
            onSpeechEnd = {
                ভয়েসStateManager.setভাবছে…()
            }
        )

        audioRecorder = AudioRecorder(this) { chunk ->
            // Don't send audio while AI is speaking
            if (!ভয়েসStateManager.isAiবলছে…()) {
                vad.processChunk(chunk)
                geminiClient?.sendAudioChunk(chunk)
            }
        }

        val apiKey = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
        if (apiKey.isনাtEmpty()) {
            connectGemini(apiKey)
        }
    }

    private fun connectGemini(apiKey: String) {
        geminiClient = GeminiWebSocketClient(
            apiKey = apiKey,
            systemPrompt = buildসিস্টেমPrompt(),
            onসংযুক্ত ✅ = {
                ভয়েসStateManager.setশুনছে…()
                audioRecorder.start()
            },
            onAudioReceived = { data ->
                audioPlayer.playChunk(data)
            },
            onTextReceived = { text ->
                val clean = AIResponseManager.clean(text)
                if (clean.isনাtBlank()) {
                    Conversationমেমোরি.addঅ্যাসিস্ট্যান্ট(clean)
                    val cmd = AIResponseManager.extractCommand(clean)
                    if (cmd != null) {
                        scope.launch {
                            val intent = IntentAnalyzer.analyze(cmd)
                            DynamicDecisionEngine.execute(this@Foregroundভয়েসService, intent)
                        }
                    }
                    // Broadcast to UI
                    sendBroadcast(Intent("MAYA_RESPONSE").putExtra("text", clean))
                }
            },
            onTurnComplete = {
                // চালিয়ে যাও listening after AI finishes
                if (!audioRecorder.isসক্রিয়()) audioRecorder.start()
            },
            onসমস্যা = { msg ->
                Logger.e(TAG, "Gemini error: $msg")
                ভয়েসStateManager.setসমস্যা("পুনরায় সংযুক্ত হচ্ছে…")
                // অটো-reconnect after delay
                scope.launch {
                    kotlinx.coroutines.delay(3000)
                    geminiClient?.connect()
                }
            }
        )
        geminiClient?.connect()
    }

    fun sendTextToGemini(text: String) {
        Conversationমেমোরি.addব্যবহারকারী(text)
        ভয়েসStateManager.setভাবছে…()
        geminiClient?.sendTextমেসেজ(text)
    }

    fun reconnectGemini() {
        val apiKey = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
        if (apiKey.isনাtEmpty()) {
            geminiClient?.disconnect()
            connectGemini(apiKey)
        }
    }

    private fun buildসিস্টেমPrompt(): String {
        val userনাম = prefs().getString(Constants.KEY_USER_NAME, "Boss") ?: "Boss"
        val personality = prefs().getString(Constants.KEY_PERSONALITY, "friendly") ?: "friendly"
        return """
YOU ARE MAYA - My Yours Responsive অ্যাসিস্ট্যান্ট.
ব্যবহারকারী's name is $userনাম.
ব্যক্তিগতity: $personality.

STRICT RULES:
- কখনো না explain or think aloud
- কখনো না say: "Responding to", "I've registered", "Formulating", "Interpreting", "Processing"
- For device actions return ONLY the command:
  OPEN_APP <name> | CALL <name> | WHATSAPP_CALL <name>
  WHATSAPP_MSG <name> <message> | YOUTUBE_PLAY <query>
  SPOTIFY_PLAY <query> | FLASHLIGHT_ON | FLASHLIGHT_OFF
  VOLUME_UP | VOLUME_DOWN | SMS <name> <message>
- For conversation: Reply short and natural in হাইnglish
- যোগ করোress user as $userনাম
- Be warm, witty, and human-like
        """.trimIndent()
    }

    private fun buildনাtification(): নাtification {
        createChannel()
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return নাtificationCompat.Builder(this, Constants.NOTIF_CHANNEL_VOICE)
            .setContentTitle("MAYA শুনছে ❤️")
            .setContentText("সবসময় ready for you")
            .setSmallIcon(R.drawable.ic_maya_notif)
            .setContentIntent(pi)
            .setচালুgoing(true)
            .setPriority(নাtificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = নাtificationChannel(
                Constants.NOTIF_CHANNEL_VOICE,
                "MAYA ভয়েস Service",
                নাtificationManager.IMPORTANCE_LOW
            ).apply { setদেখাওBadge(false) }
            getসিস্টেমService(নাtificationManager::class.java).createনাtificationChannel(ch)
        }
    }

    override fun onশুরু করোCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

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
