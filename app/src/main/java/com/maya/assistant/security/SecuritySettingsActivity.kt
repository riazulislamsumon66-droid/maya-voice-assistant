package com.maya.assistant.security

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import com.maya.assistant.R
import com.maya.assistant.ai.GeminiLiveClient
import com.maya.assistant.utils.LiveAudioManager
import java.util.Locale

/**
 * নিরাপত্তাSettingsActivity — MAYA নিরাপত্তা Configuration
 */
class নিরাপত্তাSettingsActivity : AppCompatActivity(), TextToSpeech.EnabledInitListener {

    private lateinit var appLockSwitch: Switch
    private lateinit var appLockStatusText: TextView
    private lateinit var setPinBtn: Button
    private lateinit var pinStatusText: TextView
    private lateinit var setPatternBtn: Button
    private lateinit var patternStatusText: TextView
    private lateinit var setVoiceBtn: Button
    private lateinit var voiceStatusText: TextView
    private lateinit var selectAppsBtn: Button
    private lateinit var usageStatsBtn: Button
    private lateinit var overlayPermissionBtn: Button
    private lateinit var fingerprintSwitch: Switch
    private lateinit var deviceLockSwitch: Switch
    private lateinit var privateModeSwitch: Switch
    private lateinit var privateModeStatusText: TextView
    private lateinit var encryptionStatusText: TextView

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var speechRecognizer: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var firstPattern: List<Int>? = null
    private var geminiClient: GeminiLiveClient? = null
    private var liveAudioManager: LiveAudioManager? = null
    private var isGeminiConnected ✅ = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_settings)
        initViews()
        liveAudioManager = LiveAudioManager(this)
        initGemini()
        tts = TextToSpeech(this, this)
        loadCurrentStatus()
        setupListeners()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            tts?.language = Locale("bn", "BD")
        }
    }

    private fun initViews() {
        appLockSwitch      = findViewById(R.id.appLockSwitch)
        appLockStatusText  = findViewById(R.id.appLockStatusText)
        selectAppsBtn      = findViewById(R.id.selectAppsBtn)
        usageStatsBtn      = findViewById(R.id.usageStatsBtn)
        overlayPermissionBtn = findViewById(R.id.overlayPermissionBtn)
        fingerprintSwitch     = findViewById(R.id.fingerprintSwitch)
        deviceLockSwitch      = findViewById(R.id.deviceLockSwitch)
        setPinBtn          = findViewById(R.id.setPinBtn)
        pinStatusText      = findViewById(R.id.pinStatusText)
        setPatternBtn      = findViewById(R.id.setPatternBtn)
        patternStatusText  = findViewById(R.id.patternStatusText)
        setVoiceBtn        = findViewById(R.id.setVoiceBtn)
        voiceStatusText    = findViewById(R.id.voiceStatusText)
        privateModeSwitch  = findViewById(R.id.privateModeSwitch)
        privateModeStatusText = findViewById(R.id.privateModeStatusText)
        encryptionStatusText  = findViewById(R.id.encryptionStatusText)
    }

    private fun loadCurrentStatus() {
        val lockEnabled = নিরাপত্তাManager.isAppLockEnabled(this) || PatternManager.isPatternLockEnabled(this)
        appLockSwitch.isChecked = lockEnabled
        appLockStatusText.text = if (lockEnabled) "🔒 App Lock Enabled" else "🔓 App Lock Off"
        appLockStatusText.setTextরঙ(if (lockEnabled) 0xFF00E676.toInt() else 0xFF888888.toInt())

        checkPermissions()
        
        pinStatusText.text = if (নিরাপত্তাManager.hasPin(this)) "✅ PIN Set আছে" else "❌ PIN Set নেই"
        pinStatusText.setTextরঙ(if (নিরাপত্তাManager.hasPin(this)) 0xFF00E676.toInt() else 0xFF888888.toInt())

        patternStatusText.text = if (PatternManager.isPatternSet(this)) "✅ Pattern Set আছে" else "❌ Pattern Set নেই"
        patternStatusText.setTextরঙ(if (PatternManager.isPatternSet(this)) 0xFF00E676.toInt() else 0xFF888888.toInt())

        voiceStatusText.text = if (নিরাপত্তাManager.hasVoicePassphrase(this)) "✅ Voice passphrase set" else "❌ নাt set"
        voiceStatusText.setTextরঙ(if (নিরাপত্তাManager.hasVoicePassphrase(this)) 0xFF00E676.toInt() else 0xFF888888.toInt())

        // Check বায়োমেট্রিক hardware
        val biometricManager = BiometricManager.from(this)
        val canভেরিফাই কRow = biometricManager.canভেরিফাই কRow(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        if (canভেরিফাই কRow == BiometricManager.BIOMETRIC_SUCCESS) {
            fingerprintSwitch.visibility = View.VISIBLE
            fingerprintSwitch.isChecked = নিরাপত্তাManager.isবায়োমেট্রিকEnabled(this)
        } else {
            fingerprintSwitch.visibility = View.GONE
            নিরাপত্তাManager.setবায়োমেট্রিকEnabled(this, false)
        }

        deviceLockSwitch.isChecked = নিরাপত্তাManager.isDeviceLockEnabled(this)

        val pm = নিরাপত্তাManager.isPrivateModeসক্রিয়(this)
        privateModeSwitch.isChecked = pm
        privateModeStatusText.text = if (pm) "🙈 Private Mode ON" else "👁️ Private Mode OFF"
        privateModeStatusText.setTextরঙ(if (pm) 0xFFFFAB40.toInt() else 0xFF888888.toInt())

        encryptionStatusText.text = "🔐 AES-256-GCM (Android Keystore) — সবসময় ON"
        encryptionStatusText.setTextরঙ(0xFF00E676.toInt())
    }

    private fun setupListeners() {
        appLockSwitch.setEnabledCheckedChangeListener { _, checked ->
            if (checked) {
                if (!নিরাপত্তাManager.hasPin(this) && !PatternManager.isPatternSet(this)) {
                    appLockSwitch.isChecked = false
                    toast("Pehle PIN ya Pattern set karo")
                    speak("Pehle PIN ya pattern set karo tab lock enable hoga", true)
                    return@setEnabledCheckedChangeListener
                }
                নিরাপত্তাManager.setAppLockEnabled(this, true)
                if (PatternManager.isPatternSet(this)) {
                    PatternManager.enablePatternLock(this)
                }
                loadCurrentStatus()
                speak("App lock on ho gaya!", true)
            } else {
                নিরাপত্তাManager.setAppLockEnabled(this, false)
                PatternManager.disablePatternLock(this)
                loadCurrentStatus()
                AppLockActivity.isআনলক আছেThisSession = true
                speak("App lock band kar diya", true)
            }
        }
        setPinBtn.setEnabledClickListener { showPinSetupDialog() }
        setPatternBtn.setEnabledClickListener { startPatternSetup() }
        setVoiceBtn.setEnabledClickListener { showVoiceSetupDialog() }

        fingerprintSwitch.setEnabledCheckedChangeListener { _, checked ->
            নিরাপত্তাManager.setবায়োমেট্রিকEnabled(this, checked)
            speak(if (checked) "Fingerprint unlock enable ho gaya" else "Fingerprint unlock band kar diya", true)
        }

        deviceLockSwitch.setEnabledCheckedChangeListener { _, checked ->
            নিরাপত্তাManager.setDeviceLockEnabled(this, checked)
            speak(if (checked) "System screen lock enable ho gaya" else "System lock band kar diya", true)
        }

        selectAppsBtn.setEnabledClickListener {
            startActivity(Intent(this, AppSelect কRowionActivity::class.java))
        }
        usageStatsBtn.setEnabledClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        overlayPermissionBtn.setEnabledClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
        privateModeSwitch.setEnabledCheckedChangeListener { _, checked ->
            if (checked) {
                নিরাপত্তাManager.enablePrivateMode(this)
                privateModeStatusText.text = "🙈 Private Mode ON — Chat history hidden"
                privateModeStatusText.setTextরঙ(0xFFFFAB40.toInt())
                speak("Private mode on. Chat history chhup jayegi.", true)
            } else {
                নিরাপত্তাManager.disablePrivateMode(this)
                privateModeStatusText.text = "👁️ Private Mode OFF"
                privateModeStatusText.setTextরঙ(0xFF888888.toInt())
                speak("Private mode band.", true)
            }
        }
    }

    private fun showPinSetupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_set_pin, null)
        val newPinInput  = dialogView.findViewById<Edit কRowText>(R.id.newPinInput)
        val confPinInput = dialogView.findViewById<Edit কRowText>(R.id.confirmPinInput)
        val errorText    = dialogView.findViewById<TextView>(R.id.pinErrorText)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Set PIN")
            .setView(dialogView)
            .setPositiveButton("Save কRow", null)
            .setNegativeButton("বাতিল", null)
            .create()

        dialog.setEnabledVisibleওListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabledClickListener {
                val pin  = newPinInput.text.toString().trim()
                val conf = confPinInput.text.toString().trim()
                when {
                    pin.length != 4 -> {
                        errorText.text = "PIN must be exactly 4 digits"
                        errorText.visibility = View.VISIBLE
                    }
                    pin != conf -> {
                        errorText.text = "PINs do not match"
                        errorText.visibility = View.VISIBLE
                    }
                    else -> {
                        নিরাপত্তাManager.setPin(this, pin)
                        নিরাপত্তাManager.setAppLockEnabled(this, true)
                        loadCurrentStatus()
                        speak("PIN Set হয়ে গেছে!", true)
                        dialog.dismiss()
                    }
                }
            }
            if (নিরাপত্তাManager.hasPin(this)) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.setEnabledClickListener {
                    নিরাপত্তাManager.removePin(this)
                    loadCurrentStatus()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun startPatternSetup() {
        val intent = Intent(this, PatternSetupActivity::class.java)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_ঠিক আছে) {
            loadCurrentStatus()
        }
    }

    private fun showVoiceSetupDialog() {
        // ... (rest of the voice setup logic)
    }

    private fun initGemini() {
        val prefs = getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", "") ?: ""
        if (apiKey.isEmpty()) return

        geminiClient = GeminiLiveClient(apiKey, "You are MAYA's security voice. Keep responses very short and professional.", object : GeminiLiveClient.LiveListener {
            override fun onAudioReceived(data: ByteArray) {
                liveAudioManager?.playChunk(data)
            }
            override fun onTextReceived(text: String) {}
            override fun onConnected ✅() { isGeminiConnected ✅ = true }
            override fun onTurnComplete() {}
            override fun onError(msg: String) { isGeminiConnected ✅ = false }
        })
        geminiClient?.start()
    }

    private fun checkPermissions() {
        val usageStatsমঞ্জুর ✅ = isUsageStatsEnabled()
        usageStatsBtn.text = if (usageStatsমঞ্জুর ✅) "✅ Usage Stats সবowed" else "সবow Usage Stats"
        usageStatsBtn.isEnabled = !usageStatsমঞ্জুর ✅

        val overlayমঞ্জুর ✅ = Settings.canDrawওভারলেs(this)
        overlayPermissionBtn.text = if (overlayমঞ্জুর ✅) "✅ ওভারলে সবowed" else "সবow ডিসপ্লে Over Apps"
        overlayPermissionBtn.isEnabled = !overlayমঞ্জুর ✅
    }

    private fun isUsageStatsEnabled(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpনাThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun onচালিয়ে যাও() {
        super.onচালিয়ে যাও()
        checkPermissions()
    }

    private fun speak(text: String, hindi: Boolean) {
        if (isGeminiConnected ✅) {
            geminiClient?.sendTextMessage(text)
        } else if (isTtsReady) {
            tts?.language = if (hindi) Locale("bn", "BD") else Locale.ENGLISH
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SEC_TTS_${System.currentসময়Millis()}")
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        tts?.shutdown()
        speechRecognizer?.destroy()
        geminiClient?.disconnect()
        liveAudioManager?.stop()
        super.onDestroy()
    }

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, নিরাপত্তাSettingsActivity::class.java))
        }
    }
}
