package com.maya.assistant.accessibility

import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য
import com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine

object ScrollController {
    fun scrollDown() = scroll(অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_SCROLL_FORWARD)
    fun scrollUp() = scroll(অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_SCROLL_BACKWARD)
    private fun scroll(action: Int): Boolean {
        val root = Smartঅ্যাক্সেসিবিলিটিEngine.service?.rootInসক্রিয়Window ?: return false
        return doScroll(root, action)
    }
    private fun doScroll(node: অ্যাক্সেসিবিলিটিনাdeতথ্য, action: Int): Boolean {
        if (node.isScrollable) return node.performAction(action)
        for (i in 0 until node.childগণনা) {
            val c = node.getChild(i) ?: continue
            if (doScroll(c, action)) return true
        }
        return false
    }
}
