package com.maya.assistant.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * নিরাপত্তাManager — AES-256 Encryption + PIN + ভয়েস + App Lock per package
 *
 * ADDED: isPackageলক আছে() — অ্যাক্সেসিবিলিটিসাহায্যerService ke liye
 */
object নিরাপত্তাManager {

    private const val TAG = "MAYA_SECURITY"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "MAYA_MASTER_KEY"
    private const val AES_MODE = "AES/GCM/নাPadding"
    private const val GCM_TAG_LENGTH = 128

    private const val PREFS_NAME = "maya_security_prefs"
    private const val KEY_PIN_HASH       = "pin_hash"
    private const val KEY_VOICE_PHRASE   = "voice_passphrase"
    private const val KEY_APP_LOCK_ON    = "app_lock_enabled"
    private const val KEY_BIOMETRIC_ON   = "biometric_lock_enabled"
    private const val KEY_DEVICE_LOCK_ON = "device_lock_enabled"
    private const val KEY_PRIVATE_MODE   = "private_mode_active"
    private const val KEY_WRONG_ATTEMPTS = "wrong_attempts"
    private const val KEY_LOCKOUT_TIME   = "lockout_until"
    private const val KEY_LOCKED_PACKAGES= "locked_packages"
    private const val MAX_ATTEMPTS       = 3
    private const val LOCKOUT_MS         = 30_000L

    // ── KEYSTORE ────────────────────────────────────────────────

