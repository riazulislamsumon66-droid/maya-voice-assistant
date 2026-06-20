package com.maya.assistant.ai

import android.content.Context
import com.maya.assistant.apps.AppLauncher
import com.maya.assistant.models.CommandType
import com.maya.assistant.models.ভয়েসCommand
import com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine
import com.maya.assistant.utils.Logger

object DynamicDecisionEngine {
    private val TAG = "DECISION"

    suspend fun execute(context: Context, command: ভয়েসCommand): String {
        Logger.d(TAG, "Executing: ${command.type} - ${command.raw}")

        return when (command.type) {
            CommandType.OPEN_APP -> {
                val app = command.args["app"] ?: ""
                if (AppLauncher.launch(context, app)) "খোলোing $app"
                else "App not found: $app"
            }

            CommandType.VOLUME_UP -> {
                Smartঅ্যাক্সেসিবিলিটিEngine.execute("VOLUME_UP")
                "ভলিউম badha diya"
            }

            CommandType.VOLUME_DOWN -> {
                Smartঅ্যাক্সেসিবিলিটিEngine.execute("VOLUME_DOWN")
                "ভলিউম kam kiya"
            }

            CommandType.FLASHLIGHT_ON -> {
                toggleFlashlight(context, true)
                "Torch চালু করে দিলাম"
            }

            CommandType.FLASHLIGHT_OFF -> {
                toggleFlashlight(context, false)
                "Torch বন্ধ করে দিলাম"
            }

            CommandType.WHATSAPP_CALL -> {
                Smartঅ্যাক্সেসিবিলিটিEngine.execute("WHATSAPP_CALL ${command.args["name"] ?: ""}")
                "WhatsApp call kar rahi hoon"
            }

            CommandType.WHATSAPP_MSG -> {
                val name = command.args["name"] ?: ""
                val msg = command.args["message"] ?: ""
                Smartঅ্যাক্সেসিবিলিটিEngine.execute("WHATSAPP_MSG $name $msg")
                "মেসেজ bhej rahi hoon"
            }

            CommandType.YOUTUBE_PLAY -> {
                val query = command.args["query"] ?: ""
                Smartঅ্যাক্সেসিবিলিটিEngine.execute("YOUTUBE_PLAY $query")
                "YouTube play kar rahi hoon"
            }

            CommandType.CALL -> {
                Smartঅ্যাক্সেসিবিলিটিEngine.execute("CALL ${command.args["name"] ?: ""}")
                "কল kar rahi hoon"
            }

            else -> ""
        }
    }

    private fun toggleFlashlight(context: Context, on: Boolean) {
        try {
            val cm = context.getসিস্টেমService(Context.CAMERA_SERVICE) as android.hardware.camera2.ক্যামেরাManager
            val cameraId = cm.cameraIdList.firstOrNull() ?: return
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                cm.setTorchMode(cameraId, on)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Flashlight error: ${e.message}")
        }
    }
}
