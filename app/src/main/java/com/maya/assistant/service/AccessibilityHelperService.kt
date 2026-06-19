package com.maya.assistant.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import java.io.ByteArrayOutputStream

class AccessibilityHelperService : AccessibilityService() {

    companion object {
        private const val TAG = "AccessibilityHelper"
        var instance: AccessibilityHelperService? = null

        fun isEnabled(): Boolean {
            return instance != null
        }
    }

    // Store latest screen content
    private var lastScreenContent = ""
    private var lastScreenUpdateTime = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        serviceInfo = serviceInfo.apply {
            AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
            AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
            AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
            AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        }
        Log.d(TAG, "Accessibility Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // Update screen content on window state changes
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                if (System.currentTimeMillis() - lastScreenUpdateTime > 1000) {
                    lastScreenUpdateTime = System.currentTimeMillis()
                    // Don't rebuild on every event — too expensive
                }
            }
        }
    }

    override fun onInterrupt() {
        // Not used
    }

    /**
     * Get all text content from the current screen
     * This is the key method for real-time screen reading
     */
    fun getScreenContent(): String {
        val rootNode = rootInActiveWindow ?: return lastScreenContent
        val sb = StringBuilder()
        try {
            collectTextFromNode(rootNode, sb, 0, 3) // max depth 3
            lastScreenContent = sb.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting screen content: ${e.message}")
        }
        return lastScreenContent
    }

    /**
     * Get structured screen info — what app, what text, what buttons
     */
    fun getScreenInfo(): ScreenInfo {
        val rootNode = rootInActiveWindow ?: return ScreenInfo("", "", emptyList())
        val sb = StringBuilder()
        val clickableItems = mutableListOf<ClickableItem>()
        try {
            collectStructuredInfo(rootNode, sb, clickableItems, 0, 3)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting screen info: ${e.message}")
        }

        val packageName = rootNode.packageName?.toString() ?: ""
        val appName = getAppNameFromPackage(packageName)

        return ScreenInfo(
            appName = appName,
            packageName = packageName,
            fullText = sb.toString(),
            clickableItems = clickableItems
        )
    }

    private fun collectTextFromNode(node: AccessibilityNodeInfo, sb: StringBuilder, depth: Int, maxDepth: Int) {
        if (depth > maxDepth) return

        // Get text from this node
        node.text?.let {
            if (it.isNotBlank()) {
                sb.append(it.trim()).append(" | ")
            }
        }

        // Get content description
        node.contentDescription?.let {
            if (it.isNotBlank() && it != node.text) {
                sb.append(it.trim()).append(" | ")
            }
        }

        // Recurse children
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                collectTextFromNode(child, sb, depth + 1, maxDepth)
                child.recycle()
            }
        }
    }

    private fun collectStructuredInfo(
        node: AccessibilityNodeInfo,
        sb: StringBuilder,
        clickableItems: MutableList<ClickableItem>,
        depth: Int,
        maxDepth: Int
    ) {
        if (depth > maxDepth) return

        val text = node.text?.toString()?.trim() ?: ""
        val contentDesc = node.contentDescription?.toString()?.trim() ?: ""
        val className = node.className?.toString() ?: ""
        val bounds = Rect()
        node.getBoundsInScreen(bounds)

        if (text.isNotBlank()) {
            sb.append(text).append(" | ")
            if (node.isClickable) {
                clickableItems.add(
                    ClickableItem(
                        text = text,
                        className = className,
                        bounds = bounds,
                        nodeInfo = null // We store index instead of node ref
                    )
                )
            }
        }

        if (contentDesc.isNotBlank() && contentDesc != text) {
            sb.append(contentDesc).append(" | ")
            if (node.isClickable) {
                clickableItems.add(
                    ClickableItem(
                        text = contentDesc,
                        className = className,
                        bounds = bounds,
                        nodeInfo = null
                    )
                )
            }
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                collectStructuredInfo(child, sb, clickableItems, depth + 1, maxDepth)
                child.recycle()
            }
        }
    }

    private fun getAppNameFromPackage(packageName: String): String {
        val appMap = mapOf(
            "com.facebook.katana" to "Facebook",
            "com.facebook.orca" to "Messenger",
            "com.whatsapp" to "WhatsApp",
            "com.instagram.android" to "Instagram",
            "com.google.android.youtube" to "YouTube",
            "com.spotify.music" to "Spotify",
            "org.telegram.messenger" to "Telegram",
            "com.twitter.android" to "Twitter/X",
            "com.snapchat.android" to "Snapchat",
            "com.android.chrome" to "Chrome",
            "com.google.android.apps.maps" to "Google Maps",
            "com.google.android.gm" to "Gmail",
            "com.netflix.mediaclient" to "Netflix",
            "com.discord" to "Discord",
            "com.zhiliaoapp.musically" to "TikTok",
            "com.android.settings" to "Settings",
            "com.android.dialer" to "Phone",
            "com.android.contacts" to "Contacts",
            "com.android.mms" to "Messages",
            "com.android.camera" to "Camera",
            "com.google.android.apps.photos" to "Gallery",
            "com.google.android.calendar" to "Calendar",
            "com.google.android.keep" to "Keep Notes",
            "com.flipkart.android" to "Flipkart",
            "in.amazon.mShop.android.shopping" to "Amazon",
            "net.one97.paytm" to "Paytm",
            "com.phonepe.app" to "PhonePe"
        )
        return appMap[packageName] ?: packageName
    }

    // ===== ACTION METHODS =====

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

    fun goHome() {
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun openRecents() {
        performGlobalAction(GLOBAL_ACTION_RECENTS)
    }

    /**
     * Find and click a node by text
     */
    fun clickOnText(text: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val nodes = rootNode.findAccessibilityNodeInfosByText(text)
        for (node in nodes) {
            if (node.isClickable) {
                val result = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (result) return true
            }
            // Try parent
            var parent = node.parent
            while (parent != null) {
                if (parent.isClickable) {
                    val result = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    if (result) return true
                }
                val nextParent = parent.parent
                if (parent != node.parent) parent.recycle()
                parent = nextParent
            }
        }
        return false
    }

    /**
     * Type text into the focused input field
     */
    fun typeText(text: String) {
        if (!isEnabled()) {
            showAccessibilityError()
            return
        }
        val rootNode = rootInActiveWindow ?: return
        typeTextInNode(rootNode, text)
    }

    private fun typeTextInNode(node: AccessibilityNodeInfo, text: String) {
        val className = node.className?.toString() ?: ""
        if (className.contains("EditText")) {
            // Focus first
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            // Set text
            val arguments = android.os.Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            return
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                typeTextInNode(child, text)
                child.recycle()
            }
        }
    }

    /**
     * Find EditText and type into it (smart — finds the most relevant one)
     */
    fun typeTextSmart(text: String, hint: String = ""): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val editTexts = mutableListOf<AccessibilityNodeInfo>()
        collectEditTexts(rootNode, editTexts)

        if (editTexts.isEmpty()) return false

        // If hint specified, try to match
        if (hint.isNotBlank()) {
            for (et in editTexts) {
                val etHint = et.text?.toString() ?: ""
                val etDesc = et.contentDescription?.toString() ?: ""
                if (etHint.contains(hint, ignoreCase = true) || etDesc.contains(hint, ignoreCase = true)) {
                    return typeIntoNode(et, text)
                }
            }
        }

        // Type into the first visible EditText
        for (et in editTexts) {
            val bounds = Rect()
            et.getBoundsInScreen(bounds)
            if (bounds.width() > 0 && bounds.height() > 0) {
                return typeIntoNode(et, text)
            }
        }

        return false
    }

    private fun collectEditTexts(node: AccessibilityNodeInfo, list: MutableList<AccessibilityNodeInfo>) {
        val className = node.className?.toString() ?: ""
        if (className.contains("EditText")) {
            list.add(node)
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { collectEditTexts(it, list) }
        }
    }

    private fun typeIntoNode(node: AccessibilityNodeInfo, text: String): Boolean {
        node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
        val arguments = android.os.Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )
        return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
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

    /**
     * Click at specific coordinates using gesture
     */
    fun clickAt(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = android.graphics.Path().apply {
                moveTo(x.toFloat(), y.toFloat())
            }
            val gesture = android.accessibilityservice.GestureDescription.Builder()
                .addStroke(android.accessibilityservice.GestureDescription.StrokeDescription(
                    path, 0, 100
                ))
                .build()
            dispatchGesture(gesture, null, null)
        }
    }

    /**
     * Long press at coordinates
     */
    fun longPressAt(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = android.graphics.Path().apply {
                moveTo(x.toFloat(), y.toFloat())
            }
            val gesture = android.accessibilityservice.GestureDescription.Builder()
                .addStroke(android.accessibilityservice.GestureDescription.StrokeDescription(
                    path, 0, 1500
                ))
                .build()
            dispatchGesture(gesture, null, null)
        }
    }

    /**
     * Get all clickable elements from screen
     */
    fun getClickableElements(): List<String> {
        val rootNode = rootInActiveWindow ?: return emptyList()
        val items = mutableListOf<String>()
        collectClickableTexts(rootNode, items)
        return items
    }

    private fun collectClickableTexts(node: AccessibilityNodeInfo, list: MutableList<String>) {
        if (node.isClickable) {
            val text = node.text?.toString()?.trim() ?: ""
            val desc = node.contentDescription?.toString()?.trim() ?: ""
            if (text.isNotBlank()) list.add(text)
            else if (desc.isNotBlank()) list.add(desc)
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { collectClickableTexts(it, list) }
        }
    }

    /**
     * Wait for a specific text to appear on screen, then click it
     */
    fun waitAndClick(text: String, timeoutMs: Long = 5000): Boolean {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (clickOnText(text)) return true
            Thread.sleep(500)
        }
        return false
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

/**
 * Data class for structured screen information
 */
data class ScreenInfo(
    val appName: String,
    val packageName: String,
    val fullText: String,
    val clickableItems: List<ClickableItem>
)

data class ClickableItem(
    val text: String,
    val className: String,
    val bounds: Rect,
    val nodeInfo: AccessibilityNodeInfo?
)
