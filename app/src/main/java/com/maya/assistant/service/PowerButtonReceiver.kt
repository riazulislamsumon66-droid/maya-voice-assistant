package com.maya.assistant.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PowerButtonReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "PowerButtonReceiver"
        private var lastPressTime = 0L
        private const val DOUBLE_PRESS_INTERVAL = 600L // ms
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_SCREEN_OFF || action == Intent.ACTION_SCREEN_ON) {
            val now = System.currentTimeMillis()
            if (now - lastPressTime < DOUBLE_PRESS_INTERVAL) {
                // Double press detected — show overlay
                Log.d(TAG, "Double power press detected!")
                lastPressTime = 0L
                try {
                    val overlayIntent = Intent(context, MayaOverlayService::class.java)
                    context.startService(overlayIntent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting overlay: ${e.message}")
                }
            } else {
                lastPressTime = now
            }
        }
    }
}
