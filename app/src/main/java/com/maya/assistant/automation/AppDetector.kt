package com.maya.assistant.automation

import android.accessibilityservice.অ্যাক্সেসিবিলিটিService
import android.content.Intent
import android.content.pm.Applicationতথ্য
import android.content.pm.PackageManager
import android.util.Log

object AppDetector {

    private const val TAG = "MAYA_APP_DETECTOR"

    data class ইনস্টল করোedApp(
        val packageনাম: String,
        val label: String,
        val isসিস্টেমApp: Boolean
    )

    private fun normalizeনাম(value: String): String {
        return value
            .lowercase()
            .replace("_", " ")
            .replace("-", " ")
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun getLaunchableApps(
        service: অ্যাক্সেসিবিলিটিService,
        includeসিস্টেম: Boolean = true
    ): List<ইনস্টল করোedApp> {

        val pm = service.packageManager
        val apps = mutableListOf<ইনস্টল করোedApp>()
        val seen = mutableSetOf<String>()

        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolveতথ্যs = pm.queryIntentActivities(
                intent,
                PackageManager.MATCH_ALL
            )

            for (resolveতথ্য in resolveতথ্যs) {
                val appতথ্য = resolveতথ্য.activityতথ্য.applicationতথ্য
                val packageনাম = appতথ্য.packageনাম
                if (!seen.add(packageনাম)) continue

                val isসিস্টেম = (appতথ্য.flags and Applicationতথ্য.FLAG_SYSTEM) != 0
                if (!includeসিস্টেম && isসিস্টেম) continue

                val label = pm.getApplicationLabel(appতথ্য).toString()
                apps.add(
                    ইনস্টল করোedApp(
                        packageনাম = packageনাম,
                        label = label,
                        isসিস্টেমApp = isসিস্টেম
                    )
                )
            }

            Log.d(TAG, "Found ${apps.size} launchable apps")

        } catch (e: Exception) {
            Log.e(TAG, "সমস্যা getting launchable apps: ${e.message}")
        }

        return apps
    }

    /**
     * Get all installed applications.
     */
    private fun getসবইনস্টল করোedApps(
        service: অ্যাক্সেসিবিলিটিService,
        includeসিস্টেম: Boolean = true
    ): List<ইনস্টল করোedApp> {

        val pm = service.packageManager
        val apps = mutableListOf<ইনস্টল করোedApp>()

        try {
            val packages = pm.getইনস্টল করোedApplications(0)

            for (appতথ্য in packages) {
                val isসিস্টেম = (appতথ্য.flags and Applicationতথ্য.FLAG_SYSTEM) != 0
                if (!includeসিস্টেম && isসিস্টেম) continue

                val label = pm.getApplicationLabel(appতথ্য).toString()
                apps.add(
                    ইনস্টল করোedApp(
                        packageনাম = appতথ্য.packageনাম,
                        label = label,
                        isসিস্টেমApp = isসিস্টেম
                    )
                )
            }

            Log.d(TAG, "Found ${apps.size} installed apps")

        } catch (e: Exception) {
            Log.e(TAG, "সমস্যা getting installed apps: ${e.message}")
        }

        return apps
    }

    fun getইনস্টল করোedApps(
        service: অ্যাক্সেসিবিলিটিService
    ): List<ইনস্টল করোedApp> {
        return getসবইনস্টল করোedApps(service, includeসিস্টেম = true)
    }

    /**
     * Find an app by name (fuzzy match)
     */
    fun findAppByনাম(
        service: অ্যাক্সেসিবিলিটিService,
        appনাম: String
    ): ইনস্টল করোedApp? {

        val query = normalizeনাম(appনাম)
        if (query.isBlank()) return null

        val apps = getLaunchableApps(service, includeসিস্টেম = true)

        val exact = apps.find {
            normalizeনাম(it.label) == query ||
                normalizeনাম(it.packageনাম) == query
        }
        if (exact != null) return exact

        val starts = apps.find {
            val label = normalizeনাম(it.label)
            val pkg = normalizeনাম(it.packageনাম)
            label.startsWith(query) || pkg.startsWith(query)
        }
        if (starts != null) return starts

        return apps
            .map { app ->
                val label = normalizeনাম(app.label)
                val pkg = normalizeনাম(app.packageনাম)
                var score = 0

                if (label == query || pkg == query) score += 100
                if (label.contains(query) || pkg.contains(query)) score += 50
                if (label.split(" ").all { query.contains(it) }) score += 20
                if (query.split(" ").all { label.contains(it) || pkg.contains(it) }) score += 10

                app to score
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .firstOrNull()
            ?.first
    }

    fun findAppByKeywords(
        service: অ্যাক্সেসিবিলিটিService,
        keywords: List<String>
    ): ইনস্টল করোedApp? {

        val apps = getLaunchableApps(service, includeসিস্টেম = true)
        val normalizedKeywords = keywords.map { normalizeনাম(it) }

        return apps
            .map { app ->
                val label = normalizeনাম(app.label)
                val pkg = normalizeনাম(app.packageনাম)
                val score = normalizedKeywords.sumOf { keyword ->
                    when {
                        label.contains(keyword) -> 30
                        pkg.contains(keyword) -> 20
                        else -> 0
                    }
                }
                app to score
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .firstOrNull()
            ?.first
    }

    /**
     * Get all installed apps including system apps
     */
    fun getসবApps(
        service: অ্যাক্সেসিবিলিটিService,
        includeসিস্টেম: Boolean = false
    ): List<ইনস্টল করোedApp> {
        return getসবইনস্টল করোedApps(service, includeসিস্টেম)
    }
}
