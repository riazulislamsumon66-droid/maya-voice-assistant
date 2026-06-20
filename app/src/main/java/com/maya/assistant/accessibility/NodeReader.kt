package com.maya.assistant.accessibility

import android.graphics.Rect
import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য
import com.maya.assistant.models.স্ক্রিননাdeModel

object নাdeReader {
    fun readসব(root: অ্যাক্সেসিবিলিটিনাdeতথ্য?): List<স্ক্রিননাdeModel> {
        val nodes = mutableListOf<স্ক্রিননাdeModel>()
        if (root == null) return nodes
        traverse(root, nodes)
        return nodes
    }

    private fun traverse(node: অ্যাক্সেসিবিলিটিনাdeতথ্য, list: MutableList<স্ক্রিননাdeModel>) {
        list.add(স্ক্রিননাdeModel(
            text = node.text?.toString(),
            contentDesc = node.contentDescription?.toString(),
            classনাম = node.classনাম?.toString(),
            isClickable = node.isClickable,
            isএডিট করোable = node.isএডিট করোable,
            bounds = Rect().also { node.getBoundsInস্ক্রিন(it) },
            viewId = node.viewIdResourceনাম
        ))
        for (i in 0 until node.childগণনা) { node.getChild(i)?.let { traverse(it, list) } }
    }

    fun dumpText(root: অ্যাক্সেসিবিলিটিনাdeতথ্য?): String =
        readসব(root).mapনাtNull { it.text?.ifBlank { null } ?: it.contentDesc?.ifBlank { null } }
            .joinToString(" | ")
}
