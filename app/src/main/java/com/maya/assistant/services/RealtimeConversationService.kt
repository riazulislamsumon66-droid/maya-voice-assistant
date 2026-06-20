package com.maya.assistant.services

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

/**
 * Convenience helper to manage the Foregroundভয়েসService lifecycle.
 */
object RealtimeConversationService {

    fun start(context: Context) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, Foregroundভয়েসService::class.java)
        )
    }

    fun stop(context: Context) {
        context.stopService(Intent(context, Foregroundভয়েসService::class.java))
    }

    fun isRunning() = Foregroundভয়েসService.isRunning

    fun sendমেসেজ(text: String) {
        Foregroundভয়েসService.instance?.sendTextToGemini(text)
    }

    fun reconnect() {
        Foregroundভয়েসService.instance?.reconnectGemini()
    }
}
