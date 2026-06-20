package com.maya.assistant.overlay

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.maya.assistant.service.MayaওভারলেService
import com.maya.assistant.utils.Constants

object FloatingAsিস্ট্যান্টController {

    fun show(context: Context) {
        if (!ওভারলেPermissionManager.hasPermission(context)) {
            ওভারলেPermissionManager.requestPermission(context)
            return
        }
        val i = Intent(context, MayaওভারলেService::class.java).apply {
            action = Constants.ACTION_SHOW_OVERLAY
        }
        ContextCompat.startForegroundService(context, i)
    }

    fun hide(context: Context) {
        context.startService(Intent(context, MayaওভারলেService::class.java).apply {
            action = Constants.ACTION_HIDE_OVERLAY
        })
    }

    fun toggle(context: Context) {
        val i = Intent(context, MayaওভারলেService::class.java).apply {
            action = Constants.ACTION_TOGGLE_OVERLAY
        }
        ContextCompat.startForegroundService(context, i)
    }

    fun isRunning() = MayaওভারলেService.isRunning
}
