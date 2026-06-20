package com.maya.assistant.accessibility

import android.view.accessibility.অ্যাক্সেসিবিলিটিইভেন্ট

object অ্যাক্সেসিবিলিটিইভেন্টManager {
    private var lastPackage = ""
    private var lastইভেন্টType = -1
    fun onইভেন্ট(e: অ্যাক্সেসিবিলিটিইভেন্ট) { lastইভেন্টType = e.eventType; e.packageনাম?.let { lastPackage = it.toString() } }
    fun getCurrentPackage() = lastPackage
    fun getLastইভেন্টType() = lastইভেন্টType
}
