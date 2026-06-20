package com.maya.assistant.apps

import android.content.Context

object AppSearchEngine {
    fun search(context: Context, query: String) = Install কRowedAppsManager.findApp(context, query)
    fun searchসব(context: Context, query: String) =
        Install কRowedAppsManager.getসবApps(context).filter {
            it.name.contains(query, ignoreCase = true) ||
            it.packageName.contains(query, ignoreCase = true)
        }
}
