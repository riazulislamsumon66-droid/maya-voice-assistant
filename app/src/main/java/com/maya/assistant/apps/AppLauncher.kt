package com.maya.assistant.apps

import android.content.Context
import android.content.Intent
import com.maya.assistant.utils.Logger

object AppLauncher {
    private val TAG = "LAUNCHER"

    fun launch(context: Context, appনাম: String): Boolean {
        val intent = ইনস্টল করোedAppsManager.getLaunchIntent(context, appনাম) ?: run {
            Logger.w(TAG, "App not found: $appনাম")
            return false
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
            Logger.d(TAG, "Launched: $appনাম")
            return true
        } catch (e: Exception) {
            Logger.e(TAG, "Launch failed: ${e.message}")
            return false
        }
    }
}
