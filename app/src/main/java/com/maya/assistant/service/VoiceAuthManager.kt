package com.maya.assistant.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * VoiceAuthManager — Manages voice authentication state
 * 
 * Simple approach:
 * - When hotword detected, verify voice against enrolled profile
 * - If match → authenticated (commands work)
 * - If no match → not authenticated (conversation only)
 * - If no voice enrolled → everyone can use commands (first-time setup)
 */
class VoiceAuthManager(private val context: Context) {

    companion object {
        private const val TAG = "VoiceAuthManager"
        private const val PREFS_KEY = "maya_voice_enrolled"
        private const val AUTH_STATE_KEY = "maya_auth_state"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)

    private var voiceAuthenticator: VoiceAuthenticator? = null
    private var isVoiceEnrolled = false

    // Current auth state
    var isAuthenticated = false
        private set

    var authStatus: AuthStatus = AuthStatus.NOT_ENROLLED
        private set

    enum class AuthStatus {
        NOT_ENROLLED,    // No voice profile — first time setup needed
        ENROLLED,        // Voice enrolled, waiting for verification
        VERIFIED,        // Voice matched — full access
        REJECTED         // Voice didn't match — conversation only
    }

    init {
        isVoiceEnrolled = prefs.getBoolean(PREFS_KEY, false)
        if (isVoiceEnrolled) {
            voiceAuthenticator = VoiceAuthenticator(context)
            authStatus = AuthStatus.ENROLLED
        }
        Log.d(TAG, "VoiceAuthManager init — enrolled: $isVoiceEnrolled")
    }

    /**
     * Enroll Jaan's voice — call this from Settings
     */
    suspend fun enrollVoice(): Boolean {
        Log.d(TAG, "Starting voice enrollment...")
        val authenticator = VoiceAuthenticator(context)
        val success = authenticator.enrollVoice()
        if (success) {
            isVoiceEnrolled = true
            prefs.edit().putBoolean(PREFS_KEY, true).apply()
            voiceAuthenticator = authenticator
            authStatus = AuthStatus.ENROLLED
            Log.d(TAG, "Voice enrollment successful!")
        }
        return success
    }

    /**
     * Verify voice after hotword detection
     * Returns true if voice matches Jaan's profile
     */
    suspend fun verifyVoice(): Boolean {
        if (!isVoiceEnrolled || voiceAuthenticator == null) {
            // No voice enrolled — allow everything (first-time user)
            Log.d(TAG, "No voice enrolled — allowing all commands")
            isAuthenticated = true
            authStatus = AuthStatus.NOT_ENROLLED
            return true
        }

        Log.d(TAG, "Verifying voice...")
        val (matched, similarity) = voiceAuthenticator!!.verifyVoice()
        
        isAuthenticated = matched
        authStatus = if (matched) AuthStatus.VERIFIED else AuthStatus.REJECTED
        
        Log.d(TAG, "Voice verification: matched=$matched, similarity=$similarity")
        return matched
    }

    /**
     * Check if commands should be allowed
     */
    fun canExecuteCommands(): Boolean {
        return isAuthenticated || !isVoiceEnrolled
    }

    /**
     * Reset auth state (after timeout)
     */
    fun resetAuth() {
        isAuthenticated = false
        if (isVoiceEnrolled) {
            authStatus = AuthStatus.ENROLLED
        }
    }

    fun isVoiceEnrolled(): Boolean = isVoiceEnrolled

    fun getStatusText(): String {
        return when (authStatus) {
            AuthStatus.NOT_ENROLLED -> "⚠️ Voice not enrolled — anyone can use commands"
            AuthStatus.ENROLLED -> "🔴 Voice enrolled — verify needed"
            AuthStatus.VERIFIED -> "🟢 Voice verified — full access"
            AuthStatus.REJECTED -> "🔴 Voice not recognized — conversation only"
        }
    }
}
