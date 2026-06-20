package com.maya.assistant.accessibility

import android.view.accessibility.অ্যাক্সেসিবিলিটিনাdeতথ্য

object নাdeFinder {
    fun findByText(root: অ্যাক্সেসিবিলিটিনাdeতথ্য?, text: String): অ্যাক্সেসিবিলিটিনাdeতথ্য? {
        if (root == null) return null
        if (root.text?.toString()?.contains(text, true) == true) return root
        if (root.contentDescription?.toString()?.contains(text, true) == true) return root
        for (i in 0 until root.childগণনা) { findByText(root.getChild(i), text)?.let { return it } }
        return null
    }
    fun findএডিট করোText(root: অ্যাক্সেসিবিলিটিনাdeতথ্য?): অ্যাক্সেসিবিলিটিনাdeতথ্য? {
        if (root == null) return null
        if (root.classনাম?.contains("এডিট করোText") == true) return root
        for (i in 0 until root.childগণনা) { findএডিট করোText(root.getChild(i))?.let { return it } }
        return null
    }
    fun findClickable(root: অ্যাক্সেসিবিলিটিনাdeতথ্য?, hint: String): অ্যাক্সেসিবিলিটিনাdeতথ্য? {
        if (root == null) return null
        val m = root.text?.toString()?.contains(hint, true) == true ||
                root.contentDescription?.toString()?.contains(hint, true) == true
        if (root.isClickable && m) return root
        for (i in 0 until root.childগণনা) { findClickable(root.getChild(i), hint)?.let { return it } }
        return null
    }
}
