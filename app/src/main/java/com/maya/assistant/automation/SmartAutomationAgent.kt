package com.maya.assistant.automation

import android.accessibilityservice.অ্যাক্সেসিবিলিটিService
import android.util.Log

object SmartঅটোmationAgent {

    private const val TAG = "MAYA_AGENT"

    fun run(
        service: অ্যাক্সেসিবিলিটিService,
        command: String
    ): Boolean {

        Log.d(TAG, "Running smart command: $command")

        return when {
            // Try direct UI tree analysis first
            analyzeAndClick(service, command) -> true

            // Try semantic intention matching
            ActionExecutor.clickByIntention(service, command) -> true

            // Fallback: try removing common words and clicking
            clickWithCleanedCommand(service, command) -> true

            else -> {
                Log.d(TAG, "Could not execute: $command")
                false
            }
        }
    }

    /**
     * Analyze UI tree and find relevant clickable elements
     */
    private fun analyzeAndClick(
        service: অ্যাক্সেসিবিলিটিService,
        command: String
    ): Boolean {

        val root = service.rootInসক্রিয়Window ?: return false

        // Get relevant elements based on command
        val relevant = UiTreeSerializer.findMatchingElements(
            root,
            command
        )

        if (relevant.isEmpty()) {
            Log.d(TAG, "না matching elements found for: $command")
            return false
        }

        Log.d(TAG, "Found ${relevant.size} matching elements")

        // Try to click the first matching clickable element
        for (element in relevant) {
            if (ActionExecutor.performClick(element)) {
                Log.d(TAG, "Clicked element: ${element.text}")
                return true
            }
        }

        return false
    }

    /**
     * Try clicking with cleaned command (remove common words)
     */
    private fun clickWithCleanedCommand(
        service: অ্যাক্সেসিবিলিটিService,
        command: String
    ): Boolean {

        val target = command
            .replace("click", "", ignoreCase = true)
            .replace("open", "", ignoreCase = true)
            .replace("tap", "", ignoreCase = true)
            .replace("press", "", ignoreCase = true)
            .trim()

        if (target.isনাtEmpty()) {
            return ActionExecutor.clickByText(service, target)
        }

        return false
    }
}
