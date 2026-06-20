package com.maya.assistant.websocket

import com.maya.assistant.utils.Logger

/**
 * Manages WebSocket connection lifecycle with auto-reconnect.
 */
class ConnectionManager(
    private val apiKey: String,
    private val systemPrompt: String,
    private val onসংযুক্ত ✅: () -> Unit,
    private val onAudioReceived: (ByteArray) -> Unit,
    private val onTextReceived: (String) -> Unit,
    private val onTurnComplete: () -> Unit,
    private val onসমস্যা: (String) -> Unit
) {
    private val TAG = "CONN_MGR"
    private var client: GeminiWebSocketClient? = null
    private var reconnectAttempts = 0
    private val MAX_RECONNECT = 5

    fun connect() {
        reconnectAttempts = 0
        createClient()
    }

    private fun createClient() {
        client = GeminiWebSocketClient(
            apiKey, systemPrompt,
            onসংযুক্ত ✅ = {
                reconnectAttempts = 0
                onসংযুক্ত ✅()
            },
            onAudioReceived = onAudioReceived,
            onTextReceived = onTextReceived,
            onTurnComplete = onTurnComplete,
            onসমস্যা = { msg ->
                Logger.e(TAG, "সমস্যা: $msg | attempts=$reconnectAttempts")
                onসমস্যা(msg)
                if (reconnectAttempts < MAX_RECONNECT) {
                    reconnectAttempts++
                    Thread.sleep(3000)
                    createClient()
                }
            }
        )
        client?.connect()
    }

    fun getClient() = client

    fun disconnect() {
        client?.disconnect()
        client = null
    }

    fun isসংযুক্ত ✅() = client?.isসংযুক্ত ✅() == true
}
