package com.maya.assistant.screenvision

import android.view.accessibility.AccessibilityNodeInfo
import com.maya.assistant.accessibility.NodeReader
import com.maya.assistant.models.স্ক্রিনNodeModel
import com.maya.assistant.service.SmartAccessibilityEngine

object স্ক্রিনহাইerarchyParser {

    fun getCurrentNodes(): List<স্ক্রিনNodeModel> {
        val root = SmartAccessibilityEngine.service?.rootInসক্রিয়Window ?: return emptyList()
        return NodeReader.readসব(root)
    }

    fun getCurrentText(): String {
        val root = SmartAccessibilityEngine.service?.rootInসক্রিয়Window ?: return ""
        return NodeReader.dumpText(root)
    }

    fun getCurrentPackage(): String {
        return SmartAccessibilityEngine.service?.rootInসক্রিয়Window?.packageName?.toString() ?: ""
    }

    fun summarizeস্ক্রিন(): String {
        val nodes = getCurrentNodes()
        val texts = nodes.mapনাtNull { it.text?.ifBlank { null } ?: it.contentDesc?.ifBlank { null } }
        return "Package: ${getCurrentPackage()} | UI: ${texts.take(10).joinToString(", ")}"
    }
}
