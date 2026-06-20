package com.maya.assistant.accessibility

import android.os.Bundle
import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য
import com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine

object TypingController {
    fun typeText(text: String): Boolean {
        val root = Smartঅ্যাক্সেসিবিলিটিEngine.service?.rootInসক্রিয়Window ?: return false
        val et = নাdeFinder.findএডিট করোText(root) ?: return false
        et.performAction(অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_FOCUS)
        val args = Bundle().apply {
            putCharSequence(অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        return et.performAction(অ্যাক্সেসিবিলিটিনাdeতথ্য.ACTION_SET_TEXT, args)
    }
}
