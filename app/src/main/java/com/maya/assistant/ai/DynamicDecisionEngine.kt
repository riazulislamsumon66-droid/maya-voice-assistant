package com.maya.assistant.ai

import android.content.Context
import com.maya.assistant.apps.AppLauncher
import com.maya.assistant.models.CommandType
import com.maya.assistant.models.VoiceCommand
import com.maya.assistant.service.SmartAccessibilityEngine
import com.maya.assistant.utils.Logger

object DynamicDecisionEngine {
    private val TAG = "DECISION"

    suspend fun execute(context: Context, command: VoiceCommand): String {
        Logger.d(TAG, "Executing: ${command.type} - ${command.raw}")

        return when (command.type) {
            CommandType.OPEN_APP -> {
                val app = command.args["app"] ?: ""
                if (AppLauncher.launch(context, app)) "$app খুলছি"
                else "$app পাওয়া যায়নি"
            }

            CommandType.VOLUME_UP -> {
                SmartAccessibilityEngine.execute("VOLUME_UP")
                "Volume বাড়িয়ে দিলাম"
            }

            CommandType.VOLUME_DOWN -> {
                SmartAccessibilityEngine.execute("VOLUME_DOWN")
                "Volume কমিয়ে দিলাম"
            }

            CommandType.FLASHLIGHT_ON -> {
                toggleFlashlight(context, true)
                "টর্চ চালু করে দিলাম"
            }

            CommandType.FLASHLIGHT_OFF -> {
                toggleFlashlight(context, false)
                "টর্চ বন্ধ করে দিলাম"
            }

            CommandType.WHATSAPP_CALL -> {
                SmartAccessibilityEngine.execute("WHATSAPP_CALL ${command.args["name"] ?: ""}")
                "WhatsApp call করছি"
            }

            CommandType.WHATSAPP_MSG -> {
                val name = command.args["name"] ?: ""
                val msg = command.args["message"] ?: ""
                SmartAccessibilityEngine.execute("WHATSAPP_MSG $name $msg")
                "মেসেজ পাঠাচ্ছি"
            }

            CommandType.YOUTUBE_PLAY -> {
                val query = command.args["query"] ?: ""
                SmartAccessibilityEngine.execute("YOUTUBE_PLAY $query")
                "YouTube এ চালাচ্ছি"
            }

            CommandType.CALL -> {
                SmartAccessibilityEngine.execute("CALL ${command.args["name"] ?: ""}")
                "কল করছি"
            }

            else -> ""
        }
    }

    private fun toggleFlashlight(context: Context, on: Boolean) {
        try {
            val cm = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
            val cameraId = cm.cameraIdList.firstOrNull() ?: return
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                cm.setTorchMode(cameraId, on)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Flashlight error: ${e.message}")
        }
    }
}
