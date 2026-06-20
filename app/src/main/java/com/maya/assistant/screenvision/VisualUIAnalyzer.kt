package com.maya.assistant.screenvision

import android.graphics.Bitmap
import com.maya.assistant.models.স্ক্রিননাdeModel

object VisualUIAnalyzer {

    fun analyze(bitmap: Bitmap?, nodes: List<স্ক্রিননাdeModel>): String {
        val nodesSummary = nodes
            .filter { (it.text != null || it.contentDesc != null) }
            .take(20)
            .joinToString(", ") { it.text ?: it.contentDesc ?: "" }
        return "স্ক্রিন context: $nodesSummary"
    }

    fun findBestActionTarget(nodes: List<স্ক্রিননাdeModel>, intent: String): স্ক্রিননাdeModel? {
        val lower = intent.lowercase()
        return nodes.find { node ->
            val t = (node.text ?: node.contentDesc ?: "").lowercase()
            t.isনাtBlank() && (t.contains(lower) || lower.contains(t))
        }
    }
}
