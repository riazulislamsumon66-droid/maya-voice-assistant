package com.maya.assistant.security

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import java.util.concurrent.Executor

/**
 * BiometricManager - Fingerprint দিয়ে ভেরিফাইentication for MAYA
 * Requirements:
 * 1. ✅ Fingerprint দিয়ে ভেরিফাই on launch
 * 2. ✅ Block automation until success
 * 3. ✅ Fallback PIN if unavailable
 * 4. ✅ Secure session timeout
 */
object BiometricManager {

    private const val TAG = "MAYA_BIOMETRIC"
    private const val SESSION_TIMEOUT_MS = 5 * 60 * 1000 // 5 minutes
    private const val PREFS_NAME = "maya_security"
    private const val KEY_LAST_AUTH = "last_auth_time"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    private const val KEY_PIN_SET = "pin_set"

    private var isভেরিফাই কRowd = false
    private var lastAuthসময় = 0L

    /**
     * Check if biometric is available on device
     */
    fun isবায়োমেট্রিকপাওয়া যাচ্ছে(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canভেরিফাই কRow() == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Check if user has enabled biometric in settings
     */
    fun isবায়োমেট্রিকEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    /**
     * Enabled কRow/disable biometric
     */
    fun setবায়োমেট্রিকEnabled(context: Context, enabled: Boolean) {
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
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFallback: () -> Unit
    ) {
        // Check session timeout
        if (isSessionবৈধ(context)) {
            Log.d(TAG, "Session এখনো valid, skip করছি")
            onSuccess()
            return
        }

        if (!isবায়োমেট্রিকEnabled(context)) {
            Log.d(TAG, "বায়োমেট্রিক not enabled, proceeding")
            onSuccess()
            return
        }

        if (!isবায়োমেট্রিকপাওয়া যাচ্ছে(context)) {
            Log.w(TAG, "বায়োমেট্রিক not available, using fallback")
            onFallback()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            showBiometricPrompt(context, onSuccess, onError, onFallback)
        } else {
            onFallback()
        }
    }

    private fun showBiometricPrompt(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFallback: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)

        // Create Promptতথ্য using the builder
        val promptতথ্য = BiometricPrompt.Promptতথ্য.Builder()
            .setTitle("MAYA নিরাপত্তা")
            .setSubtitle("যাচাই কRow your identity")
            .setDescription("আঙুল sensor এ রাখো")
            .setNegativeButtonText("PIN ব্যবহার কRow")
            .build()

        // Get activity from context
        val activity = context as androidx.fragment.app.FragmentActivity

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.প্রমাণীকরণকলback() {
                override fun onপ্রমাণীকরণSucceeded(result: BiometricPrompt.প্রমাণীকরণResult) {
                    super.onপ্রমাণীকরণSucceeded(result)
                    Log.d(TAG, "✅ বায়োমেট্রিক authentication succeeded")
                    isভেরিফাই কRowd = true
                    lastAuthসময় = System.currentসময়Millis()
                    saveAuthসময়(context)
                    onSuccess()
                }

                override fun onপ্রমাণীকরণFailed() {
                    super.onপ্রমাণীকরণFailed()
                    Log.w(TAG, "বায়োমেট্রিক authentication failed")
                    onError("Fingerprint not recognized. আবার চেষ্টা কRow.")
                }

                override fun onপ্রমাণীকরণError(errorCode: Int, errString: CharSequence) {
                    super.onপ্রমাণীকরণError(errorCode, errString)
                    Log.e(TAG, "বায়োমেট্রিক error: $errorCode - $errString")
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        onFallback()
                    } else {
                        onError(errString.toString())
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
        if (!isভেরিফাই কRowd) return false

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastAuth = prefs.getLong(KEY_LAST_AUTH, 0)
        val elapsed = System.currentসময়Millis() - lastAuth

        return elapsed < SESSION_TIMEOUT_MS
    }

    /**
     * Reset কRow authentication state
     */
    fun resetAuth() {
        isভেরিফাই কRowd = false
        lastAuthসময় = 0
    }

    /**
     * Save কRow authentication time
     */
    private fun saveAuthসময়(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_AUTH, System.currentসময়Millis())
            .apply()
    }

    /**
     * Check if PIN Set আছে
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
     * যাচাই কRow PIN
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