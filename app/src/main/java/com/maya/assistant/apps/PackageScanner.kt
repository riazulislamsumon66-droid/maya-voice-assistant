package com.maya.assistant.apps

import android.content.Context
import com.maya.assistant.models.AppModel

object PackageScanner {
    fun scanসব(context: Context): List<AppModel> = Install কRowedAppsManager.getসবApps(context)
    fun refresh(context: Context): List<AppModel> {
        Install কRowedAppsManager.invalidateCache()
        return Install কRowedAppsManager.getসবApps(context)
    }
}
