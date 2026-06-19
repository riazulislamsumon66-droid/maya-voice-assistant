package com.maya.assistant.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.telecom.TelecomManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maya.assistant.service.AccessibilityHelperService
import com.maya.assistant.service.ScreenInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val commandResult = MutableLiveData<String?>()

    private val appMap = mapOf(
        "youtube" to "com.google.android.youtube",
        "whatsapp" to "com.whatsapp",
        "instagram" to "com.instagram.android",
        "facebook" to "com.facebook.katana",
        "chrome" to "com.android.chrome",
        "gmail" to "com.google.android.gm",
        "maps" to "com.google.android.apps.maps",
        "google maps" to "com.google.android.apps.maps",
        "spotify" to "com.spotify.music",
        "netflix" to "com.netflix.mediaclient",
        "twitter" to "com.twitter.android",
        "x" to "com.twitter.android",
        "telegram" to "org.telegram.messenger",
        "snapchat" to "com.snapchat.android",
        "settings" to "com.android.settings",
        "calculator" to "com.google.android.calculator",
        "calendar" to "com.google.android.calendar",
        "clock" to "com.google.android.deskclock",
        "phone" to "com.android.dialer",
        "contacts" to "com.android.contacts",
        "play store" to "com.android.vending",
        "amazon" to "in.amazon.mShop.android.shopping",
        "flipkart" to "com.flipkart.android",
        "paytm" to "net.one97.paytm",
        "phonepe" to "com.phonepe.app",
        "gpay" to "com.google.android.apps.nbu.paisa.user",
        "google pay" to "com.google.android.apps.nbu.paisa.user",
        "zoom" to "us.zoom.videomeetings",
        "meet" to "com.google.android.apps.meetings",
        "teams" to "com.microsoft.teams",
        "tiktok" to "com.zhiliaoapp.musically",
        "discord" to "com.discord",
        "linkedin" to "com.linkedin.android",
        "camera" to "com.android.camera",
        "gallery" to "com.google.android.apps.photos",
        "photos" to "com.google.android.apps.photos"
    )

    fun executeCommand(command: com.maya.assistant.model.AppCommand) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = when (command.type) {
                    "OPEN_APP" -> openApp(command.params["app_name"] ?: "", command.params["package_name"] ?: "")
                    "CLOSE_APP" -> "Accessibility service needed to close apps"
                    "CALL" -> makeCall(command.params["target"] ?: "")
                    "SMS" -> sendSms(command.params["target"] ?: "", command.params["message"] ?: "")
                    "WHATSAPP_MSG" -> sendWhatsApp(command.params["target"] ?: "", command.params["message"] ?: "")
                    "WHATSAPP_CALL" -> makeWhatsAppCall(command.params["target"] ?: "")
                    "PRIME_CALL" -> callPrimeContact(command.params["index"]?.toIntOrNull() ?: 0)
                    "PRIME_MSG" -> messagePrimeContact(command.params["index"]?.toIntOrNull() ?: 0)
                    "VOLUME_UP" -> adjustVolumeUp()
                    "VOLUME_DOWN" -> adjustVolumeDown()
                    "MUTE" -> mutePhone()
                    "FLASHLIGHT_ON" -> toggleFlashlight(true)
                    "FLASHLIGHT_OFF" -> toggleFlashlight(false)
                    "WIFI_ON" -> toggleWifi(true)
                    "WIFI_OFF" -> toggleWifi(false)
                    "BLUETOOTH_ON" -> toggleBluetooth(true)
                    "BLUETOOTH_OFF" -> toggleBluetooth(false)
                    else -> "Unknown command: ${command.type}"
                }
                withContext(Dispatchers.Main) {
                    commandResult.value = result
                }
            } catch (e: Exception) {
                Log.e(TAG, "Command error: ${e.message}")
                withContext(Dispatchers.Main) {
                    commandResult.value = "Error: ${e.message}"
                }
            }
        }
    }

    private fun openApp(appName: String, packageName: String): String {
        val context = getApplication<Application>()
        val pm = context.packageManager

        // Try known package first
        if (packageName.isNotEmpty()) {
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return "Opening $appName..."
            }
        }

        // Try from app map
        val mappedPackage = appMap[appName.lowercase()]
        if (mappedPackage != null) {
            val intent = pm.getLaunchIntentForPackage(mappedPackage)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return "Opening $appName..."
            }
        }

        // Fallback: scan all installed apps
        val allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in allApps) {
            val label = pm.getApplicationLabel(app).toString().lowercase()
            if (label.contains(appName.lowercase())) {
                val intent = pm.getLaunchIntentForPackage(app.packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return "Opening $appName..."
                }
            }
        }

        return "App '$appName' not found"
    }

    private fun makeCall(target: String): String {
        val context = getApplication<Application>()
        val number = resolveContactNumber(target)
        return try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$number")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Calling $target..."
        } catch (e: Exception) {
            "Cannot make call: ${e.message}"
        }
    }

    private fun sendSms(target: String, message: String): String {
        val context = getApplication<Application>()
        val number = resolveContactNumber(target)
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("smsto:$number")
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Sending SMS to $target..."
        } catch (e: Exception) {
            "Cannot send SMS: ${e.message}"
        }
    }

    private fun sendWhatsApp(target: String, message: String): String {
        val context = getApplication<Application>()
        val number = resolveContactNumber(target).replace("+", "")
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$number?text=${Uri.encode(message)}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Opening WhatsApp for $target..."
        } catch (e: Exception) {
            "Cannot open WhatsApp: ${e.message}"
        }
    }

    private fun makeWhatsAppCall(target: String): String {
        val context = getApplication<Application>()
        val number = resolveContactNumber(target).replace("+", "")
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$number")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            "Opening WhatsApp call for $target..."
        } catch (e: Exception) {
            "Cannot open WhatsApp call: ${e.message}"
        }
    }

    private fun callPrimeContact(index: Int): String {
        val contact = getPrimeContact(index)
        return if (contact != null) {
            makeCall(contact.second)
        } else {
            "No prime contact at position ${index + 1}"
        }
    }

    private fun messagePrimeContact(index: Int): String {
        val contact = getPrimeContact(index)
        return if (contact != null) {
            sendSms(contact.second, "")
        } else {
            "No prime contact at position ${index + 1}"
        }
    }

    private fun getPrimeContact(index: Int): Pair<String, String>? {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("prime_contacts_json", null) ?: return null
        return try {
            val arr = JSONArray(json)
            if (index < arr.length()) {
                val obj = arr.getJSONObject(index)
                Pair(obj.getString("name"), obj.getString("number"))
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun resolveContactNumber(name: String): String {
        val context = getApplication<Application>()
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?",
            arrayOf("%$name%"),
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        // If not found in contacts, assume it's a number
        return name.replace(" ", "").replace("-", "")
    }

    private fun adjustVolumeUp(): String {
        val context = getApplication<Application>()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        am.adjustVolume(android.media.AudioManager.ADJUST_RAISE, android.media.AudioManager.FLAG_SHOW_UI)
        return "Volume increased"
    }

    private fun adjustVolumeDown(): String {
        val context = getApplication<Application>()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        am.adjustVolume(android.media.AudioManager.ADJUST_LOWER, android.media.AudioManager.FLAG_SHOW_UI)
        return "Volume decreased"
    }

    private fun mutePhone(): String {
        val context = getApplication<Application>()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        am.ringerMode = android.media.AudioManager.RINGER_MODE_SILENT
        return "Phone muted"
    }

    private fun toggleFlashlight(on: Boolean): String {
        val context = getApplication<Application>()
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, on)
            }
            if (on) "Flashlight turned on" else "Flashlight turned off"
        } catch (e: Exception) {
            "Cannot toggle flashlight: ${e.message}"
        }
    }

    private fun toggleWifi(on: Boolean): String {
        return try {
            val context = getApplication<Application>()
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            if (on) "Opening WiFi settings (turn ON manually)" else "Opening WiFi settings (turn OFF manually)"
        } catch (e: Exception) {
            "Cannot toggle WiFi: ${e.message}"
        }
    }

    private fun toggleBluetooth(on: Boolean): String {
        return try {
            val context = getApplication<Application>()
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            if (on) "Opening Bluetooth settings (turn ON manually)" else "Opening Bluetooth settings (turn OFF manually)"
        } catch (e: Exception) {
            "Cannot toggle Bluetooth: ${e.message}"
        }
    }

    fun acceptCall(): String {
        return try {
            val context = getApplication<Application>()
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telecomManager.acceptRingingCall()
                "Call accepted"
            } else {
                "Call accept requires Android 8.0+"
            }
        } catch (e: Exception) {
            "Cannot accept call: ${e.message}"
        }
    }

    fun rejectCall(): String {
        return try {
            val context = getApplication<Application>()
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                telecomManager.endCall()
                "Call rejected"
            } else {
                "Call reject requires Android 9.0+"
            }
        } catch (e: Exception) {
            "Cannot reject call: ${e.message}"
        }
    }

    fun getPrimeContactsJson(): String {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
        return prefs.getString("prime_contacts_json", "[]") ?: "[]"
    }

    fun getPrimeContactsJson(): String {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
        return prefs.getString("prime_contacts_json", "[]") ?: "[]"
    }

    fun savePrimeContacts(json: String) {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("prime_contacts_json", json).apply()
    }

    // ===== SCREEN READING METHODS =====

    /**
     * Get current screen content as text
     */
    fun getScreenContent(): String {
        val service = AccessibilityHelperService.instance
        return if (service != null && AccessibilityHelperService.isEnabled()) {
            service.getScreenContent()
        } else {
            ""
        }
    }

    /**
     * Get structured screen info (app name, text, clickable items)
     */
    fun getScreenInfo(): ScreenInfo {
        val service = AccessibilityHelperService.instance
        return if (service != null && AccessibilityHelperService.isEnabled()) {
            service.getScreenInfo()
        } else {
            ScreenInfo("Unknown", "", emptyList())
        }
    }

    /**
     * Click on text on screen
     */
    fun clickOnScreenText(text: String): Boolean {
        val service = AccessibilityHelperService.instance
        return if (service != null && AccessibilityHelperService.isEnabled()) {
            service.clickOnText(text)
        } else false
    }

    /**
     * Type text into focused input
     */
    fun typeOnScreen(text: String) {
        val service = AccessibilityHelperService.instance
        if (service != null && AccessibilityHelperService.isEnabled()) {
            service.typeText(text)
        }
    }

    /**
     * Smart type — finds EditText and types
     */
    fun smartTypeOnScreen(text: String, hint: String = ""): Boolean {
        val service = AccessibilityHelperService.instance
        return if (service != null && AccessibilityHelperService.isEnabled()) {
            service.typeTextSmart(text, hint)
        } else false
    }

    /**
     * Get clickable elements from screen
     */
    fun getClickableElements(): List<String> {
        val service = AccessibilityHelperService.instance
        return if (service != null && AccessibilityHelperService.isEnabled()) {
            service.getClickableElements()
        } else emptyList()
    }

    /**
     * Click at coordinates
     */
    fun clickAt(x: Int, y: Int) {
        val service = AccessibilityHelperService.instance
        if (service != null && AccessibilityHelperService.isEnabled()) {
            service.clickAt(x, y)
        }
    }

    /**
     * Scroll screen
     */
    fun scrollScreen(direction: String) {
        val service = AccessibilityHelperService.instance
        if (service != null && AccessibilityHelperService.isEnabled()) {
            if (direction == "down") service.scrollDown() else service.scrollUp()
        }
    }

    /**
     * Wait for text and click
     */
    fun waitAndClick(text: String, timeoutMs: Long = 5000): Boolean {
        val service = AccessibilityHelperService.instance
        return if (service != null && AccessibilityHelperService.isEnabled()) {
            service.waitAndClick(text, timeoutMs)
        } else false
    }
}