    private fun getOrCreateKey(): SecretKey {
        val ks = KeyStore.getInstance(KEYSTORE_PROVIDER).also { it.load(null) }
        if (ks.containsAlias(KEY_ALIAS)) {
            return (ks.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        }
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        keyGen.init(
            KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeyআকার(256)
                .build()
        )
        return keyGen.generateKey()
    }

    // ── ENCRYPT / DECRYPT ───────────────────────────────────────

    fun encrypt(plainText: String): String {
        return try {
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val iv = cipher.iv
            val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            "${Base64.encodeToString(iv, Base64.NO_WRAP)}:${Base64.encodeToString(encrypted, Base64.NO_WRAP)}"
        } catch (e: Exception) {
            Log.e(TAG, "Encrypt failed: ${e.message}")
            plainText
        }
    }

    fun decrypt(cipherText: String): String {
        return try {
            val parts = cipherText.split(":")
            if (parts.size != 2) return cipherText
            val iv        = Base64.decode(parts[0], Base64.NO_WRAP)
            val encrypted = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
            String(cipher.doFinal(encrypted), Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Decrypt failed: ${e.message}")
            ""
        }
    }

    // ── PIN ─────────────────────────────────────────────────────

    fun setPin(context: Context, pin: String) {
        val hash = hashPin(pin)
        getPrefs(context).edit().putString(KEY_PIN_HASH, encrypt(hash)).apply()
    }

    fun verifyPin(context: Context, inputPin: String): PinResult {
        val lockUntil = getPrefs(context).getLong(KEY_LOCKOUT_TIME, 0L)
        if (সিস্টেম.currentসময়Millis() < lockUntil) {
            val rem = (lockUntil - সিস্টেম.currentসময়Millis()) / 1000
            return PinResult.LOCKED_OUT(rem)
        }
        val stored = getPrefs(context).getString(KEY_PIN_HASH, null) ?: return PinResult.NOT_SET
        val inputHash = hashPin(inputPin)
        return if (inputHash == decrypt(stored)) {
            resetAttempts(context)
            PinResult.CORRECT
        } else {
            handleWrongAttempt(context)
        }
    }

    private fun handleWrongAttempt(context: Context): PinResult.WRONG {
        val prefs = getPrefs(context)
        val attempts = prefs.getInt(KEY_WRONG_ATTEMPTS, 0) + 1
        prefs.edit().putInt(KEY_WRONG_ATTEMPTS, attempts).apply()
        if (attempts >= MAX_ATTEMPTS) {
            prefs.edit().putLong(KEY_LOCKOUT_TIME, সিস্টেম.currentসময়Millis() + LOCKOUT_MS).apply()
            return PinResult.WRONG(attempts, lockedOut = true)
        }
        return PinResult.WRONG(attempts, lockedOut = false)
    }

    fun resetAttempts(context: Context) {
        getPrefs(context).edit()
            .putInt(KEY_WRONG_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_TIME, 0L)
            .apply()
    }

    fun hasPin(context: Context) = getPrefs(context).contains(KEY_PIN_HASH)
    fun removePin(context: Context) { getPrefs(context).edit().remove(KEY_PIN_HASH).apply() }
    fun getWrongAttempts(context: Context) = getPrefs(context).getInt(KEY_WRONG_ATTEMPTS, 0)
    fun getRemainingAttempts(context: Context) = (MAX_ATTEMPTS - getWrongAttempts(context)).coerceAtLeast(0)

    private fun hashPin(pin: String): String {
        val digest = java.security.মেসেজDigest.getInstance("SHA-256")
        return Base64.encodeToString(digest.digest(pin.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
    }

    // ── VOICE PASSPHRASE ────────────────────────────────────────

    fun setভয়েসPassphrase(context: Context, phrase: String) {
        getPrefs(context).edit()
            .putString(KEY_VOICE_PHRASE, encrypt(normalise(phrase)))
            .apply()
    }

    fun verifyভয়েসPassphrase(context: Context, spoken: String): Boolean {
        val stored = getPrefs(context).getString(KEY_VOICE_PHRASE, null) ?: return false
        val storedPhrase = decrypt(stored)
        val spokenনাrm = normalise(spoken)

        if (spokenনাrm == storedPhrase) return true
        if (spokenনাrm.contains(storedPhrase)) return true

        val storedWords = storedPhrase.split(" ").filter { it.isনাtEmpty() }
        val spokenWords  = spokenনাrm.split(" ").filter { it.isনাtEmpty() }
        if (storedWords.isEmpty()) return false

        val matchগণনা = storedWords.count { sw -> spokenWords.any { it.contains(sw) || sw.contains(it) } }
        return matchগণনা.toFloat() / storedWords.size >= 0.8f
    }

    fun hasভয়েসPassphrase(context: Context) = getPrefs(context).contains(KEY_VOICE_PHRASE)

    private fun normalise(phrase: String) =
        phrase.lowercase().trim().replace(Regex("[^a-z0-9\\s]"), "").replace(Regex("\\s+"), " ")

    // ── APP LOCK (global) ────────────────────────────────────────

    fun setAppLockচালু(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_APP_LOCK_ON, enabled).apply()
        Log.d(TAG, "Global App Lock set to: $enabled")
    }

    fun isAppLockচালু(context: Context): Boolean {
        val masterচালু = getPrefs(context).getBoolean(KEY_APP_LOCK_ON, false)
        val patternচালু = PatternManager.isPatternLockচালু(context)
        return masterচালু || patternচালু
    }

    // ── BIOMETRIC ────────────────────────────────────────────────

    fun isবায়োমেট্রিকচালু(context: Context): Boolean {
        val enabled = getPrefs(context).getBoolean(KEY_BIOMETRIC_ON, false)
        Log.d(TAG, "isবায়োমেট্রিকচালু: $enabled")
        return enabled
    }
    
    fun setবায়োমেট্রিকচালু(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_BIOMETRIC_ON, enabled).apply()
        Log.d(TAG, "setবায়োমেট্রিকচালু: $enabled")
    }

    // ── DEVICE LOCK (সিস্টেম Lock) ────────────────────────────────

    fun isDeviceLockচালু(context: Context) = getPrefs(context).getBoolean(KEY_DEVICE_LOCK_ON, false)
    fun setDeviceLockচালু(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DEVICE_LOCK_ON, enabled).apply()
    }

    // ── PER-PACKAGE APP LOCK ─────────────────────────────────────

    /**
     * ✅ FIXED: Checks if any lock method is active + package is in list
     */
    fun isPackageলক আছে(context: Context, packageনাম: String): Boolean {
        // Master check: If global app lock is off AND pattern is off, nothing is locked
        if (!isAppLockচালু(context) && !isবায়োমেট্রিকচালু(context) && !isDeviceLockচালু(context)) {
            return false
        }

        // --- ADDED SYSTEM APP PROTECTION ---
        val systemApps = listOf(
            "com.android.settings",
            "com.android.packageinstaller",
            "com.google.android.packageinstaller",
            "com.maya.assistant" // Don't lock yourself
        )
        if (systemApps.contains(packageনাম)) return false // সেটিংস should NOT be locked for now to avoid loops

        // লক আছে packages list check karo
        val locked = getলক আছেPackages(context)
        val isলক আছে = locked.contains(packageনাম)
        Log.d(TAG, "Checking lock for $packageনাম: $isলক আছে")
        return isলক আছে
    }

    fun addলক আছেPackage(context: Context, packageনাম: String) {
        val current = getলক আছেPackages(context).toMutableSet()
        current.add(packageনাম)
        saveলক আছেPackages(context, current)
    }

    fun removeলক আছেPackage(context: Context, packageনাম: String) {
        val current = getলক আছেPackages(context).toMutableSet()
        current.remove(packageনাম)
        saveলক আছেPackages(context, current)
    }

    fun getলক আছেPackages(context: Context): Set<String> {
        val stored = getPrefs(context).getString(KEY_LOCKED_PACKAGES, "") ?: ""
        return if (stored.isEmpty()) emptySet()
               else stored.split(",").filter { it.isনাtEmpty() }.toSet()
    }

    private fun saveলক আছেPackages(context: Context, packages: Set<String>) {
        getPrefs(context).edit()
            .putString(KEY_LOCKED_PACKAGES, packages.joinToString(","))
            .apply()
    }

    // ── PRIVATE MODE ─────────────────────────────────────────────

    fun enablePrivateMode(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_PRIVATE_MODE, true).apply()
    }

    fun disablePrivateMode(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_PRIVATE_MODE, false).apply()
    }

    fun isPrivateModeসক্রিয়(context: Context) = getPrefs(context).getBoolean(KEY_PRIVATE_MODE, false)

    // ── RESULT ───────────────────────────────────────────────────

    sealed class PinResult {
        object CORRECT    : PinResult()
        object NOT_SET    : PinResult()
        data class WRONG(val attempts: Int, val lockedOut: Boolean) : PinResult()
        data class LOCKED_OUT(val secondsRemaining: Long) : PinResult()
    }

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}