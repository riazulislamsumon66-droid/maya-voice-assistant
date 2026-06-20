package com.maya.assistant.screenvision

import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য
import com.maya.assistant.accessibility.নাdeReader
import com.maya.assistant.models.স্ক্রিননাdeModel
import com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine

object স্ক্রিনহাইerarchyParser {

    fun getCurrentনাdes(): List<স্ক্রিননাdeModel> {
        val root = Smartঅ্যাক্সেসিবিলিটিEngine.service?.rootInসক্রিয়Window ?: return emptyList()
        return নাdeReader.readসব(root)
    }

    fun getCurrentText(): String {
        val root = Smartঅ্যাক্সেসিবিলিটিEngine.service?.rootInসক্রিয়Window ?: return ""
        return নাdeReader.dumpText(root)
    }

    fun getCurrentPackage(): String {
        return Smartঅ্যাক্সেসিবিলিটিEngine.service?.rootInসক্রিয়Window?.packageনাম?.toString() ?: ""
    }

    fun summarizeস্ক্রিন(): String {
        val nodes = getCurrentনাdes()
        val texts = nodes.mapনাtNull { it.text?.ifBlank { null } ?: it.contentDesc?.ifBlank { null } }
        return "Package: ${getCurrentPackage()} | UI: ${texts.take(10).joinToString(", ")}"
    }
}
