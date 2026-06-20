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
import com.maya.assistant.service.কলMonitorService
import com.maya.assistant.utils.LiveAudioManager
import java.util.Locale



class কলঅ্যাসিস্ট্যান্টActivity : AppCompatActivity(), TextToSpeech.চালুInitListener {

    private lateinit var callerনামText: TextView
    private lateinit var statusText: TextView
    private var waveformView: WaveformView? = null

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var speechRecognizer: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var callerনাম = "অজানা কলer"
    private var phoneNumber = ""
    private var userনাম = "Sir"
    private var personality = "gf"
    private var isWhatsAppকল = false

    // State management — FIXED: Proper flags
    private var isDecisionMade = false
    private var isশুনছে… = false
    private var announcementPlayed = false
    private var isবলছে… = false
    private var isকলAnswered = false

    private lateinit var liveClient: GeminiLiveClient
    private lateinit var liveAudioManager: LiveAudioManager
    private var isLiveসংযুক্ত ✅ = false

    private val TAG = "MAYA_CALL_UI"

    // Broadcast receiver: call cut hone par finish
    private val callStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                কলMonitorService.ACTION_CALL_ENDED -> {
                    Log.d(TAG, "কল ended → closing assistant UI")
                    if (!isDecisionMade) {
                        // কল ended before user decision — just finish
                        safeFinish()
                    }
                }
                কলMonitorService.ACTION_CALL_ACTIVE -> {
                    Log.d(TAG, "কল active → closing assistant UI")
                    isকলAnswered = true
                    safeFinish()
                }
                কলMonitorService.ACTION_CALL_RINGING -> {
                    Log.d(TAG, "কল ringing received")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock screen pe bhi dikhao
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setদেখাওWhenলক আছে(true)
            setTurnস্ক্রিনচালু(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        setContentView(R.layout.activity_call_assistant)

        // Intent se data lo — FIXED: WhatsApp call detection
        callerনাম  = intent.getStringExtra("CALLER_NAME") ?: "অজানা কলer"
        phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: ""
        userনাম    = intent.getStringExtra("USER_NAME") ?: getPrefsValue("user_name", "Sir")
        personality = intent.getStringExtra("PERSONALITY") ?: getPrefsValue("personality_mode", "gf")
        isWhatsAppকল = intent.getBooleanExtra("IS_WHATSAPP_CALL", false)

        Log.d(TAG, "কলঅ্যাসিস্ট্যান্ট started: caller=$callerনাম, whatsapp=$isWhatsAppকল, personality=$personality")

        initViews()

        // Initialize Live Audio Manager FIRST (before Gemini)
        liveAudioManager = LiveAudioManager(this)

        // Setup Gemini Live FIRST for natural voice
        setupGeminiLive()

        // TTS as fallback only
        tts = TextToSpeech(this, this)

        // Broadcast register karo
        val filter = IntentFilter().apply {
            addAction(কলMonitorService.ACTION_CALL_ENDED)
            addAction(কলMonitorService.ACTION_CALL_ACTIVE)
            addAction(কলMonitorService.ACTION_CALL_RINGING)
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

        // ফোন state monitor
        startকলStateMonitor()
    }

    private fun initViews() {
        callerনামText = findViewById(R.id.callerনামText)
        statusText     = findViewById(R.id.callStatusText)
        waveformView   = findViewById<WaveformView?>(R.id.callWaveform)

        callerনামText.text = callerনাম
        statusText.text = "MAYA preparing..."
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setভাষা(Locale("bn", "BD"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.ENGLISH
            }
            isTtsReady = true
        } else {
            Log.e(TAG, "TTS init failed")
        }
    }

    /**
     * ✅ FIXED: WebSocket natural voice for announcement
     * না more robotic TTS — uses Gemini Live audio streaming
     */
    private fun startAnnouncement() {
        if (isDecisionMade || announcementPlayed) {
            if (!isDecisionMade) startশুনছে…()
            return
        }

        announcementPlayed = true
        val msg = buildAnnouncementText()

        statusText.text = "Announcing..."
        Log.d(TAG, "Announcing via WebSocket: $msg")

        // ✅ Use WebSocket natural voice instead of TTS
        speakViaWebSocket(msg)

        // Announcement khatam hone ke baad listen karo
        // WebSocket audio duration estimate
        val estimatedDuration = (msg.length * 80L).coerceIn(3000L, 8000L)
        handler.postDelayed({
            if (!isDecisionMade && !isবলছে…) startশুনছে…()
        }, estimatedDuration)
    }

    private fun buildAnnouncementText(): String {
        val isKnownContact = !isNumberLike(callerনাম)

        return if (isWhatsAppকল) {
            // WhatsApp call announcement
            when (personality) {
                "gf" -> when {
                    isKnownContact ->
                        "$userনাম, $callerনাম ka WhatsApp call aa raha hai. Uthana hai ya nahi?"
                    else ->
                        "$userনাম, ek anjaan number se WhatsApp call aa rahi hai. Uthana hai ya nahi?"
                }
                "professional" -> when {
                    isKnownContact ->
                        "$userনাম, incoming WhatsApp call from $callerনাম. Should I answer or decline?"
                    else ->
                        "$userনাম, unknown WhatsApp caller. Should I answer or decline?"
                }
                else ->
                    "$userনাম, $callerনাম ka WhatsApp call aa raha hai. Kya karna hai?"
            }
        } else {
            // নাrmal call announcement
            when (personality) {
                "gf" -> when {
                    isKnownContact ->
                        "$userনাম, $callerনাম ka call aa raha hai. Uthana hai ya nahi?"
                    else ->
                        "$userনাম, ek anjaan number se call aa rahi hai. Uthana hai ya nahi?"
                }
                "professional" -> when {
                    isKnownContact ->
                        "$userনাম, incoming call from $callerনাম. Should I answer or decline?"
                    else ->
                        "$userনাম, unknown caller. Should I answer or decline?"
                }
                else ->
                    "$userনাম, $callerনাম ka call aa raha hai. Kya karna hai?"
            }
        }
    }

    /**
     * ✅ FIXED: Setup Gemini Live with proper prompt for call scenario
     */
    private fun setupGeminiLive() {
        val prefs = getSharedPreferences("maya_prefs", MODE_PRIVATE)
        val apiKey = prefs.getString("api_key", "") ?: ""
        if (apiKey.isEmpty()) {
            Log.e(TAG, "না API key found")
            return
        }

        val callType = if (isWhatsAppকল) "WhatsApp call" else "phone call"

        val prompt = """
            You are MAYA, a caring AI assistant for $userনাম. ব্যক্তিগতity: $personality.

            SITUATION: Incoming $callType from $callerনাম.
            
            TASKS:
            1. Announce the incoming call naturally in হাইnglish.
            2. If the user asks questions (e.g., "Who is calling?", "What should I do?"), answer them naturally and helpfully.
            3. Guide the user to say "Answer" or "Reject" to handle the call.

            STYLE: Natural, warm, human-like tone. Respond in হাইnglish. Keep it concise.
        """.trimIndent()

        liveClient = GeminiLiveClient(apiKey, prompt, object : GeminiLiveClient.LiveListener {
            override fun onAudioReceived(data: ByteArray) {
                isবলছে… = true
                liveAudioManager.playChunk(data)
                runচালুUiThread {
                    statusText.text = "বলছে…... 💬"
                    waveformView?.startAnimation()
                }
            }

            override fun onTextReceived(text: String) {
                Log.d(TAG, "Gemini Text: $text")
            }

            override fun onসংযুক্ত ✅() {
                isLiveসংযুক্ত ✅ = true
                Log.d(TAG, "Gemini Live সংযুক্ত ✅ ✅")
                // শুরু করো announcement once connected
                handler.postDelayed({ startAnnouncement() }, 500)
            }

            override fun onTurnComplete() {
                isবলছে… = false
                runচালুUiThread {
                    waveformView?.stopAnimation()
                    if (!isDecisionMade) startশুনছে…()
                }
            }

            override fun onসমস্যা(msg: String) {
                isLiveসংযুক্ত ✅ = false
                Log.e(TAG, "Gemini সমস্যা: $msg")
                // Fallback to TTS
                handler.postDelayed({
                    if (!announcementPlayed) startAnnouncement()
                }, 500)
            }
        })
        liveClient.start()
    }

    /**
     * ✅ NEW: Speak via WebSocket (natural voice)
     */
    private fun speakViaWebSocket(text: String) {
        if (isLiveসংযুক্ত ✅) {
            isবলছে… = true
            liveClient.sendTextমেসেজ(text)
            Log.d(TAG, "বলছে… via WebSocket: $text")
            return
        }
        // Fallback to TTS
        speakTTS(text)
    }

    /**
     * Fallback TTS (বন্ধ - only WebSocket natural voice allowed)
     */
    private fun speakTTS(text: String) {
        // Robotic TTS removed as per user request
        Log.d(TAG, "Robotic TTS skipped for: $text")
    }

    private fun startশুনছে…() {
        if (isDecisionMade || isশুনছে… || isবলছে…) return
        isশুনছে… = true

        statusText.text = "Sun rahi hoon... (bolo: Uthao / Reject)"
        Log.d(TAG, "শুরু করোing voice recognition")

        if (!SpeechRecognizer.isRecognitionপাওয়া যাচ্ছে(this)) {
            Log.e(TAG, "Speech recognition not available")
            handler.postDelayed({ repeatAnnouncement() }, 1000)
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                isশুনছে… = false
                val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spoken = texts?.firstOrNull()?.lowercase()?.trim() ?: ""
                Log.d(TAG, "ব্যবহারকারী said: '$spoken'")

                if (spoken.isEmpty()) {
                    if (!isDecisionMade) repeatAnnouncement()
                    return
                }
                processCommand(spoken)
            }

            override fun onসমস্যা(errorCode: Int) {
                isশুনছে… = false
                val errorMsg = when (errorCode) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "NO_MATCH"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "TIMEOUT"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "BUSY"
                    else -> "ERROR_$errorCode"
                }
                Log.w(TAG, "Speech error: $errorMsg")

                if (!isDecisionMade) {
                    handler.postDelayed({ if (!isDecisionMade) startশুনছে…() }, 1500)
                }
            }

            override fun onReadyForSpeech(p0: Bundle?) {
                statusText.text = "Bol do... 🎙️"
            }
            override fun onBeginningOfSpeech() {
                statusText.text = "Sun rahi hoon... 👂"
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
            override fun onইভেন্ট(p0: Int, p1: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            // ✅ Multi-language support
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("hi-IN", "bn-BD", "en-IN", "bn-IN", "ta-IN", "te-IN", "mr-IN", "gu-IN", "kn-IN", "ml-IN", "pa-IN", "ur-IN"))
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000)
        }
        speechRecognizer?.startশুনছে…(intent)
    }

