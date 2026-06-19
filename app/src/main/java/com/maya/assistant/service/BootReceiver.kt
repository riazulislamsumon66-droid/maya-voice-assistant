package com.maya.assistant.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed — starting CallMonitorService")
            try {
                val serviceIntent = Intent(context, CallMonitorService::class.java)
                context.startService(serviceIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting CallMonitorService on boot: ${e.message}")
            }
        }
    }
}
