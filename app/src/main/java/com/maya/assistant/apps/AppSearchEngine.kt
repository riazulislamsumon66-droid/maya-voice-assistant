package com.maya.assistant.apps

import android.content.Context

object AppখুঁজোEngine {
    fun search(context: Context, query: String) = ইনস্টল করোedAppsManager.findApp(context, query)
    fun searchসব(context: Context, query: String) =
        ইনস্টল করোedAppsManager.getসবApps(context).filter {
            it.name.contains(query, ignoreCase = true) ||
            it.packageনাম.contains(query, ignoreCase = true)
        }
}
