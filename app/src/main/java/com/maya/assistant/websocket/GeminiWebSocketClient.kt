package com.maya.assistant.websocket

import android.util.Base64
import android.util.Log
import com.maya.assistant.utils.Constants
import okhttp3.*
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.সময়Unit

class GeminiWebSocketClient(
    private val apiKey: String,
    private val systemPrompt: String,
    private val onসংযুক্ত ✅: () -> Unit,
    private val onAudioReceived: (ByteArray) -> Unit,
    private val onTextReceived: (String) -> Unit,
    private val onTurnComplete: () -> Unit,
    private val onসমস্যা: (String) -> Unit
) {
    private val TAG = "GEMINI_WS"
    private var webSocket: WebSocket? = null
    private var isSetupComplete = false

    private val client = OkHttpClient.Builder()
        .readটাইমআউট(0, সময়Unit.MILLISECONDS)
        .connectটাইমআউট(20, সময়Unit.SECONDS)
        .pingInterval(25, সময়Unit.SECONDS)
        .build()

    private val url = "${Constants.GEMINI_WS_BASE}?key=$apiKey"

    fun connect() {
        isSetupComplete = false
        val request = Request.Builder()
            .url(url)
            .addHeader("Origin", "https://generativelanguage.googleapis.com")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onখোলো(ws: WebSocket, response: Response) {
                Log.d(TAG, "সংযুক্ত ✅ ✅")
                sendSetup(ws)
            }

            override fun onমেসেজ(ws: WebSocket, text: String) {
                handleResponse(text)
            }

            override fun onমেসেজ(ws: WebSocket, bytes: ByteString) {
                try { handleResponse(bytes.utf8()) } catch (e: Exception) { Log.e(TAG, "Binary parse error") }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                isSetupComplete = false
                Log.e(TAG, "WS Failure: ${t.message}")
                onসমস্যা("Connection failed: ${t.message}")
            }

            override fun onবন্ধ করোd(ws: WebSocket, code: Int, reason: String) {
                isSetupComplete = false
                Log.d(TAG, "WS বন্ধ করোd: $reason")
            }
        })
    }

    private fun sendSetup(ws: WebSocket) {
        val setup = JSONObject().apply {
            put("setup", JSONObject().apply {
                put("model", Constants.GEMINI_MODEL)
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().put("AUDIO"))
                    put("speechConfig", JSONObject().apply {
                        put("voiceConfig", JSONObject().apply {
                            put("prebuiltভয়েসConfig", JSONObject().apply {
                                put("voiceনাম", Constants.GEMINI_VOICE)
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
            })
        }
        ws.send(setup.toString())
        Log.d(TAG, "Setup sent ✅")
    }

    fun sendAudioChunk(pcm: ByteArray) {
        if (!isSetupComplete) return
        try {
            val b64 = Base64.encodeToString(pcm, Base64.NO_WRAP)
            val msg = JSONObject().apply {
                put("realtimeInput", JSONObject().apply {
                    put("mediaChunks", JSONArray().put(JSONObject().apply {
                        put("mimeType", "audio/pcm;rate=16000")
                        put("data", b64)
                    }))
                })
            }
            webSocket?.send(msg.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Audio send error: ${e.message}")
        }
    }

    fun sendTextমেসেজ(text: String) {
        if (!isSetupComplete) return
        try {
            val msg = JSONObject().apply {
                put("clientContent", JSONObject().apply {
                    put("turns", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put("text", text)))
                    }))
                    put("turnComplete", true)
                })
            }
            webSocket?.send(msg.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Text send error: ${e.message}")
        }
    }

    private fun handleResponse(json: String) {
        try {
            val obj = JSONObject(json)

            // Setup complete
            if (obj.has("setupComplete") || obj.optJSONObject("setupComplete") != null) {
                isSetupComplete = true
                Log.d(TAG, "Gemini Ready ✅")
                onসংযুক্ত ✅()
                return
            }

            val serverContent = obj.optJSONObject("serverContent") ?: return

            val modelTurn = serverContent.optJSONObject("modelTurn")
            if (modelTurn != null) {
                val parts = modelTurn.optJSONArray("parts") ?: return
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    // Audio
                    if (part.has("inlineData")) {
                        val data = part.getJSONObject("inlineData").getString("data")
                        val bytes = Base64.decode(data, Base64.DEFAULT)
                        onAudioReceived(bytes)
                    }
                    // Text
                    if (part.has("text")) {
                        onTextReceived(part.getString("text"))
                    }
                }
            }

            if (serverContent.optBoolean("turnComplete", false)) {
                onTurnComplete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message} | ${json.take(200)}")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "বাই")
        webSocket = null
        isSetupComplete = false
    }

    fun isসংযুক্ত ✅() = isSetupComplete
}
