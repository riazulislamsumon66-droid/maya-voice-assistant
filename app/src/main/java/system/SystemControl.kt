package com.maya.assistant.system

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast

class সিস্টেমController(private val context: Context) {

    fun openApp(spokenনাম: String) {
        val pm = context.packageManager
        val apps = pm.getইনস্টল করোedApplications(PackageManager.GET_META_DATA)

        val cleanedInput = spokenনাম
            .lowercase()
            .replace("open", "")
            .replace("khol", "")
            .replace("launch", "")
            .trim()

        for (app in apps) {
            val appLabel = pm.getApplicationLabel(app)
                .toString()
                .lowercase()

            if (
                appLabel == cleanedInput ||
                appLabel.contains(cleanedInput) ||
                cleanedInput.contains(appLabel)
            ) {

                val intent = pm.getLaunchIntentForPackage(app.packageনাম)

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return
                }
            }
        }

        Toast.makeText(
            context,
            "$spokenনাম not installed",
            Toast.LENGTH_SHORT
        ).show()
    }
}