    /**
     * ✅ FIXED: Proper answer/reject detection with multi-language support
     */
    private fun processCommand(spoken: String) {
        val lower = spoken.lowercase().trim()
        Log.d(TAG, "Processing command: '$lower'")

        // ANSWER keywords — MULTI-LANGUAGE SUPPORT
        val answerWords = listOf(
            // হিন্দি/হাইnglish
            "utha", "uthao", "uthau", "answer", "pick", "receive", "accept",
            "haan", "han", "yes", "theek", "theek hai", "okay", "ok", "lo",
            "phone uthao", "uthao phone", "receive karo", "le lo", "uthane",
            "baat karo", "hello", "halo", "bolo", "sunao",
            // ইংরেজি
            "answer", "pick up", "pick", "receive", "accept", "yes", "yeah", "yep",
            "sure", "okay", "ok", "go ahead", "talk", "speak",
            // Tamil
            "eduthukko", "pesu", "sari",
            // Telugu
            "attuko", "matladu", "sare",
            // Bengali
            "tulun", "kotha bolo", "thik",
            // Marathi
            "uthav", "bol", "thik",
            // Gujarati
            "uthavo", "bolo", "thik",
            // Kannada
            "ettuko", "matadu", "sari",
            // Malayalam
            "eduthu", "samsarik", "sheri",
            // Punjabi
            "chukko", "gall karo", "thik",
            // Urdu
            "uthavo", "baat karo", "thik"
        )

        // REJECT keywords — MULTI-LANGUAGE SUPPORT
        val rejectWords = listOf(
            // হিন্দি/হাইnglish
            "reject", "decline", "cut", "kat", "kaat", "nahi", "na", "no",
            "mat", "band karo", "drop", "dismiss", "nahi uthana",
            "nahi uthao", "chhod do", "ignore", "kat do", "kaat do",
            "busy hoon", "baad mein", "later", "ruk", "rukna",
            // ইংরেজি
            "reject", "decline", "cut", "drop", "dismiss", "no", "nope", "nah",
            "don't", "dont", "stop", "end", "busy", "later", "not now",
            // Tamil
            "venam", "cut pannu", "apram",
            // Telugu
            "vaddu", "cut chey", "tarvata",
            // Bengali
            "na", "kat", " pore",
            // Marathi
            "nako", "kat", "nanter",
            // Gujarati
            "nahi", "kat", "pachi",
            // Kannada
            "beda", "kat", "melake",
            // Malayalam
            "venda", "kat", "pinnne",
            // Punjabi
            "nahi", "kat", "baad",
            // Urdu
            "nahi", "kat", "baad mein"
        )

        // Decision logic — FIXED: Better detection
        val hasAnswer = answerWords.any { lower.contains(it) }
        val hasReject = rejectWords.any { lower.contains(it) }

        // Priority: If both detected, check which appears first
        val answerIndex = answerWords.map { lower.indexOf(it) }.filter { it >= 0 }.minOrNull() ?: Int.MAX_VALUE
        val rejectIndex = rejectWords.map { lower.indexOf(it) }.filter { it >= 0 }.minOrNull() ?: Int.MAX_VALUE

        val isAnswer = hasAnswer && (!hasReject || answerIndex < rejectIndex)
        val isReject = hasReject && (!hasAnswer || rejectIndex < answerIndex)

        Log.d(TAG, "Decision: isAnswer=$isAnswer, isReject=$isReject, answerPos=$answerIndex, rejectPos=$rejectIndex")

        when {
            isAnswer && !isReject -> performAnswer()
            isReject -> performReject()
            else -> {
                // ✅ ENHANCED: If not answer/reject, let Gemini handle the query naturally
                if (isLiveসংযুক্ত ✅) {
                    Log.d(TAG, "Passing unrecognized command to Gemini: $spoken")
                    isবলছে… = true
                    runচালুUiThread { statusText.text = "ভাবছে…... 🤔" }
                    liveClient.sendTextমেসেজ(spoken)
                } else {
                    val confusion = when (personality) {
                        "gf"  -> "Jaan, samajh nahi aaya. 'Uthao' ya 'Nahi' bolo."
                        else  -> "অনুগ্রহ করে say 'Answer' or 'Reject'."
                    }
                    speakViaWebSocket(confusion)
                    handler.postDelayed({ if (!isDecisionMade) repeatAnnouncement() }, 4000)
                }
            }
        }
    }

