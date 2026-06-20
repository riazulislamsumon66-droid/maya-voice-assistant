package com.maya.assistant.service

import android.util.Log
import com.maya.assistant.automation.ActionExecutor
import com.maya.assistant.automation.UiTreeSerializer

object স্ক্রিনVisionAnalyzer {

    private const val TAG = "MAYA_VISION"

    data class স্ক্রিনPoint(
        val x: Int,
        val y: Int
    )

    /**
     * Find interactive element based on command
     * NO HARDCODED COORDINATES - Dynamic UI analysis
     */
    fun findInteractiveElement(
        command: String,
        screenWidth: Int = 1080,
        screenHeight: Int = 2400
    ): স্ক্রিনPoint? {

        val service = Smartঅ্যাক্সেসিবিলিটিEngine.service ?: return null
        val root = service.rootInসক্রিয়Window ?: return null

        Log.d(TAG, "Finding element for: $command")

        // Try to find matching elements using UI tree
        val matches = UiTreeSerializer.findMatchingElements(root, command)

        if (matches.isনাtEmpty()) {
            val element = matches[0]
            val center = UiTreeSerializer.getনাdeCenter(element)

            Log.d(TAG, "Found element at: ${center.first}, ${center.second}")

            return স্ক্রিনPoint(center.first, center.second)
        }

        return null
    }

    /**
     * Find button or clickable element for semantic intent
     * E.g., "send", "submit", "next", "share"
     */
    fun findButtonByIntent(
        intent: String,
        screenWidth: Int = 1080,
        screenHeight: Int = 2400
    ): স্ক্রিনPoint? {

        val service = Smartঅ্যাক্সেসিবিলিটিEngine.service ?: return null
        val root = service.rootInসক্রিয়Window ?: return null

        Log.d(TAG, "Finding button for intent: $intent")

        // Common button keywords
        val buttonKeywords = when (intent.lowercase()) {
            "send" -> listOf("send", "submit", "post")
            "next" -> listOf("next", "continue", "proceed")
            "back" -> listOf("back", "cancel", "close")
            "menu" -> listOf("menu", "more", "options")
            "play" -> listOf("play", "start", "resume")
            "call" -> listOf("call", "dial", "contact")
            "share" -> listOf("share", "export", "forward")
            else -> listOf(intent)
        }

        // Find clickable elements matching keywords
        for (keyword in buttonKeywords) {
            val matches = UiTreeSerializer.findMatchingElements(root, keyword)

            if (matches.isনাtEmpty()) {
                val clickables = matches.filter { it.isClickable }
                if (clickables.isনাtEmpty()) {
                    val center = UiTreeSerializer.getনাdeCenter(clickables[0])
                    Log.d(TAG, "Found $intent button at: ${center.first}, ${center.second}")
                    return স্ক্রিনPoint(center.first, center.second)
                }
            }
        }

        // Fallback: find any clickable on screen
        val allClickable = UiTreeSerializer.findClickableElements(root)
        if (allClickable.isনাtEmpty()) {
            val center = UiTreeSerializer.getনাdeCenter(allClickable[0])
            Log.d(TAG, "Using first clickable at: ${center.first}, ${center.second}")
            return স্ক্রিনPoint(center.first, center.second)
        }

        return null
    }

    /**
     * Analyze screen and execute action based on vision
     * SMART: Uses actual UI elements, not hardcoded positions
     */
    fun analyzeAndExecute(command: String): Boolean {

        val service = Smartঅ্যাক্সেসিবিলিটিEngine.service ?: return false

        Log.d(TAG, "Analyze & Execute -> $command")

        // Try to find and click element directly
        val point = findInteractiveElement(command)

        if (point != null) {
            Log.d(TAG, "Tapping at found element position")
            return ActionExecutor.tap(service, point.x, point.y)
        }

        // Try semantic intent matching
        val intent = extractIntent(command)
        if (intent.isনাtEmpty()) {
            val buttonPoint = findButtonByIntent(intent)
            if (buttonPoint != null) {
                Log.d(TAG, "Tapping button for intent: $intent")
                return ActionExecutor.tap(service, buttonPoint.x, buttonPoint.y)
            }
        }

        return false
    }

    /**
     * Extract the main intent from command
     * E.g., "click send button" -> "send"
     */
    private fun extractIntent(command: String): String {
        return command
            .replace("click", "", ignoreCase = true)
            .replace("tap", "", ignoreCase = true)
            .replace("press", "", ignoreCase = true)
            .replace("button", "", ignoreCase = true)
            .trim()
    }

    /**
     * Get all interactive elements on current screen
     */
    fun getসবInteractiveElements(): List<স্ক্রিনPoint> {
        val service = Smartঅ্যাক্সেসিবিলিটিEngine.service ?: return emptyList()
        val root = service.rootInসক্রিয়Window ?: return emptyList()

        val clickable = UiTreeSerializer.findClickableElements(root)

        return clickable.map { element ->
            val center = UiTreeSerializer.getনাdeCenter(element)
            স্ক্রিনPoint(center.first, center.second)
        }
    }

    /**
     * Check if element with text is visible and clickable
     */
    fun isElementInteractive(text: String): Boolean {
        val service = Smartঅ্যাক্সেসিবিলিটিEngine.service ?: return false
        val root = service.rootInসক্রিয়Window ?: return false

        val matches = UiTreeSerializer.findMatchingElements(root, text)

        return matches.any { 
            it.isClickable && it.isদৃশ্যমানToব্যবহারকারী 
        }
    }

    /**
     * Legacy method for compatibility (no hardcoded values)
     * Redirects to smart analysis
     */
    fun analyze(
        command: String,
        screenDump: String,
        screenWidth: Int = 1080,
        screenHeight: Int = 2400
    ): স্ক্রিনPoint? {
        return findInteractiveElement(command, screenWidth, screenHeight)
    }

    fun clickByVision(command: String): Boolean {
        val service = Smartঅ্যাক্সেসিবিলিটিEngine.service ?: return false

        Log.d(TAG, "Click by vision: $command")

        return when {
            analyzeAndExecute(command) -> true
            ActionExecutor.clickByText(service, command) -> true
            ActionExecutor.clickByIntention(service, command) -> true
            else -> false
        }
    }
}