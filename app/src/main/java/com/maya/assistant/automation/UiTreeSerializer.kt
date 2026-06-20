package com.maya.assistant.automation

import android.graphics.Rect
import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

object UiTreeSerializer {

    private const val TAG = "MAYA_UI_TREE"

    private fun normalize(text: String): String {
        return text
            .lowercase()
            .replace("_", " ")
            .replace("-", " ")
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun serialize(root: অ্যাক্সেসিবিলিটিনাdeতথ্য?): String {
        if (root == null) return "[]"

        val array = JSONArray()
        traverse(root, array)

        return array.toString()
    }

    private fun traverse(
        node: অ্যাক্সেসিবিলিটিনাdeতথ্য,
        array: JSONArray
    ) {

        val rect = Rect()
        node.getBoundsInস্ক্রিন(rect)

        val text = node.text?.toString() ?: ""
        val desc = node.contentDescription?.toString() ?: ""

        val obj = JSONObject().apply {
            put("text", text)
            put("desc", desc)
            put("class", node.classনাম?.toString() ?: "")
            put("clickable", node.isClickable)
            put("enabled", node.isচালু)
            put("visible", node.isদৃশ্যমানToব্যবহারকারী)
            put(
                "bounds",
                "${rect.left},${rect.top},${rect.right},${rect.bottom}"
            )
        }

        array.put(obj)

        for (i in 0 until node.childগণনা) {
            node.getChild(i)?.let {
                traverse(it, array)
            }
        }
    }

    /**
     * Find elements matching a query in the UI tree
     */
    fun findMatchingElements(
        root: অ্যাক্সেসিবিলিটিনাdeতথ্য?,
        query: String
    ): List<অ্যাক্সেসিবিলিটিনাdeতথ্য> {

        if (root == null) return emptyList()

        val results = mutableListOf<অ্যাক্সেসিবিলিটিনাdeতথ্য>()
        val queryLower = normalize(query)

        findMatchingElementsRecursive(root, queryLower, results)

        return results
    }

    private fun findMatchingElementsRecursive(
        node: অ্যাক্সেসিবিলিটিনাdeতথ্য,
        query: String,
        results: MutableList<অ্যাক্সেসিবিলিটিনাdeতথ্য>
    ) {

        val text = normalize(node.text?.toString() ?: "")
        val desc = normalize(node.contentDescription?.toString() ?: "")

        // Match if text or description contains query
        if ((text.contains(query) || desc.contains(query)) &&
            (node.isClickable || text.isনাtEmpty())) {
            results.add(node)
        }

        // Recurse through children
        for (i in 0 until node.childগণনা) {
            node.getChild(i)?.let {
                findMatchingElementsRecursive(it, query, results)
            }
        }
    }

    /**
     * Get all clickable elements
     */
    fun findClickableElements(
        root: অ্যাক্সেসিবিলিটিনাdeতথ্য?
    ): List<অ্যাক্সেসিবিলিটিনাdeতথ্য> {

        if (root == null) return emptyList()

        val results = mutableListOf<অ্যাক্সেসিবিলিটিনাdeতথ্য>()
        findClickableElementsRecursive(root, results)

        return results
    }

    private fun findClickableElementsRecursive(
        node: অ্যাক্সেসিবিলিটিনাdeতথ্য,
        results: MutableList<অ্যাক্সেসিবিলিটিনাdeতথ্য>
    ) {

        if (node.isClickable && node.isদৃশ্যমানToব্যবহারকারী) {
            results.add(node)
        }

        for (i in 0 until node.childগণনা) {
            node.getChild(i)?.let {
                findClickableElementsRecursive(it, results)
            }
        }
    }

    /**
     * Get node center coordinates
     */
    fun getনাdeCenter(node: অ্যাক্সেসিবিলিটিনাdeতথ্য): Pair<Int, Int> {
        val rect = Rect()
        node.getBoundsInস্ক্রিন(rect)

        return Pair(
            (rect.left + rect.right) / 2,
            (rect.top + rect.bottom) / 2
        )
    }
}