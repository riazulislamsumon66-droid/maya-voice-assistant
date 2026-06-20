package com.maya.assistant.overlay

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Alias service — actual floating bubble is handled by MayaওভারলেService.
 * This exists for the project structure as specified in the prompt.
 */
class FloatingBubbleService : Service() {
    override fun onশুরু করোCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Floatingঅ্যাসিস্ট্যান্টController.show(this)
        stopSelf()
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
