package com.maya.assistant.security

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.biometric.বায়োমেট্রিকPrompt
import androidx.biometric.বায়োমেট্রিকManager
import java.util.concurrent.Executor

/**
 * বায়োমেট্রিকManager - Fingerprint দিয়ে ভেরিফাইentication for MAYA
 * Requirements:
 * 1. ✅ Fingerprint দিয়ে ভেরিফাই on launch
 * 2. ✅ Block automation until success
 * 3. ✅ Fallback PIN if unavailable
 * 4. ✅ Secure session timeout
 */
object বায়োমেট্রিকManager {

    private const val TAG = "MAYA_BIOMETRIC"
    private const val SESSION_TIMEOUT_MS = 5 * 60 * 1000 // 5 minutes
    private const val PREFS_NAME = "maya_security"
    private const val KEY_LAST_AUTH = "last_auth_time"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    private const val KEY_PIN_SET = "pin_set"

    private var isভেরিফাই করোd = false
    private var lastAuthসময় = 0L

    /**
     * Check if biometric is available on device
     */
    fun isবায়োমেট্রিকপাওয়া যাচ্ছে(context: Context): Boolean {
        val biometricManager = বায়োমেট্রিকManager.from(context)
        return biometricManager.canভেরিফাই করো() == বায়োমেট্রিকManager.BIOMETRIC_SUCCESS
    }

    /**
     * Check if user has enabled biometric in settings
     */
    fun isবায়োমেট্রিকচালু(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    /**
     * চালু করো/disable biometric
     */
    fun setবায়োমেট্রিকচালু(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    /**
     * Main authentication method
     */
    fun authenticate(
        context: Context,
        onসফল: () -> Unit,
        onসমস্যা: (String) -> Unit,
        onFallback: () -> Unit
    ) {
        // Check session timeout
        if (isSessionবৈধ(context)) {
            Log.d(TAG, "Session এখনো valid, skip করছি")
            onসফল()
            return
        }

        if (!isবায়োমেট্রিকচালু(context)) {
            Log.d(TAG, "বায়োমেট্রিক not enabled, proceeding")
            onসফল()
            return
        }

        if (!isবায়োমেট্রিকপাওয়া যাচ্ছে(context)) {
            Log.w(TAG, "বায়োমেট্রিক not available, using fallback")
            onFallback()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            showবায়োমেট্রিকPrompt(context, onসফল, onসমস্যা, onFallback)
        } else {
            onFallback()
        }
    }

    private fun showবায়োমেট্রিকPrompt(
        context: Context,
        onসফল: () -> Unit,
        onসমস্যা: (String) -> Unit,
        onFallback: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)

        // Create Promptতথ্য using the builder
        val promptতথ্য = বায়োমেট্রিকPrompt.Promptতথ্য.Builder()
            .setTitle("MAYA নিরাপত্তা")
            .setSubtitle("যাচাই করো your identity")
            .setDescription("আঙুল sensor এ রাখো")
            .setNegativeButtonText("PIN ব্যবহার করো")
            .build()

        // Get activity from context
        val activity = context as androidx.fragment.app.FragmentActivity

        val biometricPrompt = বায়োমেট্রিকPrompt(
            activity,
            executor,
            object : বায়োমেট্রিকPrompt.প্রমাণীকরণকলback() {
                override fun onপ্রমাণীকরণSucceeded(result: বায়োমেট্রিকPrompt.প্রমাণীকরণResult) {
                    super.onপ্রমাণীকরণSucceeded(result)
                    Log.d(TAG, "✅ বায়োমেট্রিক authentication succeeded")
                    isভেরিফাই করোd = true
                    lastAuthসময় = সিস্টেম.currentসময়Millis()
                    saveAuthসময়(context)
                    onসফল()
                }

                override fun onপ্রমাণীকরণব্যর্থ() {
                    super.onপ্রমাণীকরণব্যর্থ()
                    Log.w(TAG, "বায়োমেট্রিক authentication failed")
                    onসমস্যা("Fingerprint not recognized. আবার চেষ্টা করো.")
                }

                override fun onপ্রমাণীকরণসমস্যা(errorCode: Int, errString: CharSequence) {
                    super.onপ্রমাণীকরণসমস্যা(errorCode, errString)
                    Log.e(TAG, "বায়োমেট্রিক error: $errorCode - $errString")
                    if (errorCode == বায়োমেট্রিকPrompt.ERROR_USER_CANCELED ||
                        errorCode == বায়োমেট্রিকPrompt.ERROR_NEGATIVE_BUTTON) {
                        onFallback()
                    } else {
                        onসমস্যা(errString.toString())
                    }
                }
            }
        )

        biometricPrompt.authenticate(promptতথ্য)
    }

    /**
     * Check if current session is valid (not timed out)
     */
    fun isSessionবৈধ(context: Context): Boolean {
        if (!isভেরিফাই করোd) return false

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastAuth = prefs.getLong(KEY_LAST_AUTH, 0)
        val elapsed = সিস্টেম.currentসময়Millis() - lastAuth

        return elapsed < SESSION_TIMEOUT_MS
    }

    /**
     * রিসেট করো authentication state
     */
    fun resetAuth() {
        isভেরিফাই করোd = false
        lastAuthসময় = 0
    }

    /**
     * সেভ করো authentication time
     */
    private fun saveAuthসময়(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_AUTH, সিস্টেম.currentসময়Millis())
            .apply()
    }

    /**
     * Check if PIN সেট আছে
     */
    fun isPinSet(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_PIN_SET, false)
    }

    /**
     * Set PIN
     */
    fun setPin(context: Context, pin: String) {
        // In production, use encrypted storage
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_PIN_SET, true)
            .putString("pin_hash", hashPin(pin))
            .apply()
    }

    /**
     * যাচাই করো PIN
     */
    fun verifyPin(context: Context, pin: String): Boolean {
        val storedHash = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString("pin_hash", "") ?: ""
        return storedHash == hashPin(pin)
    }

    private fun hashPin(pin: String): String {
        // Simple hash - in production use proper encryption
        return pin.hashCode().toString()
    }
}