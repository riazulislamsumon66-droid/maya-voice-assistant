package com.maya.assistant.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.maya.assistant.R
import com.maya.assistant.ai.GeminiLiveClient
import com.maya.assistant.service.CallMonitorService
import com.maya.assistant.utils.LiveAudioManager
import java.util.Locale


class CallAssistantActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var callerNameText: TextView
    private lateinit var statusText: TextView
    private var waveformView: WaveformView? = null

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var speechRecognizer: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var callerName = "অচেনা কলার"
    private var phoneNumber = ""
    private var userName = "স্যার"
    private var personality = "gf"
    private var isWhatsAppCall = false

    // State management
    private var isDecisionMade = false
    private var isListening = false
    private var announcementPlayed = false
    private var isSpeaking = false
    private var isCallAnswered = false

    private lateinit var liveClient: GeminiLiveClient
    private lateinit var liveAudioManager: LiveAudioManager
    private var isLiveConnected = false

    private val TAG = "MAYA_CALL_UI"

    // Broadcast receiver: call cut হলে finish
    private val callStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                CallMonitorService.ACTION_CALL_ENDED -> {
                    Log.d(TAG, "Call ended → closing assistant UI")
                    if (!isDecisionMade) {
                        safeFinish()
                    }
                }
                CallMonitorService.ACTION_CALL_ACTIVE -> {
                    Log.d(TAG, "Call active → closing assistant UI")
                    isCallAnswered = true
                    safeFinish()
                }
                CallMonitorService.ACTION_CALL_RINGING -> {
                    Log.d(TAG, "Call ringing received")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock screen এও দেখাও
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        setContentView(R.layout.activity_call_assistant)

        // Intent থেকে data নাও
        callerName  = intent.getStringExtra("CALLER_NAME") ?: "অচেনা কলার"
        phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: ""
        userName    = intent.getStringExtra("USER_NAME") ?: getPrefsValue("user_name", "স্যার")
        personality = intent.getStringExtra("PERSONALITY") ?: getPrefsValue("personality_mode", "gf")
        isWhatsAppCall = intent.getBooleanExtra("IS_WHATSAPP_CALL", false)

        Log.d(TAG, "CallAssistant started: caller=$callerName, whatsapp=$isWhatsAppCall, personality=$personality")

        initViews()

        // Initialize Live Audio Manager FIRST
        liveAudioManager = LiveAudioManager(this)

        // Setup Gemini Live FIRST
        setupGeminiLive()

        // TTS as fallback only
        tts = TextToSpeech(this, this)

        // Broadcast register করো
        val filter = IntentFilter().apply {
            addAction(CallMonitorService.ACTION_CALL_ENDED)
            addAction(CallMonitorService.ACTION_CALL_ACTIVE)
            addAction(CallMonitorService.ACTION_CALL_RINGING)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(callStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(callStateReceiver, filter)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Receiver register failed: ${e.message}")
        }

        // Phone state monitor
        startCallStateMonitor()
    }

    private fun initViews() {
        callerNameText = findViewById(R.id.callerNameText)
        statusText     = findViewById(R.id.callStatusText)
        waveformView   = findViewById<WaveformView?>(R.id.callWaveform)

        callerNameText.text = callerName
        statusText.text = "MAYA তৈরি হচ্ছে..."
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("bn", "BD"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale("hi", "IN")
            }
            isTtsReady = true
        } else {
            Log.e(TAG, "TTS init failed")
        }
    }

    private fun startAnnouncement() {
        if (isDecisionMade || announcementPlayed) {
            if (!isDecisionMade) startListening()
            return
        }

        announcementPlayed = true
        val msg = buildAnnouncementText()

        statusText.text = "ঘোষণা করছি..."
        Log.d(TAG, "Announcing via WebSocket: $msg")

        speakViaWebSocket(msg)

        val estimatedDuration = (msg.length * 80L).coerceIn(3000L, 8000L)
        handler.postDelayed({
            if (!isDecisionMade && !isSpeaking) startListening()
        }, estimatedDuration)
    }

    private fun buildAnnouncementText(): String {
        val isKnownContact = !isNumberLike(callerName)

        return if (isWhatsAppCall) {
            when (personality) {
                "gf" -> when {
                    isKnownContact ->
                        "$userName, $callerName এর WhatsApp call আসছে। উঠাবো নাকি না?"
                    else ->
                        "$userName, একটা অচেনা নম্বর থেকে WhatsApp call আসছে। উঠাবো নাকি না?"
                }
                "professional" -> when {
                    isKnownContact ->
                        "$userName, $callerName এর WhatsApp call আসছে। Answer করবো নাকি decline?"
                    else ->
                        "$userName, অচেনা WhatsApp caller। Answer করবো নাকি decline?"
                }
                else ->
                    "$userName, $callerName এর WhatsApp call আসছে। কী করবো?"
            }
        } else {
            when (personality) {
                "gf" -> when {
                    isKnownContact ->
                        "$userName, $callerName এর call আসছে। উঠাবো নাকি না?"
                    else ->
                        "$userName, একটা অচেনা নম্বর থেকে call আসছে। উঠাবো নাকি না?"
                }
                "professional" -> when {
                    isKnownContact ->
                        "$userName, incoming call from $callerName। Answer করবো নাকি decline?"
                    else ->
                        "$userName, unknown caller। Answer করবো নাকি decline?"
                }
                else ->
                    "$userName, $callerName এর call আসছে। কী করবো?"
            }
        }
    }

    private fun setupGeminiLive() {
        val prefs = getSharedPreferences("maya_prefs", MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", "") ?: ""
        if (apiKey.isEmpty()) {
            Log.e(TAG, "No API key found")
            return
        }

        val callType = if (isWhatsAppCall) "WhatsApp call" else "phone call"

        val prompt = """
            You are MAYA, a caring AI assistant for $userName. Personality: $personality.

            SITUATION: Incoming $callType from $callerName.
            
            TASKS:
            1. Announce the incoming call naturally in Bangla (বাংলা).
            2. If the user asks questions, answer them naturally in Bangla.
            3. Guide the user to say "উঠাও" (Answer) or "না/রিজেক্ট" (Reject) to handle the call.

            STYLE: Natural, warm, human-like tone. Respond in Bangla (বাংলা) with some English words mixed (Banglish). Keep it concise.
        """.trimIndent()

        liveClient = GeminiLiveClient(apiKey, prompt, object : GeminiLiveClient.LiveListener {
            override fun onAudioReceived(data: ByteArray) {
                isSpeaking = true
                liveAudioManager.playChunk(data)
                runOnUiThread {
                    statusText.text = "বলছি... 💬"
                    waveformView?.startAnimation()
                }
            }

            override fun onTextReceived(text: String) {
                Log.d(TAG, "Gemini Text: $text")
            }

            override fun onConnected() {
                isLiveConnected = true
                Log.d(TAG, "Gemini Live Connected ✅")
                handler.postDelayed({ startAnnouncement() }, 500)
            }

            override fun onTurnComplete() {
                isSpeaking = false
                runOnUiThread {
                    waveformView?.stopAnimation()
                    if (!isDecisionMade) startListening()
                }
            }

            override fun onError(msg: String) {
                isLiveConnected = false
                Log.e(TAG, "Gemini Error: $msg")
                handler.postDelayed({
                    if (!announcementPlayed) startAnnouncement()
                }, 500)
            }
        })
        liveClient.start()
    }

    private fun speakViaWebSocket(text: String) {
        if (isLiveConnected) {
            isSpeaking = true
            liveClient.sendTextMessage(text)
            Log.d(TAG, "Speaking via WebSocket: $text")
            return
        }
        speakTTS(text)
    }

    private fun speakTTS(text: String) {
        Log.d(TAG, "Robotic TTS skipped for: $text")
    }

    private fun startListening() {
        if (isDecisionMade || isListening || isSpeaking) return
        isListening = true

        statusText.text = "শুনছি... (বলো: উঠাও / রিজেক্ট)"
        Log.d(TAG, "Starting voice recognition")

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e(TAG, "Speech recognition not available")
            handler.postDelayed({ repeatAnnouncement() }, 1000)
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                isListening = false
                val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spoken = texts?.firstOrNull()?.lowercase()?.trim() ?: ""
                Log.d(TAG, "User said: '$spoken'")

                if (spoken.isEmpty()) {
                    if (!isDecisionMade) repeatAnnouncement()
                    return
                }
                processCommand(spoken)
            }

            override fun onError(errorCode: Int) {
                isListening = false
                val errorMsg = when (errorCode) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "NO_MATCH"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "TIMEOUT"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "BUSY"
                    else -> "ERROR_$errorCode"
                }
                Log.w(TAG, "Speech error: $errorMsg")

                if (!isDecisionMade) {
                    handler.postDelayed({ if (!isDecisionMade) startListening() }, 1500)
                }
            }

            override fun onReadyForSpeech(p0: Bundle?) {
                statusText.text = "বলো... 🎙️"
            }
            override fun onBeginningOfSpeech() {
                statusText.text = "শুনছি... 👂"
                waveformView?.startAnimation()
            }
            override fun onRmsChanged(rmsdB: Float) {
                waveformView?.setAmplitude(rmsdB)
            }
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {
                statusText.text = "প্রসেস করছি..."
                waveformView?.stopAnimation()
            }
            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            // Multi-language support: Bangla + Hindi + English
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("bn-BD", "hi-IN", "en-US", "en-IN"))
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000)
        }
        speechRecognizer?.startListening(intent)
    }

    private fun processCommand(spoken: String) {
        val lower = spoken.lowercase().trim()
        Log.d(TAG, "Processing command: '$lower'")

        // ANSWER keywords — Bangla + Hindi + English
        val answerWords = listOf(
            // Bangla
            "উঠাও", "উঠা", "উঠাবো", "উঠান", "answer", "pick", "receive", "accept",
            "হ্যাঁ", "হাঁ", "ঠিক", "ঠিক আছে", "ওকে", "নে", "নেও",
            "ফোন উঠাও", "উঠাও ফোন", "রিসিভ করো", "নিয়ে নাও",
            "কথা বলো", "হ্যালো", "বলো", "শোনাও",
            // Hindi
            "utha", "uthao", "uthau", "haan", "han", "theek", "theek hai", "lo",
            "phone uthao", "uthao phone", "receive karo", "le lo", "uthane",
            "baat karo", "halo", "bolo", "sunao",
            // English
            "answer", "pick up", "receive", "accept", "yes", "yeah", "yep",
            "sure", "okay", "ok", "go ahead", "talk", "speak"
        )

        // REJECT keywords — Bangla + Hindi + English
        val rejectWords = listOf(
            // Bangla
            "না", "নাহ", "রিজেক্ট", "কাট", "কেটে দাও", "বন্ধ করো", "ড্রপ",
            "না উঠাবো", "ছেড়ে দাও", "ইগনোর", "কেট দাও",
            "বিস্তর আছি", "পরে", "রাখ", "রোকো",
            // Hindi
            "reject", "decline", "cut", "kat", "kaat", "nahi", "na", "no",
            "mat", "band karo", "drop", "dismiss", "nahi uthana",
            "nahi uthao", "chhod do", "ignore", "kat do", "kaat do",
            "busy hoon", "baad mein", "later", "ruk", "rukna",
            // English
            "reject", "decline", "cut", "drop", "dismiss", "no", "nope", "nah",
            "don't", "dont", "stop", "end", "busy", "later", "not now"
        )

        val hasAnswer = answerWords.any { lower.contains(it) }
        val hasReject = rejectWords.any { lower.contains(it) }

        val answerIndex = answerWords.map { lower.indexOf(it) }.filter { it >= 0 }.minOrNull() ?: Int.MAX_VALUE
        val rejectIndex = rejectWords.map { lower.indexOf(it) }.filter { it >= 0 }.minOrNull() ?: Int.MAX_VALUE

        val isAnswer = hasAnswer && (!hasReject || answerIndex < rejectIndex)
        val isReject = hasReject && (!hasAnswer || rejectIndex < answerIndex)

        Log.d(TAG, "Decision: isAnswer=$isAnswer, isReject=$isReject, answerPos=$answerIndex, rejectPos=$rejectIndex")

        when {
            isAnswer && !isReject -> performAnswer()
            isReject -> performReject()
            else -> {
                if (isLiveConnected) {
                    Log.d(TAG, "Passing unrecognized command to Gemini: $spoken")
                    isSpeaking = true
                    runOnUiThread { statusText.text = "ভাবছি... 🤔" }
                    liveClient.sendTextMessage(spoken)
                } else {
                    val confusion = when (personality) {
                        "gf"  -> "জান, বুঝলাম না। 'উঠাও' বা 'না' বলো।"
                        else  -> "Please say 'Answer' or 'Reject'."
                    }
                    speakViaWebSocket(confusion)
                    handler.postDelayed({ if (!isDecisionMade) repeatAnnouncement() }, 4000)
                }
            }
        }
    }

    private fun performAnswer() {
        if (isDecisionMade) return
        isDecisionMade = true
        isCallAnswered = true
        stopListening()

        val confirmMsg = when (personality) {
            "gf"  -> "জি $userName, call উঠাচ্ছি! 📞"
            else  -> "Answering the call, $userName."
        }
        speakViaWebSocket(confirmMsg)
        statusText.text = "উঠাচ্ছি... 📞"
        Log.d(TAG, "=== ANSWERING CALL ===")

        handler.postDelayed({
            var success = false

            if (isWhatsAppCall) {
                success = answerWhatsAppCall()
            } else {
                success = answerNormalCall()
            }

            if (!success) {
                speakViaWebSocket("সরি $userName, call উঠাতে পারিনি")
            }

            handler.postDelayed({ safeFinish() }, 2000)
        }, 1500)
    }

    private fun answerNormalCall(): Boolean {
        var success = false

        try {
            if (checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                val telecom = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    @Suppress("DEPRECATION")
                    telecom.acceptRingingCall()
                    Log.d(TAG, "✅ Call accepted via TelecomManager.acceptRingingCall()")
                    success = true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "TelecomManager answer failed: ${e.message}")
        }

        if (!success) {
            try {
                val answered = com.maya.assistant.service.SmartAccessibilityEngine.click(
                    text = "Answer"
                ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                    contentDesc = "Answer"
                ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                    text = "Accept"
                ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                    contentDesc = "Accept"
                )
                Log.d(TAG, "Accessibility answer: $answered")
                success = answered
            } catch (e: Exception) {
                Log.e(TAG, "Accessibility answer failed: ${e.message}")
            }
        }

        return success
    }

    private fun answerWhatsAppCall(): Boolean {
        var success = false

        try {
            success = com.maya.assistant.service.SmartAccessibilityEngine.click(
                text = "Answer"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                contentDesc = "Answer"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                text = "Accept"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                contentDesc = "Accept call"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                id = "com.whatsapp:id/incoming_call_answer"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                id = "com.whatsapp.w4b:id/incoming_call_answer"
            )

            Log.d(TAG, "WhatsApp call answer: $success")
        } catch (e: Exception) {
            Log.e(TAG, "WhatsApp answer failed: ${e.message}")
        }

        return success
    }

    private fun performReject() {
        if (isDecisionMade) return
        isDecisionMade = true
        stopListening()

        val confirmMsg = when (personality) {
            "gf"  -> "ঠিক আছে $userName, call reject করে দিলাম। ❌"
            else  -> "Call declined, $userName."
        }
        speakViaWebSocket(confirmMsg)
        statusText.text = "রিজেক্ট করছি... ❌"
        Log.d(TAG, "=== REJECTING CALL ===")

        handler.postDelayed({
            var success = false

            if (isWhatsAppCall) {
                success = rejectWhatsAppCall()
            } else {
                success = rejectNormalCall()
            }

            if (!success) {
                speakViaWebSocket("সরি $userName, call reject হয়নি")
            }

            handler.postDelayed({ safeFinish() }, 2000)
        }, 1500)
    }

    private fun rejectNormalCall(): Boolean {
        var success = false

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                    val telecom = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    @Suppress("DEPRECATION")
                    telecom.endCall()
                    Log.d(TAG, "✅ Call rejected via TelecomManager.endCall()")
                    success = true
                } else {
                    Log.w(TAG, "Permission ANSWER_PHONE_CALLS missing")
                }
            } else {
                Log.w(TAG, "TelecomManager.endCall() requires API 28")
            }
        } catch (e: Exception) {
            Log.e(TAG, "TelecomManager reject failed: ${e.message}")
        }

        if (!success) {
            try {
                val rejected = com.maya.assistant.service.SmartAccessibilityEngine.click(
                    text = "Decline"
                ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                    text = "Reject"
                ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                    contentDesc = "Decline"
                ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                    contentDesc = "Reject call"
                )
                Log.d(TAG, "Accessibility reject: $rejected")
                success = rejected
            } catch (e: Exception) {
                Log.e(TAG, "Accessibility reject failed: ${e.message}")
            }
        }

        return success
    }

    private fun rejectWhatsAppCall(): Boolean {
        var success = false

        try {
            success = com.maya.assistant.service.SmartAccessibilityEngine.click(
                text = "Decline"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                contentDesc = "Decline"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                text = "Reject"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                id = "com.whatsapp:id/incoming_call_decline"
            ) || com.maya.assistant.service.SmartAccessibilityEngine.click(
                id = "com.whatsapp.w4b:id/incoming_call_decline"
            )

            Log.d(TAG, "WhatsApp call reject: $success")
        } catch (e: Exception) {
            Log.e(TAG, "WhatsApp reject failed: ${e.message}")
        }

        return success
    }

    private fun repeatAnnouncement() {
        announcementPlayed = false
        startAnnouncement()
    }

    private fun stopListening() {
        isListening = false
        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {}
        waveformView?.stopAnimation()
    }

    private fun startCallStateMonitor() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val monitor = object : Runnable {
            override fun run() {
                if (isDecisionMade || isCallAnswered) return
                if (tm.callState == TelephonyManager.CALL_STATE_IDLE && !isWhatsAppCall) {
                    Log.d(TAG, "Phone idle — closing call assistant")
                    safeFinish()
                    return
                }
                handler.postDelayed(this, 2000)
            }
        }
        handler.postDelayed(monitor, 3000)
    }

    private fun safeFinish() {
        if (!isFinishing && !isDestroyed) {
            try { speechRecognizer?.cancel() } catch (_: Exception) {}
            try { speechRecognizer?.destroy() } catch (_: Exception) {}

            isDecisionMade = false
            isListening = false
            announcementPlayed = false
            isSpeaking = false

            finish()
        }
    }

    private fun isNumberLike(str: String) =
        str.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }

    private fun getPrefsValue(key: String, default: String): String {
        return getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
            .getString(key, default) ?: default
    }

    override fun onBackPressed() {
        speakViaWebSocket("আগে decision নাও!")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        try { unregisterReceiver(callStateReceiver) } catch (_: Exception) {}
        tts?.shutdown()
        if (::liveClient.isInitialized) liveClient.disconnect()
        liveAudioManager.stop()
        speechRecognizer?.destroy()

        isDecisionMade = false
        isListening = false
        announcementPlayed = false
        isSpeaking = false
        isCallAnswered = false

        Log.d(TAG, "CallAssistantActivity destroyed")
    }
}
