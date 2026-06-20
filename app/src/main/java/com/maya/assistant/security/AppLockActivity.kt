package com.maya.assistant.security

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.বায়োমেট্রিকManager
import androidx.biometric.বায়োমেট্রিকPrompt
import androidx.core.content.ContextCompat
import com.maya.assistant.R
import com.maya.assistant.ai.GeminiLiveClient
import com.maya.assistant.utils.LiveAudioManager
import com.maya.assistant.ui.main.MainActivity
import java.util.Locale

/**
 * AppLockActivity — MAYA Complete Lock স্ক্রিন
 */
class AppLockActivity : AppCompatActivity(), TextToSpeech.চালুInitListener {

    private lateinit var tabPin: TextView
    private lateinit var tabPattern: TextView
    private lateinit var tabভয়েস: TextView
    private lateinit var tabFinger: TextView
    private lateinit var pinSection: View
    private lateinit var patternSection: View
    private lateinit var voiceSection: View
    private lateinit var fingerSection: View
    private lateinit var pinডিসপ্লে: TextView
    private lateinit var pinStatusText: TextView
    private lateinit var pinAttemptsText: TextView
    private lateinit var patternLockView: PatternLockView
    private lateinit var patternInstructionText: TextView
    private lateinit var patternAttemptsText: TextView
    private lateinit var voiceBtn: ImageButton
    private lateinit var voiceStatusText: TextView
    private lateinit var voiceInstructionText: TextView
    private lateinit var fingerBtn: ImageButton
    private lateinit var fingerStatusText: TextView
    private lateinit var lockoutওভারলে: LinearLayout
    private lateinit var lockoutসময়rText: TextView

    private var enteredPin = ""
    private var tts: TextToSpeech? = null
    private var geminiClient: GeminiLiveClient? = null
    private var liveAudioManager: LiveAudioManager? = null
    private var isGeminiসংযুক্ত ✅ = false
    private var isTtsReady = false
    private var speechRecognizer: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var lockoutRunnable: Runnable? = null
    private var currentTab = Tab.PATTERN

    enum class Tab { PIN, PATTERN, VOICE, FINGER }

