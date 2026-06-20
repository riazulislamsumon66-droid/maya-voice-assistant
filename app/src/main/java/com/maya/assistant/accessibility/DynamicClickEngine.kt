package com.maya.assistant.accessibility

import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য
import com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine

object DynamicClickEngine {
    fun clickByText(text: String): Boolean {
        val root = Smartঅ্যাক্সেসিবিলিটিEngine.service?.rootInসক্রিয়Window ?: return false
        val node = নাdeFinder.findByText(root, text) ?: return false
        return click(node)
    }
    private fun click(node: অ্যাক্সেসিবিলিটিনাdeতথ্য): Boolean {
        if (node.isClickable) return node.performAction(অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_CLICK)
        var p = node.parent; var d = 0
        while (p != null && d++ < 5) {
            if (p.isClickable) return p.performAction(অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_CLICK)
            p = p.parent
        }
        return false
    }
}
