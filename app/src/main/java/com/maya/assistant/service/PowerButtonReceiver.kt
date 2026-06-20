package com.maya.assistant.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.maya.assistant.ui.main.MainActivity

class PowerButtonReceiver : BroadcastReceiver() {

    companion object {
        private var pressগণনা = 0
        private var lastPressসময় = 0L
        private const val DOUBLE_PRESS_WINDOW = 700L
        private const val TAG = "MAYA_POWER"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF,
            Intent.ACTION_SCREEN_ON -> detectDoublePress(context)
        }
    }

    private fun detectDoublePress(context: Context) {
        val now = সিস্টেম.currentসময়Millis()

        if (now - lastPressসময় <= DOUBLE_PRESS_WINDOW) {
            pressগণনা++

            if (pressগণনা >= 2) {
                pressগণনা = 0
                launchMaya(context)
            }

        } else {
            pressগণনা = 1
        }

        lastPressসময় = now
    }

    private fun launchMaya(context: Context) {
        Log.d(TAG, "Double power press detected")

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "ব্যর্থ to start activity: ${e.message}")
        }
    }
}