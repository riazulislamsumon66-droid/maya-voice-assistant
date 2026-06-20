package com.maya.assistant.accessibility

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.maya.assistant.models.স্ক্রিনNodeModel

object NodeReader {
    fun readসব(root: AccessibilityNodeInfo?): List<স্ক্রিনNodeModel> {
        val nodes = mutableListOf<স্ক্রিনNodeModel>()
        if (root == null) return nodes
        traverse(root, nodes)
        return nodes
    }

    private fun traverse(node: AccessibilityNodeInfo, list: MutableList<স্ক্রিনNodeModel>) {
        list.add(স্ক্রিনNodeModel(
            text = node.text?.toString(),
            contentDesc = node.contentDescription?.toString(),
            className = node.className?.toString(),
            isClickable = node.isClickable,
            isEdit কRowable = node.isEdit কRowable,
            bounds = Rect().also { node.getBoundsInস্ক্রিন(it) },
            viewId = node.viewIdResourceName
        ))
        for (i in 0 until node.childCount) { node.getChild(i)?.let { traverse(it, list) } }
    }

    fun dumpText(root: AccessibilityNodeInfo?): String =
        readসব(root).mapনাtNull { it.text?.ifBlank { null } ?: it.contentDesc?.ifBlank { null } }
            .joinToString(" | ")
}
