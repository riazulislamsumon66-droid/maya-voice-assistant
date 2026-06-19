package com.maya.assistant.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class AccessibilityHelperService : AccessibilityService() {

    companion object {
        var instance: AccessibilityHelperService? = null

        fun isEnabled(): Boolean {
            // Check if accessibility service is enabled
            return instance != null
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used for now
    }

    override fun onInterrupt() {
        // Not used
    }

    fun closeCurrentApp() {
        if (!isEnabled()) {
            showAccessibilityError()
            return
        }
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun goBack() {
        if (!isEnabled()) {
            showAccessibilityError()
            return
        }
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun clickOnText(text: String) {
        if (!isEnabled()) {
            showAccessibilityError()
            return
        }
        val rootNode = rootInActiveWindow ?: return
        val nodes = rootNode.findAccessibilityNodeInfosByText(text)
        for (node in nodes) {
            if (node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return
            }
            // Try parent
            val parent = node.parent
            if (parent != null && parent.isClickable) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return
            }
        }
    }

    fun typeText(text: String) {
        if (!isEnabled()) {
            showAccessibilityError()
            return
        }
        val rootNode = rootInActiveWindow ?: return
        val nodes = rootNode.findAccessibilityNodeInfosByText("")
        for (node in nodes) {
            if (node.className?.contains("EditText") == true) {
                val arguments = android.os.Bundle()
                arguments.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
                )
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                return
            }
        }
    }

    fun scrollDown() {
        if (!isEnabled()) {
            showAccessibilityError()
            return
        }
        performGlobalAction(ACTION_SCROLL_FORWARD)
    }

    fun scrollUp() {
        if (!isEnabled()) {
            showAccessibilityError()
            return
        }
        performGlobalAction(ACTION_SCROLL_BACKWARD)
    }

    private fun showAccessibilityError() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Ignore
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
