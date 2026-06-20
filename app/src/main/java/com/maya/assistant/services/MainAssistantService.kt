package com.maya.assistant.services

import android.content.Context

/**
 * Facade for the primary assistant service layer.
 * Routes to Foregroundভয়েসService + Smartঅ্যাক্সেসিবিলিটিEngine.
 */
object Mainঅ্যাসিস্ট্যান্টService {

    fun initialize(context: Context) {
        RealtimeConversationService.start(context)
    }

    fun shutdown(context: Context) {
        RealtimeConversationService.stop(context)
    }

    fun speak(text: String) {
        RealtimeConversationService.sendমেসেজ(text)
    }

    fun isসক্রিয়() = RealtimeConversationService.isRunning()
}
