package com.maya.assistant.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * CommandWhitelist — Only registered voice commands are executed
 * 
 * Security layer: Even if someone else's voice is detected,
 * only pre-registered commands will work.
 * Unknown commands are rejected.
 */
class CommandWhitelist(private val context: Context) {

    companion object {
        private const val TAG = "CommandWhitelist"
        private const val PREFS_KEY = "maya_command_whitelist"
        private const val ENABLED_KEY = "maya_whitelist_enabled"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)

    // All registered command keywords (Bangla + English + Hindi)
    private val registeredCommands = setOf(
        // === APP CONTROL ===
        "open", "close", "launch", "start", "exit", "quit",
        "kholo", "khola", "open karo", "band karo", "bondho koro",
        "shuru koro", "chalao", "chola", "start karo",
        
        // === CALL ===
        "call", "phone", "dial",
        "call karo", "phone karo", "dial karo",
        "call kora", "phone kora", "call dhao",
        
        // === MESSAGE ===
        "message", "sms", "text", "msg",
        "sms karo", "message karo", "msg karo",
        "sms bhejo", "message bhejo", "msg bhejo",
        "sms koro", "message koro", "msg koro",
        
        // === WHATSAPP ===
        "whatsapp",
        "whatsapp karo", "whatsapp bhejo",
        
        // === VOLUME ===
        "volume", "volume up", "volume down",
        "volume badhao", "volume barao", "volume kam karo", "volume kom koro",
        "awaz badhao", "aawaz barao", "awaz kam karo",
        "mute", "silent", "shanti karo", "chup karo",
        
        // === FLASHLIGHT ===
        "torch", "flashlight", "light", "bati",
        "torch on", "torch off", "flashlight on", "flashlight off",
        "torch jalao", "torch bondho", "flashlight jalao",
        "bati jalao", "bati bondho",
        
        // === WIFI ===
        "wifi", "wifi on", "wifi off",
        "wifi chalu karo", "wifi band karo",
        
        // === BLUETOOTH ===
        "bluetooth", "bluetooth on", "bluetooth off",
        "bluetooth chalu karo", "bluetooth band karo",
        
        // === SCREEN COMMANDS ===
        "click", "tap", "press", "type", "write", "enter",
        "click karo", "tap koro", "type karo", "likho", "lekho",
        "click karo", "tap koro",
        "scroll", "scroll down", "scroll up",
        "niche scroll", "upore scroll", "neeche scroll", "oopar scroll",
        "read screen", "screen",
        "screen bolo", "screen poro", "screen batao",
        "search", "khujo", "search karo",
        
        // === FACEBOOK ===
        "post", "facebook", "fb",
        "post karo", "post koro", "post bhejo",
        
        // === MUSIC ===
        "play music", "pause music", "stop music",
        "gan bajao", "gana bajao", "gan bondho",
        "next song", "previous song", "next track", "previous track",
        "porer gan", "ager gan", "porer gaan", "ager gaan",
        
        // === PRIME CONTACTS ===
        "close friend", "best friend", "prime contact",
        
        // === NAVIGATION ===
        "go back", "go home", "home", "back",
        
        // === CAMERA ===
        "camera", "screenshot", "screen shot", "capture",
        
        // === SETTINGS ===
        "settings",
        
        // === HELP ===
        "help", "commands", "help karo", "ki korte paro",
        "what can you do", "tumi ki korte paro"
    )

    // Commands that require authentication
    private val sensitiveCommands = setOf(
        "call", "phone", "dial", "message", "sms", "whatsapp",
        "call karo", "phone karo", "sms karo", "message karo",
        "post", "facebook"
    )

    /**
     * Check if a command is in the whitelist
     */
    fun isCommandAllowed(input: String): Boolean {
        val lower = input.lowercase().trim()
        
        // Check if whitelist enforcement is enabled
        val whitelistEnabled = prefs.getBoolean(ENABLED_KEY, true)
        if (!whitelistEnabled) return true // All commands allowed if disabled
        
        // Check against registered commands
        for (cmd in registeredCommands) {
            if (lower.contains(cmd)) {
                Log.d(TAG, "Command allowed: '$input' matched '$cmd'")
                return true
            }
        }
        
        Log.d(TAG, "Command rejected: '$input' not in whitelist")
        return false
    }

    /**
     * Check if command is sensitive (requires both voice + face auth)
     */
    fun isSensitiveCommand(input: String): Boolean {
        val lower = input.lowercase().trim()
        for (cmd in sensitiveCommands) {
            if (lower.contains(cmd)) return true
        }
        return false
    }

    /**
     * Get all allowed commands for display
     */
    fun getAllCommands(): Set<String> = registeredCommands

    /**
     * Enable/disable whitelist enforcement
     */
    fun setWhitelistEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(ENABLED_KEY, enabled).apply()
    }

    fun isWhitelistEnabled(): Boolean = prefs.getBoolean(ENABLED_KEY, true)
}