    /**
     * ✅ FIXED: কল ANSWER — proper logic, no confusion with reject
     */
    private fun performAnswer() {
        if (isDecisionMade) return
        isDecisionMade = true
        isকলAnswered = true
        stopশুনছে…()

        val confirmMsg = when (personality) {
            "gf"  -> "Ji $userনাম, call utha rahi hoon! 📞"
            else  -> "Answering the call, $userনাম."
        }
        speakViaWebSocket(confirmMsg)
        statusText.text = "Answering... 📞"
        Log.d(TAG, "=== ANSWERING CALL ===")

        handler.postDelayed({
            var success = false

            if (isWhatsAppকল) {
                // WhatsApp call answer
                success = answerWhatsAppকল()
            } else {
                // নাrmal call answer
                success = answerনাrmalকল()
            }

            if (!success) {
                speakViaWebSocket("দুঃখিত $userনাম, call nahi uth payi")
            }

            handler.postDelayed({ safeFinish() }, 2000)
        }, 1500)
    }

    /**
     * ✅ NEW: Separate method for normal call answer
     */
    private fun answerনাrmalকল(): Boolean {
        var success = false

        // Method 1: TelecomManager acceptRingingকল
        try {
            if (checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                val telecom = getসিস্টেমService(Context.TELECOM_SERVICE) as TelecomManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    @Suppress("DEPRECATION")
                    telecom.acceptRingingকল()
                    Log.d(TAG, "✅ কল accepted via TelecomManager.acceptRingingকল()")
                    success = true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "TelecomManager answer failed: ${e.message}")
        }

        // Method 2: অ্যাক্সেসিবিলিটি fallback
        if (!success) {
            try {
                val answered = com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                    text = "Answer"
                ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                    contentDesc = "Answer"
                ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                    text = "Accept"
                ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                    contentDesc = "Accept"
                )
                Log.d(TAG, "অ্যাক্সেসিবিলিটি answer: $answered")
                success = answered
            } catch (e: Exception) {
                Log.e(TAG, "অ্যাক্সেসিবিলিটি answer failed: ${e.message}")
            }
        }