    companion object {
        var isআনলক আছেThisSession = false
        fun launch(context: Context) {
            context.startActivity(Intent(context, AppLockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure Fingerprint tab is visible if enabled
        val isFingerprintচালু = নিরাপত্তাManager.isবায়োমেট্রিকচালু(this)
        
        val anyLockচালু = নিরাপত্তাManager.isAppLockচালু(this) || 
                            isFingerprintচালু || 
                            নিরাপত্তাManager.isDeviceLockচালু(this)
        
        if (!anyLockচালু) {
            finish(); return
        }
        setContentView(R.layout.activity_app_lock)
        initViews()
        
        // Force refresh Fingerprint tab visibility
        tabFinger.visibility = if (isFingerprintচালু) View.VISIBLE else View.GONE
        
        liveAudioManager = LiveAudioManager(this)
        initGemini()
        tts = TextToSpeech(this, this)
        setupTabs()
        setupPinPad()
        setupPattern()
        setupভয়েস()
        setupFinger()
        checkLockout()

        // Device lock auto-trigger if enabled
        if (নিরাপত্তাManager.isDeviceLockচালু(this)) {
            handler.postDelayed({ launchবায়োমেট্রিক(true) }, 500)
        }
        
        // ডিফল্ট to first available lock - Priority: Fingerprint > Pattern > PIN
        val defaultTab = when {
            isFingerprintচালু -> Tab.FINGER
            PatternManager.isPatternSet(this) -> Tab.PATTERN
            নিরাপত্তাManager.hasPin(this) -> Tab.PIN
            নিরাপত্তাManager.hasভয়েসPassphrase(this) -> Tab.VOICE
            else -> Tab.PIN
        }
        switchTab(defaultTab)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            tts?.language = Locale("bn", "BD")
            // Fallback in case WebSocket doesn't connect quickly
            handler.postDelayed({ 
                if (!isGeminiসংযুক্ত ✅) {
                    val mode = getSharedPreferences("maya_prefs", MODE_PRIVATE).getString("personality_mode", "gf") ?: "gf"
                    val greeting = if (mode == "gf") "আগে unlock করো জান, তারপর use করতে দিবো।" else "আগে unlock করো, তারপর continue করো।"
                    speak(greeting, true) 
                }
            }, 3000)
        }
    }

    private fun initViews() {
        tabPin     = findViewById(R.id.tabPin)
        tabPattern = findViewById(R.id.tabPattern)
        tabভয়েস   = findViewById(R.id.tabভয়েস)
        tabFinger  = findViewById(R.id.tabFinger)
        pinSection     = findViewById(R.id.pinSection)
        patternSection = findViewById(R.id.patternSection)
        voiceSection   = findViewById(R.id.voiceSection)
        fingerSection  = findViewById(R.id.fingerSection)
        pinডিসপ্লে      = findViewById(R.id.pinডিসপ্লে)
        pinStatusText   = findViewById(R.id.pinStatusText)
        pinAttemptsText = findViewById(R.id.pinAttemptsText)
        patternLockView        = findViewById(R.id.patternLockView)
        patternInstructionText = findViewById(R.id.patternInstructionText)
        patternAttemptsText    = findViewById(R.id.patternAttemptsText)
        voiceBtn             = findViewById(R.id.voiceUnlockBtn)
        voiceStatusText      = findViewById(R.id.voiceStatusText)
        voiceInstructionText = findViewById(R.id.voiceInstructionText)
        fingerBtn            = findViewById(R.id.fingerBtn)
        fingerStatusText     = findViewById(R.id.fingerStatusText)
        lockoutওভারলে   = findViewById(R.id.lockoutওভারলে)
        lockoutসময়rText = findViewById(R.id.lockoutসময়rText)
    }

    private fun setupTabs() {
        val hasPin = নিরাপত্তাManager.hasPin(this)
        val hasPattern = PatternManager.isPatternSet(this)
        val hasভয়েস = নিরাপত্তাManager.hasভয়েসPassphrase(this)
        val hasFinger = নিরাপত্তাManager.isবায়োমেট্রিকচালু(this)

        tabPin.visibility = if (hasPin) View.VISIBLE else View.GONE
        tabPattern.visibility = if (hasPattern) View.VISIBLE else View.GONE
        tabভয়েস.visibility = if (hasভয়েস) View.VISIBLE else View.GONE
        tabFinger.visibility = if (hasFinger) View.VISIBLE else View.GONE

        tabPin.setচালুClickListener     { switchTab(Tab.PIN) }
        tabPattern.setচালুClickListener { switchTab(Tab.PATTERN) }
        tabভয়েস.setচালুClickListener   { switchTab(Tab.VOICE) }
        tabFinger.setচালুClickListener  { switchTab(Tab.FINGER) }
    }

    private fun switchTab(tab: Tab) {
        currentTab = tab
        pinSection.visibility     = if (tab == Tab.PIN)     View.VISIBLE else View.GONE
        patternSection.visibility = if (tab == Tab.PATTERN) View.VISIBLE else View.GONE
        voiceSection.visibility   = if (tab == Tab.VOICE)   View.VISIBLE else View.GONE
        fingerSection.visibility  = if (tab == Tab.FINGER)  View.VISIBLE else View.GONE
        val pink = 0xFFE91E8C.toInt(); val grey = 0xFF888888.toInt()
        tabPin.setTextরঙ(if (tab == Tab.PIN) pink else grey)
        tabPattern.setTextরঙ(if (tab == Tab.PATTERN) pink else grey)
        tabভয়েস.setTextরঙ(if (tab == Tab.VOICE) pink else grey)
        tabFinger.setTextরঙ(if (tab == Tab.FINGER) pink else grey)

        if (tab == Tab.FINGER) launchবায়োমেট্রিক(false)
    }

    private fun setupPinPad() {
        val map = mapOf(R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8", R.id.btn9 to "9")
        map.forEach { (id, d) -> findViewById<Button>(id)?.setচালুClickListener { appendPin(d) } }
        findViewById<ImageButton>(R.id.backspaceBtn)?.setচালুClickListener { backspacePin() }
    }

    private fun appendPin(d: String) {
        if (enteredPin.length >= 4) return
        enteredPin += d
        pinডিসপ্লে.text = "●".repeat(enteredPin.length).padEnd(4, '○')
        if (enteredPin.length == 4) handler.postDelayed({ submitPin() }, 250)
    }

    private fun backspacePin() {
        if (enteredPin.isনাtEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            pinডিসপ্লে.text = "●".repeat(enteredPin.length).padEnd(4, '○')
        }
    }

    private fun submitPin() {
        val result = নিরাপত্তাManager.verifyPin(this, enteredPin)
        if (result is নিরাপত্তাManager.PinResult.CORRECT) {
            onসফল()
        } else {
            enteredPin = ""
            pinডিসপ্লে.text = "○○○○"
            when (result) {
                নিরাপত্তাManager.PinResult.NOT_SET -> onসফল()
                is নিরাপত্তাManager.PinResult.WRONG -> onPinWrong(result)
                is নিরাপত্তাManager.PinResult.LOCKED_OUT -> startLockout(result.secondsRemaining)
                else -> {}
            }
        }
    }

    private fun onPinWrong(r: নিরাপত্তাManager.PinResult.WRONG) {
        pinAttemptsText.text = "Wrong: ${r.attempts}/3"
        pinAttemptsText.setTextরঙ(if (r.attempts >= 2) 0xFFFF1744.toInt() else 0xFFFFAB40.toInt())
        shakeView(pinডিসপ্লে)
        pinStatusText.text = if (r.lockedOut) "🔒 লক আছে 30s" else "❌ ভুল PIN"
        pinStatusText.setTextরঙ(0xFFFF1744.toInt())
        if (r.lockedOut) startLockout(30)
        else handler.postDelayed({ pinStatusText.text = "Enter PIN"; pinStatusText.setTextরঙ(0xFFFFFFFF.toInt()) }, 1500)
        speakWrong(r.attempts, r.lockedOut, "pin")
    }

    private fun setupPattern() {
        patternInstructionText.text = "তোমার unlock pattern এঁকো"
        patternLockView.listener = object : PatternLockView.PatternListener {
            override fun onPatternশুরু করোed() { patternInstructionText.text = "আঁকতে থাকো..." }
            override fun onPatternComplete(pattern: List<Int>) {
                handler.postDelayed({ verifyPattern(pattern) }, 150)
            }
            override fun onPatternপরিষ্কার করোed() { patternInstructionText.text = "তোমার unlock pattern এঁকো" }
        }
    }

    private fun verifyPattern(pattern: List<Int>) {
        when (val r = PatternManager.verify(this, pattern)) {
            PatternManager.PatternResult.CORRECT -> { patternLockView.showসফল(); onসফল() }
            PatternManager.PatternResult.NOT_SET -> onসফল()
            PatternManager.PatternResult.TOO_SHORT -> {
                patternLockView.showসমস্যা()
                patternInstructionText.text = "অনেক ছোট — কমপক্ষে 4টা dots connect করো"
                speak("কমপক্ষে 4টা dots connect করো", true)
                handler.postDelayed({ patternLockView.clearPattern() }, 900)
            }
            is PatternManager.PatternResult.WRONG -> {
                patternLockView.showসমস্যা()
                val rem = PatternManager.getRemainingAttempts(this)
                patternAttemptsText.text = "Wrong — $rem attempts left"
                patternAttemptsText.setTextরঙ(if (r.attempts >= 3) 0xFFFF1744.toInt() else 0xFFFFAB40.toInt())
                if (r.lockedOut) startLockout(30)
                else handler.postDelayed({ patternLockView.clearPattern()
                    patternInstructionText.text = "তোমার unlock pattern এঁকো" }, 900)
                speakWrong(r.attempts, r.lockedOut, "pattern")
            }
            is PatternManager.PatternResult.LOCKED_OUT -> {
                patternLockView.showসমস্যা()
                startLockout(r.secondsRemaining)
            }
        }
    }

    private fun setupভয়েস() {
        val has = নিরাপত্তাManager.hasভয়েসPassphrase(this)
        voiceInstructionText.text = if (has) "মাইক ট্যাপ করো এবং passphrase বলো" else "ভয়েস passphrase সেট করা হয়নি"
        voiceBtn.alpha = if (has) 1f else 0.4f
        voiceBtn.isচালু = has
        voiceBtn.setচালুClickListener { if (has) startভয়েসListen() }
    }

    private fun setupFinger() {
        val biometricManager = বায়োমেট্রিকManager.from(this)
        val canAuth = biometricManager.canভেরিফাই করো(বায়োমেট্রিকManager.Authenticators.BIOMETRIC_STRONG or বায়োমেট্রিকManager.Authenticators.DEVICE_CREDENTIAL)
        
        if (canAuth == বায়োমেট্রিকManager.BIOMETRIC_SUCCESS || নিরাপত্তাManager.isবায়োমেট্রিকচালু(this)) {
            fingerBtn.setচালুClickListener { launchবায়োমেট্রিক(false) }
            tabFinger.visibility = View.VISIBLE
        } else {
            tabFinger.visibility = View.GONE
            Log.d("MAYA_LOCK", "বায়োমেট্রিক not available: $canAuth")
        }
    }

    private fun launchবায়োমেট্রিক(allowDeviceCredential: Boolean) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = বায়োমেট্রিকPrompt(this, executor, object : বায়োমেট্রিকPrompt.প্রমাণীকরণকলback() {
            override fun onপ্রমাণীকরণSucceeded(result: বায়োমেট্রিকPrompt.প্রমাণীকরণResult) {
                super.onপ্রমাণীকরণSucceeded(result)
                fingerStatusText.text = "✅ সফল!"
                onসফল()
            }
            override fun onপ্রমাণীকরণসমস্যা(errorCode: Int, errString: CharSequence) {
                super.onপ্রমাণীকরণসমস্যা(errorCode, errString)
                fingerStatusText.text = "❌ $errString"
                if (errorCode == বায়োমেট্রিকPrompt.ERROR_NEGATIVE_BUTTON) {
                    switchTab(Tab.PATTERN)
                }
            }
            override fun onপ্রমাণীকরণব্যর্থ() {
                super.onপ্রমাণীকরণব্যর্থ()
                fingerStatusText.text = "❌ প্রমাণীকরণ ব্যর্থ"
            }
        })

        val promptতথ্য = বায়োমেট্রিকPrompt.Promptতথ্য.Builder()
            .setTitle("MAYA Unlock")
            .setSubtitle("তোমার identity confirm করো")
            .apply {
                if (নিরাপত্তাManager.isDeviceLockচালু(this@AppLockActivity) || allowDeviceCredential) {
                    setসবowedAuthenticators(বায়োমেট্রিকManager.Authenticators.BIOMETRIC_STRONG or বায়োমেট্রিকManager.Authenticators.DEVICE_CREDENTIAL)
                } else {
                    setসবowedAuthenticators(বায়োমেট্রিকManager.Authenticators.BIOMETRIC_STRONG)
                    setNegativeButtonText("Pattern/PIN ব্যবহার করো")
                }
            }
            .build()

        biometricPrompt.authenticate(promptতথ্য)
    }

    private fun startভয়েসListen() {
        voiceStatusText.text = "🎙️ passphrase শুনছি..."
        voiceBtn.setImageResource(R.drawable.ic_mic_on)
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val spoken = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                voiceBtn.setImageResource(R.drawable.ic_mic_off)
                if (নিরাপত্তাManager.verifyভয়েসPassphrase(this@AppLockActivity, spoken)) {
                    voiceStatusText.text = "✅ Verified!"
                    onসফল()
                } else {
                    voiceStatusText.text = "❌ Passphrase মিলেনি"
                    shakeView(voiceBtn)
                    speakWrong(1, false, "voice")
                    handler.postDelayed({ voiceStatusText.text = "আবার try করতে মাইক ট্যাপ করো" }, 2000)
                }
            }
            override fun onসমস্যা(e: Int) {
                voiceBtn.setImageResource(R.drawable.ic_mic_off)
                voiceStatusText.text = "শুনতে পাচ্ছি না — আবার try করতে ট্যাপ করো"
                speak("বুঝতে পাচ্ছি না, আবার try করো", true)
            }
            override fun onReadyForSpeech(p: Bundle?) { voiceStatusText.text = "passphrase বলো..." }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(r: Float) {}
            override fun onBufferReceived(b: ByteArray?) {}
            override fun onEndOfSpeech() { voiceStatusText.text = "প্রসেস করছি..." }
            override fun onPartialResults(p: Bundle?) {}
            override fun onইভেন্ট(t: Int, b: Bundle?) {}
        })
        speechRecognizer?.startশুনছে…(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        })
    }

    private fun onসফল() {
        isআনলক আছেThisSession = true
        val prefs = getSharedPreferences("maya_prefs", MODE_PRIVATE)
        val name  = prefs.getString("user_name", "Jaan") ?: "Jaan"
        val mode  = prefs.getString("personality_mode", "gf") ?: "gf"
        val msg = when (mode) {
            "gf"           -> "লক খুলে গেছে! এসে গেছো $name! স্বাগতম back!"
            "professional" -> "লক খুলে গেছে। স্বাগতম back $name."
            else           -> "লক খুলে গেছে! Unlock successful।"
        }
        speak(msg, true)
        handler.postDelayed({ 
            finishAndসরাওটাস্ক()
        }, 1300)
    }

    private fun startLockout(seconds: Long) {
        lockoutওভারলে.visibility = View.VISIBLE
        var rem = seconds
        lockoutRunnable?.let { handler.removeকলbacks(it) }
        lockoutRunnable = object : Runnable {
            override fun run() {
                if (rem <= 0) {
                    lockoutওভারলে.visibility = View.GONE
                    PatternManager.resetAttempts(this@AppLockActivity)
                    speak("এখন আবার try করতে পারো", true)
                    return
                }
                lockoutসময়rText.text = "পর আবার try করো ${rem}s"
                rem--
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(lockoutRunnable!!)
    }

    private fun checkLockout() {
        val rem = PatternManager.getLockoutRemaining(this)
        if (rem > 0) startLockout(rem)
    }

    private fun speakWrong(attempts: Int, lockedOut: Boolean, type: String) {
        val mode = getSharedPreferences("maya_prefs", MODE_PRIVATE)
            .getString("personality_mode", "gf") ?: "gf"
        val rem = 3 - attempts
        val msg = when {
            lockedOut -> when (mode) {
                "gf"  -> "জান! ৩ বার ভুল? রুকো, 30 second ke liye lock kar diya!"
                "professional" -> "অনেক বেশি ভুল। ৩০ সেকেন্ড লক।"
                else  -> "অনেক ভুল। ৩০ সেকেন্ড রুকো।"
            }
            type == "voice" -> when (mode) {
                "gf"  -> "এই passphrase ছিল না জান! সঠিক বলে try করো।"
                "professional" -> "ভয়েস passphrase মিলেনি। আবার try করো।"
                else  -> "ভুল passphrase। আবার try করো।"
            }
            attempts == 1 -> when (mode) {
                "gf"  -> "আরে ভুল! মন দিয়ে দাও, $rem mauke bache hain."
                "professional" -> "ভুল। $rem বার আর চেষ্টা বাকি।"
                else  -> "ভুল! $rem বার আর try করতে পারো।"
            }
            attempts == 2 -> when (mode) {
                "gf"  -> "আবার ভুল?! আরেকটা ভুল হলে লক হয়ে যাবে!"
                "professional" -> "দ্বিতীয় ব্যর্থতা। Lockout এর আগে আরেকটা চেষ্টা।"
                else  -> "আবার ভুল! আরেকটা সুযোগ বাকি!"
            }
            else -> "ভুল! আবার try করো।"
        }
        speak(msg, true)
    }

    private fun initGemini() {
        val prefs = getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", "") ?: ""
        if (apiKey.isEmpty()) return

        val personality = prefs.getString("personality_mode", "gf") ?: "gf"
        val systemPrompt = when(personality) {
            "gf" -> "You are MAYA, the user's caring and emotional girlfriend. Keep security responses short, sweet, and in হাইnglish. Use words like 'jaan', 'babu' occasionally but keep it professional for security."
            "professional" -> "You are MAYA, a professional security assistant. Keep responses very short, formal and in ইংরেজি."
            else -> "You are MAYA, a friendly assistant. Keep responses short and balanced."
        }

        geminiClient = GeminiLiveClient(apiKey, systemPrompt, object : GeminiLiveClient.LiveListener {
            override fun onAudioReceived(data: ByteArray) { liveAudioManager?.playChunk(data) }
            override fun onTextReceived(text: String) {}
            override fun onসংযুক্ত ✅() { 
                isGeminiসংযুক্ত ✅ = true 
                Log.d("MAYA_LOCK", "Gemini WebSocket সংযুক্ত ✅ ✅")
                runচালুUiThread {
                    val mode = getSharedPreferences("maya_prefs", MODE_PRIVATE).getString("personality_mode", "gf") ?: "gf"
                    val greeting = if (mode == "gf") "আগে unlock করো জান, তারপর use করতে দিবো।" else "আগে unlock করো, তারপর continue করো।"
                    speak(greeting, true)
                }
            }
            override fun onTurnComplete() {}
            override fun onসমস্যা(msg: String) { 
                isGeminiসংযুক্ত ✅ = false 
                Log.e("MAYA_LOCK", "Gemini WebSocket সমস্যা: $msg")
            }
        })
        geminiClient?.start()
    }

    private fun speak(text: String, hindi: Boolean) {
        if (isGeminiসংযুক্ত ✅) {
            geminiClient?.sendTextমেসেজ(text)
        } else if (isTtsReady) {
            tts?.language = if (hindi) Locale("bn", "BD") else Locale.ENGLISH
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "LOCK_${সিস্টেম.currentসময়Millis()}")
        }
    }

    private fun shakeView(v: View) {
        v.animate().translationX(16f).setDuration(55).withEndAction {
            v.animate().translationX(-16f).setDuration(55).withEndAction {
                v.animate().translationX(8f).setDuration(45).withEndAction {
                    v.animate().translationX(0f).setDuration(45).start()
                }.start()
            }.start()
        }.start()
    }

    override fun onপিছনেPressed() { 
        speak("আগে unlock করো!", true)
        // If they try to back out, go to home screen so they can't access the app
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        lockoutRunnable?.let { handler.removeকলbacks(it) }
        tts?.shutdown()
        speechRecognizer?.destroy()
        geminiClient?.disconnect()
        liveAudioManager?.stop()
        super.onDestroy()
    }
}
