package com.maya.assistant.automation

import com.maya.assistant.models.ActionModel
import com.maya.assistant.utils.Logger
import kotlinx.coroutines.delay

object কাজflowManager {
    private val TAG = "WORKFLOW"

    suspend fun runSequence(actions: List<ActionModel>, delayMs: Long = 500) {
        অটোmationStateManager.setRunning()
        for (action in actions) {
            if (!অটোmationStateManager.isRunning()) break
            val success = টাস্কExecutor.execute(action)
            Logger.d(TAG, "Action ${action.type}: $success")
            delay(delayMs)
        }
        অটোmationStateManager.setIdle()
    }
}
