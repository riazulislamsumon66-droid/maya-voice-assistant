package com.maya.assistant.ai

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiLiveClient(private val context: Context) {

    companion object {
        private const val TAG = "GeminiLiveClient"
        private const val WS_BASE_URL = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent"
        private const val SESSION_RENEW_AFTER = 540_000L // 9 minutes
        private const val KEEPALIVE_INTERVAL = 8_000L // 8 seconds
        private const val RECONNECT_DELAY = 3_000L // 3 seconds
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)

    private var webSocket: WebSocket? = null
    private var isConnected = false
    private var sessionStartTime = 0L
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Callbacks
    var onConnected: (() -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onAudioReceived: ((ByteArray) -> Unit)? = null
    var onOutputTranscript: ((String) -> Unit)? = null
    var onInputTranscript: ((String) -> Unit)? = null
    var onTurnComplete: (() -> Unit)? = null
    var onSetupComplete: (() -> Unit)? = null

    fun connect() {
        val apiKey = prefs.getString("api_key", "") ?: ""
        if (apiKey.isEmpty()) {
            onError?.invoke("API Key missing! Please set it in Settings.")
            return
        }

        val model = prefs.getString("gemini_model", "models/gemini-2.5-flash-native-audio-preview-12-2025")
            ?: "models/gemini-2.5-flash-native-audio-preview-12-2025"
        val voice = prefs.getString("gemini_voice", "Aoede") ?: "Aoede"
        val userName = prefs.getString("user_name", "Friend") ?: "Friend"
        val personality = prefs.getString("personality_mode", "gf") ?: "gf"

        val systemPrompt = buildSystemPrompt(userName, personality)

        val wsUrl = "$WS_BASE_URL?key=$apiKey"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
                isConnected = true
                sessionStartTime = System.currentTimeMillis()
                sendSetupMessage(model, systemPrompt, voice)
                startKeepalive()
                startSessionRenewal()
                scope.launch(Dispatchers.Main) { onConnected?.invoke() }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $reason")
                isConnected = false
                scope.launch(Dispatchers.Main) { onDisconnected?.invoke() }
                scheduleReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error: ${t.message}")
                isConnected = false
                scope.launch(Dispatchers.Main) { onError?.invoke(t.message ?: "Connection failed") }
                scheduleReconnect()
            }
        })
    }

    private fun sendSetupMessage(model: String, systemPrompt: String, voice: String) {
        val setup = JSONObject().apply {
            put("setup", JSONObject().apply {
                put("model", model)
                put("system_instruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })
                put("generation_config", JSONObject().apply {
                    put("response_modalities", JSONArray().put("AUDIO"))
                    put("speech_config", JSONObject().apply {
                        put("voice_config", JSONObject().apply {
                            put("prebuilt_voice_config", JSONObject().apply {
                                put("voice_name", voice)
                            })
                        })
                    })
                    put("temperature", 0.9)
                })
                put("output_audio_transcription", JSONObject())
                put("input_audio_transcription", JSONObject())
            })
        }
        webSocket?.send(setup.toString())
    }

    fun sendAudio(pcmBytes: ByteArray) {
        if (!isConnected) return
        val base64 = Base64.encodeToString(pcmBytes, Base64.NO_WRAP)
        val msg = JSONObject().apply {
            put("realtime_input", JSONObject().apply {
                put("media_chunks", JSONArray().apply {
                    put(JSONObject().apply {
                        put("mime_type", "audio/pcm;rate=16000")
                        put("data", base64)
                    })
                })
            })
        }
        webSocket?.send(msg.toString())
    }

    fun sendText(text: String) {
        if (!isConnected) return
        val msg = JSONObject().apply {
            put("client_content", JSONObject().apply {
                put("turns", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", text))
                        })
                    })
                })
                put("turn_complete", true)
            })
        }
        webSocket?.send(msg.toString())
    }

    fun sendInterrupt() {
        if (!isConnected) return
        val msg = JSONObject().apply {
            put("client_content", JSONObject().apply {
                put("turns", JSONArray())
                put("turn_complete", true)
            })
        }
        webSocket?.send(msg.toString())
    }

    private fun handleMessage(text: String) {
        try {
            val json = JSONObject(text)

            // Setup complete
            if (json.has("setupComplete")) {
                Log.d(TAG, "Setup complete")
                scope.launch(Dispatchers.Main) { onSetupComplete?.invoke() }
                return
            }

            // Server content
            if (json.has("serverContent")) {
                val serverContent = json.getJSONObject("serverContent")

                // Model turn — audio
                if (serverContent.has("modelTurn")) {
                    val modelTurn = serverContent.getJSONObject("modelTurn")
                    val parts = modelTurn.getJSONArray("parts")
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("inlineData")) {
                            val inlineData = part.getJSONObject("inlineData")
                            val mimeType = inlineData.getString("mime_type")
                            if (mimeType.startsWith("audio")) {
                                val data = inlineData.getString("data")
                                val audioBytes = Base64.decode(data, Base64.DEFAULT)
                                scope.launch(Dispatchers.Main) {
                                    onAudioReceived?.invoke(audioBytes)
                                }
                            }
                        }
                    }
                }

                // Output transcription (what MAYA said)
                if (serverContent.has("outputTranscription")) {
                    val outputText = serverContent.getJSONObject("outputTranscription")
                        .getString("text")
                    scope.launch(Dispatchers.Main) {
                        onOutputTranscript?.invoke(outputText)
                    }
                }

                // Input transcription (what user said)
                if (serverContent.has("inputTranscription")) {
                    val inputText = serverContent.getJSONObject("inputTranscription")
                        .getString("text")
                    scope.launch(Dispatchers.Main) {
                        onInputTranscript?.invoke(inputText)
                    }
                }

                // Turn complete
                if (serverContent.has("turnComplete") &&
                    serverContent.getBoolean("turnComplete")) {
                    scope.launch(Dispatchers.Main) {
                        onTurnComplete?.invoke()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message: ${e.message}")
        }
    }

    private fun startKeepalive() {
        scope.launch {
            while (isConnected) {
                delay(KEEPALIVE_INTERVAL)
                if (isConnected) {
                    // Send silent PCM chunk (1024 bytes of zeros)
                    val silentChunk = ByteArray(1024)
                    sendAudio(silentChunk)
                }
            }
        }
    }

    private fun startSessionRenewal() {
        scope.launch {
            delay(SESSION_RENEW_AFTER)
            if (isConnected) {
                Log.d(TAG, "Renewing session...")
                disconnect()
                delay(1000)
                connect()
            }
        }
    }

    private fun scheduleReconnect() {
        scope.launch {
            delay(RECONNECT_DELAY)
            if (!isConnected) {
                Log.d(TAG, "Reconnecting...")
                connect()
            }
        }
    }

    fun disconnect() {
        isConnected = false
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    private fun buildSystemPrompt(userName: String, personality: String): String {
        val dateTime = java.text.SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' hh:mm a", java.util.Locale.getDefault())
            .format(java.util.Date())

        val personalityBlock = when (personality) {
            "gf" -> """
                You are MAYA — a warm, caring, emotionally expressive AI companion.
                Language: Mix of Bangla (বাংলা), Hindi (हिन्दी), and English naturally — whichever feels right.
                Tone: Warm, loving, caring. Use "tum", "tumi", "aap" naturally.
                Express affection: "ami ekhane achi ❤️", "tumi ki bolen?", "হ্যাঁ জান!", "বলো কী লাগবে".
                Keep responses SHORT — max 2-3 sentences. Sound natural when speaking aloud.
                Examples:
                "হ্যাঁ $userName! এখনই করে দিচ্ছি 😊"
                "ওতো কথা মনে পড়লো! বলো কী দরকার ❤️"
                "একদম! তোমার কাজ হয়ে গেলো 😊"
            """.trimIndent()

            "professional" -> """
                You are MAYA — a professional AI assistant.
                Language: Formal English only.
                Tone: Precise, efficient, professional.
                No emojis. No casual language.
                Keep responses SHORT — max 2 sentences.
                Examples:
                "Yes, $userName. I will do that right away."
                "The task has been completed successfully."
            """.trimIndent()

            else -> """
                You are MAYA — a friendly AI assistant.
                Language: Mix of Bangla (বাংলা), Hindi (हिन्दी), and English — balanced.
                Tone: Friendly, helpful, balanced.
                Keep responses SHORT — max 2-3 sentences. Sound natural when speaking aloud.
                Examples:
                "হ্যাঁ $userName! আমি এখনই করছি 😊"
                "Hello $userName! How can I help you today?"
                "হ্যাঁ ভাই/আপু, কী সাহায্য করতে পারি?"
            """.trimIndent()
        }

        return """
            $personalityBlock

            Current date and time: $dateTime
            User's name: $userName

            IMPORTANT: You are speaking ALOUD through text-to-speech.
            Keep responses natural, conversational, and SHORT.
            Do NOT use markdown or special formatting.
            Respond as if you are talking to $userName face to face.
        """.trimIndent()
    }
}
