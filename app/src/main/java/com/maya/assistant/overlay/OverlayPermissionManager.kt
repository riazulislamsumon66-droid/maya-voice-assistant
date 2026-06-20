package com.maya.assistant.overlay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.সেটিংস

object ওভারলেPermissionManager {

    fun hasPermission(context: Context): Boolean =
        সেটিংস.canDrawওভারলেs(context)

    fun requestPermission(context: Context) {
        val intent = Intent(
            সেটিংস.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageনাম}")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
