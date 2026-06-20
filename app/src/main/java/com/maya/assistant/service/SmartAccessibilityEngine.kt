package com.maya.assistant.service

import android.accessibilityservice.অ্যাক্সেসিবিলিটিService
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য
import com.maya.assistant.accessibility.নাdeFinder
import com.maya.assistant.automation.AppDetector
import com.maya.assistant.automation.SmartঅটোmationAgent
import com.maya.assistant.automation.ActionExecutor
import com.maya.assistant.service.স্ক্রিনVisionAnalyzer

object Smartঅ্যাক্সেসিবিলিটিEngine {

    private const val TAG = "MAYA_SMART"

    var service: অ্যাক্সেসিবিলিটিService? = null

    data class Result(
        val success: Boolean,
        val message: String
    )

    fun execute(rawCommand: String): Result {

        val cmd = cleanCommand(rawCommand)

        Log.d(TAG, "EXECUTE -> $cmd")

        val success = when {

            cmd.startsWith("OPEN_APP", true) ->
                handleখোলোApp(cmd)

            cmd.startsWith("PLAY_MUSIC", true) ->
                handlePlayগান(cmd)

            cmd.startsWith("CLICK", true) ->
                genericClick(
                    cmd.removePrefix("CLICK").trim()
                )

            cmd.startsWith("SEARCH", true) ->
                genericখুঁজো(
                    cmd.removePrefix("SEARCH").trim()
                )

            cmd.startsWith("VOLUME_UP", true) ->
                volumeUp()

            cmd.startsWith("VOLUME_DOWN", true) ->
                volumeDown()

            else ->
                runSmartঅটোmation(cmd)
        }

        return Result(
            success,
            if (success) "হয়ে গেছে" else "ব্যর্থ"
        )
    }

    private fun cleanCommand(text: String): String {
        return text
            .replace("`", "")
            .trim()
            .lines()
            .firstOrNull()
            ?: ""
    }

    // ============================
    // SMART AI AUTOMATION
    // ============================

    private fun runSmartঅটোmation(
        command: String
    ): Boolean {

        val svc = service ?: return false

        Log.d(TAG, "SMART MODE -> $command")

        return SmartঅটোmationAgent.run(
            svc,
            command
        )
    }

    // ============================
    // DYNAMIC APP OPEN
    // ============================

    private fun handleখোলোApp(
        command: String
    ): Boolean {

        val appনাম = command
            .removePrefix("OPEN_APP")
            .removePrefix(":")
            .trim()

        val intent =
            findLaunchIntent(appনাম)
                ?: return false

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        service?.startActivity(intent)

        Log.d(TAG, "OPENED -> $appনাম")
        return true
    }

    private fun handlePlayগান(
        command: String
    ): Boolean {

        val musicKeywords = listOf(
            "music",
            "spotify",
            "gaana",
            "saavn",
            "youtube music",
            "amazon music"
        )

        val svc = service ?: return false
        val app = AppDetector.findAppByKeywords(svc, musicKeywords)
            ?: return false

        return launchPackage(app.packageনাম, "PLAY_MUSIC")
    }

    private fun findLaunchIntent(
        appনাম: String
    ): Intent? {

        val svc = service ?: return null
        val app = AppDetector.findAppByনাম(svc, appনাম) ?: return null
        return svc.packageManager.getLaunchIntentForPackage(app.packageনাম)
    }

    private fun launchPackage(
        packageনাম: String,
        tag: String
    ): Boolean {
        val svc = service ?: return false
        val intent = svc.packageManager.getLaunchIntentForPackage(packageনাম)
            ?: return false

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        svc.startActivity(intent)
        Log.d(TAG, "খোলোed $tag -> $packageনাম")
        return true
    }

    // ============================
    // GENERIC CLICK
    // ============================

    fun click(
        text: String? = null,
        contentDesc: String? = null,
        id: String? = null
    ): Boolean {

        val root =
            service?.rootInসক্রিয়Window
                ?: return false

        val nodes = when {
            id != null ->
                root.findঅ্যাক্সেসিবিলিটিনাdeতথ্যsByViewId(id)

            text != null ->
                root.findঅ্যাক্সেসিবিলিটিনাdeতথ্যsByText(text)

            contentDesc != null ->
                root.findঅ্যাক্সেসিবিলিটিনাdeতথ্যsByText(contentDesc)

            else -> return false
        }

        if (nodes.isEmpty()) return false

        for (node in nodes) {

            var current:
                    অ্যাক্সেসিবিলিটিনাdeতথ্য? = node

            while (current != null) {

                if (current.isClickable) {

                    return current.performAction(
                        অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_CLICK
                    )
                }

                current = current.parent
            }
        }

        return false
    }

    private fun genericClick(
        text: String
    ): Boolean {
        return click(text = text)
    }

    // ============================
    // GENERIC SEARCH
    // ============================

    private fun genericখুঁজো(
        query: String
    ): Boolean {

        val svc = service ?: return false
        val root = svc.rootInসক্রিয়Window ?: return false

        val searchClicked = ActionExecutor.clickByIntention(svc, "search")
        if (!searchClicked) {
            নাdeFinder.findClickable(root, "search")?.let { node ->
                var current: অ্যাক্সেসিবিলিটিনাdeতথ্য? = node
                while (current != null) {
                    if (current.isClickable) {
                        current.performAction(অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_CLICK)
                        break
                    }
                    current = current.parent
                }
            }
        }

        val updatedRoot = svc.rootInসক্রিয়Window ?: root
        val editable = findএডিট করোableনাde(updatedRoot) ?: findএডিট করোableনাde(root) ?: return false

        val args = android.os.Bundle().apply {
            putCharSequence(
                অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                query
            )
        }

        return editable.performAction(
            অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_SET_TEXT,
            args
        )
    }

    private fun findএডিট করোableনাde(
        node: অ্যাক্সেসিবিলিটিনাdeতথ্য
    ): অ্যাক্সেসিবিলিটিনাdeতথ্য? {

        if (node.isএডিট করোable)
            return node

        for (i in 0 until node.childগণনা) {

            val child = node.getChild(i)

            if (child != null) {

                val result =
                    findএডিট করোableনাde(child)

                if (result != null)
                    return result
            }
        }

        return null
    }

    // ============================
    // VOLUME
    // ============================

    private fun volumeUp(): Boolean {

        val audio =
            service?.getসিস্টেমService(
                AudioManager::class.java
            ) ?: return false

        audio.adjustভলিউম(
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )

        return true
    }

    private fun volumeDown(): Boolean {

        val audio =
            service?.getসিস্টেমService(
                AudioManager::class.java
            ) ?: return false

        audio.adjustভলিউম(
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
        )

        return true
    }
}