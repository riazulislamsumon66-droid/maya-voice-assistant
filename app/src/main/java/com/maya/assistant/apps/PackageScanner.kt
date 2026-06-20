package com.maya.assistant.apps

import android.content.Context
import com.maya.assistant.models.AppModel

object PackageScanner {
    fun scanসব(context: Context): List<AppModel> = ইনস্টল করোedAppsManager.getসবApps(context)
    fun refresh(context: Context): List<AppModel> {
        ইনস্টল করোedAppsManager.invalidateCache()
        return ইনস্টল করোedAppsManager.getসবApps(context)
    }
}
