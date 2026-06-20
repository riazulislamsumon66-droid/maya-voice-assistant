package com.maya.assistant.automation

import android.accessibilityservice.অ্যাক্সেসিবিলিটিService
import android.util.Log
import android.view.accessibility.অ্যাক্সেসিবিলিটিইভেন্ট
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object স্ক্রিনMonitor {

    private const val TAG = "MAYA_SCREEN_MONITOR"

    private var lastUiTree: String = ""
    private var lastPackage: String = ""
    private var monitoringScope: CoroutineScope? = null

    data class স্ক্রিনState(
        val uiTree: String,
        val packageনাম: String,
        val timestamp: Long,
        val elements: List<UiElement>
    )

    data class UiElement(
        val text: String,
        val contentDesc: String,
        val classনাম: String,
        val isClickable: Boolean,
        val bounds: String,
        val isদৃশ্যমান: Boolean
    )

    /**
     * শুরু করো continuous screen monitoring
     */
    fun startMonitoring(
        service: অ্যাক্সেসিবিলিটিService,
        scope: CoroutineScope
    ) {
        monitoringScope = scope

        Log.d(TAG, "স্ক্রিন monitoring started")
    }

    /**
     * Capture current screen state
     */
    fun captureস্ক্রিনState(
        service: অ্যাক্সেসিবিলিটিService
    ): স্ক্রিনState? {

        return try {
            val root = service.rootInসক্রিয়Window 
                ?: return null

            val packageনাম = root.packageনাম?.toString() 
                ?: "unknown"

            val uiTree = UiTreeSerializer.serialize(root)
            val elements = extractElements(root)

            স্ক্রিনState(
                uiTree = uiTree,
                packageনাম = packageনাম,
                timestamp = সিস্টেম.currentসময়Millis(),
                elements = elements
            )

        } catch (e: Exception) {
            Log.e(TAG, "সমস্যা capturing screen: ${e.message}")
            null
        }
    }

    /**
     * Extract all clickable and text elements from the screen
     */
    private fun extractElements(
        node: android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য,
        elements: MutableList<UiElement> = mutableListOf()
    ): List<UiElement> {

        try {
            val rect = android.graphics.Rect()
            node.getBoundsInস্ক্রিন(rect)

            // Extract element only if it has text or content description
            val text = node.text?.toString() ?: ""
            val desc = node.contentDescription?.toString() ?: ""

            if (text.isনাtEmpty() || desc.isনাtEmpty() || node.isClickable) {
                elements.add(
                    UiElement(
                        text = text,
                        contentDesc = desc,
                        classনাম = node.classনাম?.toString() ?: "",
                        isClickable = node.isClickable,
                        bounds = "${rect.left},${rect.top}," +
                                "${rect.right},${rect.bottom}",
                        isদৃশ্যমান = node.isদৃশ্যমানToব্যবহারকারী
                    )
                )
            }

            // Recurse through children
            for (i in 0 until node.childগণনা) {
                node.getChild(i)?.let {
                    extractElements(it, elements)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "সমস্যা extracting elements: ${e.message}")
        }

        return elements
    }

    /**
     * Find interactive elements related to a command
     */
    fun findRelevantElements(
        service: অ্যাক্সেসিবিলিটিService,
        command: String
    ): List<UiElement> {

        val state = captureস্ক্রিনState(service) 
            ?: return emptyList()

        val query = command.lowercase()

        return state.elements.filter { element ->
            val text = element.text.lowercase()
            val desc = element.contentDesc.lowercase()

            (element.isClickable || text.isনাtEmpty()) &&
            (text.contains(query) || 
             desc.contains(query) ||
             query.contains(text) ||
             query.contains(desc))
        }
    }

    /**
     * Get all clickable elements on screen
     */
    fun getClickableElements(
        service: অ্যাক্সেসিবিলিটিService
    ): List<UiElement> {

        val state = captureস্ক্রিনState(service) 
            ?: return emptyList()

        return state.elements.filter { it.isClickable }
    }

    /**
     * আপডেট UI tree after event (called from accessibility service)
     */
    fun onঅ্যাক্সেসিবিলিটিইভেন্ট(
        event: অ্যাক্সেসিবিলিটিইভেন্ট,
        service: অ্যাক্সেসিবিলিটিService
    ) {
        when (event.eventType) {
            অ্যাক্সেসিবিলিটিইভেন্ট.TYPE_WINDOW_STATE_CHANGED,
            অ্যাক্সেসিবিলিটিইভেন্ট.TYPE_WINDOW_CONTENT_CHANGED -> {
                monitoringScope?.launch(Dispatchers.ডিফল্ট) {
                    val state = captureস্ক্রিনState(service)
                    if (state != null) {
                        lastUiTree = state.uiTree
                        lastPackage = state.packageনাম

                        Log.d(TAG, "স্ক্রিন updated: ${state.packageনাম}")
                    }
                }
            }
        }
    }

    /**
     * Get last captured screen state
     */
    fun getLastস্ক্রিনState(): Pair<String, String> {
        return Pair(lastUiTree, lastPackage)
    }

    fun stopMonitoring() {
        Log.d(TAG, "স্ক্রিন monitoring stopped")
    }
}
