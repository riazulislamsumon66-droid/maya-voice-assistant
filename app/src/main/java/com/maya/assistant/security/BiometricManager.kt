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

    private var isVerified = false
    private var lastAuthTime = 0L

    /**
     * Check if biometric is available on device
     */
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Check if user has enabled biometric in settings
     */
    fun isBiometricEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    /**
     * Enabled do/disable biometric
     */
    fun setBiometricEnabled(context: Context, enabled: Boolean) {
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
        if (isSessionValid(context)) {
            Log.d(TAG, "Session এখনো valid, skip করছি")
            onSuccess()
            return
        }

        if (!isBiometricEnabled(context)) {
            Log.d(TAG, "বায়োমেট্রিক not enabled, proceeding")
            onSuccess()
            return
        }

        if (!isBiometricAvailable(context)) {
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

        // Create PromptInfo using the builder
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("MAYA নিরাপত্তা")
            .setSubtitle("যাচাই do your identity")
            .setDescription("আঙুল sensor এ রাখো")
            .setNegativeButtonText("PIN ব্যবহার do")
            .build()

        // Get activity from context
        val activity = context as androidx.fragment.app.FragmentActivity

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "✅ বায়োমেট্রিক authentication succeeded")
                    isVerified = true
                    lastAuthTime = System.currentTimeMillis()
                    saveAuthTime(context)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w(TAG, "বায়োমেট্রিক authentication failed")
                    onError("Fingerprint not recognized. আবার চেষ্টা do.")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
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

        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Check if current session is valid (not timed out)
     */
    fun isSessionValid(context: Context): Boolean {
        if (!isVerified) return false

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastAuth = prefs.getLong(KEY_LAST_AUTH, 0)
        val elapsed = System.currentTimeMillis() - lastAuth

        return elapsed < SESSION_TIMEOUT_MS
    }

    /**
     * Reset do authentication state
     */
    fun resetAuth() {
        isVerified = false
        lastAuthTime = 0
    }

    /**
     * Save do authentication time
     */
    private fun saveAuthTime(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_AUTH, System.currentTimeMillis())
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
     * যাচাই do PIN
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