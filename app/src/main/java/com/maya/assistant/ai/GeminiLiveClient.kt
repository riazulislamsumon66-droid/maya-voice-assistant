package com.maya.assistant.ai

import android.util.Base64
import android.util.Log
import okhttp3.*
import okio.ByteString
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.সময়Unit

class GeminiLiveClient(
    private val apiKey: String,
    private val systemPrompt: String,
    private val callback: LiveListener
) {
    private val TAG = "MAYA_LIVE"
    private var isSetupComplete = false
    private var webSocket: WebSocket? = null

    private val client = OkHttpClient.Builder()
        .readটাইমআউট(0, সময়Unit.MILLISECONDS)
        .connectটাইমআউট(20, সময়Unit.SECONDS)
        .pingInterval(20, সময়Unit.SECONDS) // Connection ko zinda rakhne ke liye
        .build()

    // Aapka original model name
    private val URL = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent?key=$apiKey"

    interface LiveListener {
        fun onAudioReceived(data: ByteArray)
        fun onTextReceived(text: String)
        fun onসংযুক্ত ✅()
        fun onTurnComplete()
        fun onসমস্যা(msg: String)
    }

    fun start() {
        isSetupComplete = false
        val request = Request.Builder()
            .url(URL)
            .addHeader("Origin", "https://generativelanguage.googleapis.com")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onখোলো(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "সংযুক্ত ✅ ✅")
                sendSetup()
            }

            override fun onমেসেজ(webSocket: WebSocket, text: String) {
                // Saara data JSON mein aata hai, use handle karein
                handleResponse(text)
            }

            override fun onমেসেজ(webSocket: WebSocket, bytes: ByteString) {
                // Gemini Live API hamesha JSON Text bhejta hai.
                // Binary frame ki handle karne ki zaroorat nahi hai, par safety ke liye:
                try {
                    handleResponse(bytes.utf8())
                } catch (e: Exception) {
                    Log.e(TAG, "Binary সমস্যা: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isSetupComplete = false
                callback.onসমস্যা("Connection ব্যর্থ: ${t.message}")
            }

            override fun onবন্ধ করোd(webSocket: WebSocket, code: Int, reason: String) {
                isSetupComplete = false
            }
        })
    }

    private fun sendSetup() {
        try {
            val setupJson = JSONObject().apply {
                put("setup", JSONObject().apply {
                    put("model", "models/gemini-2.5-flash-native-audio-preview-12-2025")
                    put("generationConfig", JSONObject().apply {
                        put("responseModalities", JSONArray().put("AUDIO"))
                        put("speechConfig", JSONObject().apply {
                            put("voiceConfig", JSONObject().apply {
                                put("prebuiltভয়েসConfig", JSONObject().apply {
                                    put("voiceনাম", "Aoede")
                                })
                            })
                        })
                    })
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                    })
                })
            }
            webSocket?.send(setupJson.toString())
            Log.d(TAG, "Setup Sent ✅")
        } catch (e: Exception) {
            Log.e(TAG, "Setup সমস্যা: ${e.message}")
        }
    }

    fun sendTextমেসেজ(text: String) {
        if (!isSetupComplete) return
        try {
            val msg = JSONObject().apply {
                put("clientContent", JSONObject().apply {
                    put("turns", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().apply {
                            put("text", text)
                        }))
                    }))
                    put("turnComplete", true)
                })
            }
            webSocket?.send(msg.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Send সমস্যা: ${e.message}")
        }
    }

    private fun handleResponse(jsonString: String) {
        try {
            Log.d(TAG, "Raw response: ${jsonString.take(200)}...")
            val json = JSONObject(jsonString)

            // Setup confirm karne ka sahi tarika
            if (json.has("setupComplete")) {
                isSetupComplete = true
                Log.d(TAG, "GEMINI READY ✅")
                callback.onসংযুক্ত ✅()
                return
            }

            // Handle setupComplete inside setupComplete object (sometimes nested)
            if (json.optJSONObject("setupComplete") != null) {
                isSetupComplete = true
                Log.d(TAG, "GEMINI READY (nested) ✅")
                callback.onসংযুক্ত ✅()
                return
            }

            // Handle serverContent
            val serverContent = json.optJSONObject("serverContent")
            if (serverContent == null) {
                Log.d(TAG, "না serverContent in response")
                return
            }

            // AI Response handle karein
            val modelTurn = serverContent.optJSONObject("modelTurn")
            if (modelTurn != null) {
                val parts = modelTurn.optJSONArray("parts")
                if (parts == null) {
                    Log.d(TAG, "না parts in modelTurn")
                    return
                }

                Log.d(TAG, "Processing ${parts.length()} parts")

                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    Log.d(TAG, "Part $i: ${part.keys().asSequence().toList()}")

                    // Audio Data (Awaaz)
                    if (part.has("inlineData")) {
                        val inlineData = part.getJSONObject("inlineData")
                        val mimeType = inlineData.optString("mimeType", "")
                        val base64Data = inlineData.getString("data")

                        Log.d(TAG, "Audio received: mimeType=$mimeType, size=${base64Data.length}")

                        val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
                        Log.d(TAG, "Decoded audio bytes: ${audioBytes.size}")

                        callback.onAudioReceived(audioBytes)
                    }

                    // Text Data (Subtitles)
                    if (part.has("text")) {
                        val text = part.getString("text")
                        Log.d(TAG, "Text received: $text")
                        callback.onTextReceived(text)
                    }
                }
            }

            // Handle turnComplete
            if (serverContent.optBoolean("turnComplete", false)) {
                Log.d(TAG, "Turn complete")
                callback.onTurnComplete()
            }

            // Handle interruption
            if (serverContent.has("interrupted")) {
                Log.d(TAG, "Response interrupted")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Parse সমস্যা: ${e.message}")
            Log.e(TAG, "JSON: ${jsonString.take(500)}")
            e.printStackTrace()
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "বাই")
        isSetupComplete = false
    }
}