        return success
    }

    /**
     * ✅ NEW: Answer WhatsApp call via accessibility
     */
    private fun answerWhatsAppকল(): Boolean {
        var success = false

        try {
            // Try to click WhatsApp answer button
            success = com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                text = "Answer"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                contentDesc = "Answer"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                text = "Accept"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                contentDesc = "Accept call"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                id = "com.whatsapp:id/incoming_call_answer"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                id = "com.whatsapp.w4b:id/incoming_call_answer"
            )

            Log.d(TAG, "WhatsApp call answer: $success")
        } catch (e: Exception) {
            Log.e(TAG, "WhatsApp answer failed: ${e.message}")
        }

        return success
    }

    /**
     * ✅ FIXED: কল REJECT — proper endকল(), no confusion
     */
    private fun performReject() {
        if (isDecisionMade) return
        isDecisionMade = true
        stopশুনছে…()

        val confirmMsg = when (personality) {
            "gf"  -> "Theek hai $userনাম, call reject kar diya. ❌"
            else  -> "কল declined, $userনাম."
        }
        speakViaWebSocket(confirmMsg)
        statusText.text = "Rejecting... ❌"
        Log.d(TAG, "=== REJECTING CALL ===")

        handler.postDelayed({
            var success = false

            if (isWhatsAppকল) {
                // WhatsApp call reject
                success = rejectWhatsAppকল()
            } else {
                // নাrmal call reject
                success = rejectনাrmalকল()
            }

            if (!success) {
                speakViaWebSocket("দুঃখিত $userনাম, call reject nahi ho paya")
            }

            handler.postDelayed({ safeFinish() }, 2000)
        }, 1500)
    }

    /**
     * ✅ NEW: Separate method for normal call reject
     */
    private fun rejectনাrmalকল(): Boolean {
        var success = false

        // Method 1: TelecomManager endকল
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                    val telecom = getসিস্টেমService(Context.TELECOM_SERVICE) as TelecomManager
                    @Suppress("DEPRECATION")
                    telecom.endকল()
                    Log.d(TAG, "✅ কল rejected via TelecomManager.endকল()")
                    success = true
                } else {
                    Log.w(TAG, "Permission ANSWER_PHONE_CALLS missing")
                }
            } else {
                Log.w(TAG, "TelecomManager.endকল() requires API 28")
            }
        } catch (e: Exception) {
            Log.e(TAG, "TelecomManager reject failed: ${e.message}")
        }

        // Method 2: অ্যাক্সেসিবিলিটি fallback
        if (!success) {
            try {
                val rejected = com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                    text = "Decline"
                ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                    text = "Reject"
                ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                    contentDesc = "Decline"
                ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                    contentDesc = "Reject call"
                )
                Log.d(TAG, "অ্যাক্সেসিবিলিটি reject: $rejected")
                success = rejected
            } catch (e: Exception) {
                Log.e(TAG, "অ্যাক্সেসিবিলিটি reject failed: ${e.message}")
            }
        }

        return success
    }

    /**
     * ✅ NEW: Reject WhatsApp call via accessibility
     */
    private fun rejectWhatsAppকল(): Boolean {
        var success = false

        try {
            success = com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                text = "Decline"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                contentDesc = "Decline"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                text = "Reject"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
                id = "com.whatsapp:id/incoming_call_decline"
            ) || com.maya.assistant.service.Smartঅ্যাক্সেসিবিলিটিEngine.click(
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

    private fun stopশুনছে…() {
        isশুনছে… = false
        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {}
        waveformView?.stopAnimation()
    }

    private fun startকলStateMonitor() {
        val tm = getসিস্টেমService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val monitor = object : Runnable {
            override fun run() {
                if (isDecisionMade || isকলAnswered) return
                if (tm.callState == TelephonyManager.CALL_STATE_IDLE && !isWhatsAppকল) {
                    Log.d(TAG, "ফোন idle — closing call assistant")
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

            // ✅ FIXED: রিসেট করো state properly
            isDecisionMade = false
            isশুনছে… = false
            announcementPlayed = false
            isবলছে… = false

            finish()
        }
    }

    private fun isNumberLike(str: String) =
        str.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }

    private fun getPrefsValue(key: String, default: String): String {
        return getSharedPreferences("maya_prefs", Context.MODE_PRIVATE)
            .getString(key, default) ?: default
    }

    override fun onপিছনেPressed() {
        speakViaWebSocket("Pehle decision lo!")
        // super.onপিছনেPressed() // সরাওd to block back button during call decision
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeকলbacksAndমেসেজs(null)
        try { unregisterReceiver(callStateReceiver) } catch (_: Exception) {}
        tts?.shutdown()
        if (::liveClient.isInitialized) liveClient.disconnect()
        liveAudioManager.stop()
        speechRecognizer?.destroy()

        // ✅ FIXED: রিসেট করো all states on destroy
        isDecisionMade = false
        isশুনছে… = false
        announcementPlayed = false
        isবলছে… = false
        isকলAnswered = false

        Log.d(TAG, "কলঅ্যাসিস্ট্যান্টActivity destroyed")
    }
}