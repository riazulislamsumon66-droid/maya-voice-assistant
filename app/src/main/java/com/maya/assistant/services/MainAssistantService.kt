package com.maya.assistant.services

import android.content.Context

/**
 * Facade for the primary assistant service layer.
 * Routes to ForegroundVoiceService + SmartAccessibilityEngine.
 */
object MainAsিস্ট্যান্টService {

    fun initialize(context: Context) {
        RealtimeConversationService.start(context)
    }

    fun shutdown(context: Context) {
        RealtimeConversationService.stop(context)
    }

    fun speak(text: String) {
        RealtimeConversationService.sendMessage(text)
    }

    fun isসক্রিয়() = RealtimeConversationService.isRunning()
}
