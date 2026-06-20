package com.maya.assistant.automation

import android.accessibilityservice.অ্যাক্সেসিবিলিটিService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য

object ActionExecutor {

    private const val TAG = "MAYA_ACTION"

    /**
     * Click element by text (smart matching)
     */
    fun clickByText(
        service: অ্যাক্সেসিবিলিটিService,
        text: String
    ): Boolean {

        val root = service.rootInসক্রিয়Window ?: return false

        Log.d(TAG, "খুঁজোing for text: $text")

        // Try exact match first
        val nodes = root.findঅ্যাক্সেসিবিলিটিনাdeতথ্যsByText(text)
        if (nodes.isনাtEmpty()) {
            return performClick(nodes[0])
        }

        // Try case-insensitive partial match
        val matches = UiTreeSerializer.findMatchingElements(root, text)
        if (matches.isনাtEmpty()) {
            return performClick(matches[0])
        }

        Log.d(TAG, "না matching element found for: $text")
        return false
    }

    /**
     * Click by content description
     */
    fun clickByDescription(
        service: অ্যাক্সেসিবিলিটিService,
        description: String
    ): Boolean {

        val root = service.rootInসক্রিয়Window ?: return false

        Log.d(TAG, "খুঁজোing by description: $description")

        val nodes = root.findঅ্যাক্সেসিবিলিটিনাdeতথ্যsByText(description)
        if (nodes.isনাtEmpty()) {
            return performClick(nodes[0])
        }

        return false
    }

    /**
     * Click by view ID (requires resource name)
     */
    fun clickById(
        service: অ্যাক্সেসিবিলিটিService,
        resourceId: String
    ): Boolean {

        val root = service.rootInসক্রিয়Window ?: return false

        Log.d(TAG, "খুঁজোing by ID: $resourceId")

        val nodes = root.findঅ্যাক্সেসিবিলিটিনাdeতথ্যsByViewId(resourceId)
        if (nodes.isনাtEmpty()) {
            return performClick(nodes[0])
        }

        return false
    }

    /**
     * Click element based on semantic meaning
     * (e.g., "click the send button")
     */
    fun clickByIntention(
        service: অ্যাক্সেসিবিলিটিService,
        intention: String
    ): Boolean {

        val root = service.rootInসক্রিয়Window ?: return false
        val query = intention.lowercase()

        Log.d(TAG, "Finding element for intention: $intention")

        // Analyze intention to find relevant keywords
        val keywords = extractKeywords(intention)

        for (keyword in keywords) {
            val matches = UiTreeSerializer.findMatchingElements(root, keyword)
            if (matches.isনাtEmpty()) {
                // Prefer clickable elements
                val clickable = matches.find { it.isClickable }
                if (clickable != null) {
                    return performClick(clickable)
                }
                return performClick(matches[0])
            }
        }

        return false
    }

    /**
     * Perform click on a node (handles parent search for clickable)
     */
    fun performClick(
        node: অ্যাক্সেসিবিলিটিনাdeতথ্য?
    ): Boolean {

        var current = node

        while (current != null) {
            if (current.isClickable && current.isচালু) {
                Log.d(TAG, "Clicking on: ${current.text}")

                return current.performAction(
                    অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_CLICK
                )
            }
            current = current.parent
        }

        Log.d(TAG, "না clickable parent found")
        return false
    }

    /**
     * Tap at specific coordinates
     */
    fun tap(
        service: অ্যাক্সেসিবিলিটিService,
        x: Int,
        y: Int
    ): Boolean {

        Log.d(TAG, "Tapping at: $x, $y")

        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0,
                    50  // 50ms duration
                )
            )
            .build()

        return service.dispatchGesture(
            gesture,
            null,
            null
        )
    }

    /**
     * Swipe gesture
     */
    fun swipe(
        service: অ্যাক্সেসিবিলিটিService,
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        duration: Long = 300
    ): Boolean {

        Log.d(TAG, "Swiping from ($startX,$startY) to ($endX,$endY)")

        val path = Path()
        path.moveTo(startX.toFloat(), startY.toFloat())
        path.lineTo(endX.toFloat(), endY.toFloat())

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0,
                    duration
                )
            )
            .build()

        return service.dispatchGesture(
            gesture,
            null,
            null
        )
    }

    /**
     * Long press gesture
     */
    fun longPress(
        service: অ্যাক্সেসিবিলিটিService,
        x: Int,
        y: Int,
        duration: Long = 500
    ): Boolean {

        Log.d(TAG, "Long pressing at: $x, $y")

        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0,
                    duration
                )
            )
            .build()

        return service.dispatchGesture(
            gesture,
            null,
            null
        )
    }

    /**
     * Extract keywords from intention string
     */
    private fun extractKeywords(intention: String): List<String> {
        val words = intention.lowercase()
            .split(" ")
            .filter { it.length > 2 }  // এড়িয়ে যাও short words

        return words + intention.lowercase()  // Include full query too
    }

    /**
     * Get first clickable element on screen
     */
    fun getFirstClickable(
        service: অ্যাক্সেসিবিলিটিService
    ): অ্যাক্সেসিবিলিটিনাdeতথ্য? {

        val root = service.rootInসক্রিয়Window ?: return null
        val clickable = UiTreeSerializer.findClickableElements(root)

        return if (clickable.isনাtEmpty()) clickable[0] else null
    }
}