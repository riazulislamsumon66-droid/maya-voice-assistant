package com.maya.assistant.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maya.assistant.R
import com.maya.assistant.ai.AudioEngine
import com.maya.assistant.ai.CommandParser
import com.maya.assistant.ai.GeminiLiveClient
import com.maya.assistant.service.CallMonitorService
import com.maya.assistant.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val PREFS_NAME = "maya_prefs"
    }

    // AI
    private lateinit var geminiLive: GeminiLiveClient
    private lateinit var audioEngine: AudioEngine
    private val commandParser = CommandParser()

    // ViewModel
    private lateinit var viewModel: MainViewModel

    // UI
    private lateinit var orbView: OrbAnimationView
    private lateinit var waveformView: WaveformView
    private lateinit var statusText: TextView
    private lateinit var micButton: ImageButton
    private lateinit var chatRecycler: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var redOverlay: View
    private lateinit var batteryText: TextView
    private lateinit var ramText: TextView
    private lateinit var timeText: TextView
    private lateinit var settingsBtn: ImageButton

    // Chat buffers
    private val inputBuffer = StringBuilder()
    private val outputBuffer = StringBuilder()

    // State
    private var isActive = false
    private var isInCallMode = false
    private var isMuted = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            initServices()
        } else {
            Toast.makeText(this, "Permissions required for MAYA to work!", Toast.LENGTH_LONG).show()
        }
    }

    private val callEndedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isInCallMode = false
            setActiveMode(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        checkPermissions()

        // Handle incoming call intent
        if (intent.getBooleanExtra("INCOMING_CALL", false)) {
            val callerName = intent.getStringExtra("CALLER_NAME") ?: "Unknown"
            announceCall(callerName)
        }
    }

    private fun initViews() {
        orbView = findViewById(R.id.orbView)
        waveformView = findViewById(R.id.waveformView)
        statusText = findViewById(R.id.statusText)
        micButton = findViewById(R.id.micButton)
        chatRecycler = findViewById(R.id.chatRecycler)
        redOverlay = findViewById(R.id.redOverlay)
        batteryText = findViewById(R.id.batteryText)
        ramText = findViewById(R.id.ramText)
        timeText = findViewById(R.id.timeText)
        settingsBtn = findViewById(R.id.settingsBtn)

        // Chat
        chatAdapter = ChatAdapter()
        chatRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        // Mic button touch
        micButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startListening()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    stopListening()
                    true
                }
                else -> false
            }
        }

        // Long press to interrupt
        micButton.setOnLongClickListener {
            interruptMaya()
            true
        }

        // Settings
        settingsBtn.setOnClickListener {
            startActivity(Intent(this, com.maya.assistant.ui.settings.SettingsActivity::class.java))
        }

        // ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.commandResult.observe(this) { result ->
            result?.let {
                geminiLive.sendText(it)
                addChatMessage(it, false)
            }
        }

        // Status updates
        startStatusUpdates()
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions.add(Manifest.permission.ANSWER_PHONE_CALLS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }

        val needed = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needed.isEmpty()) {
            initServices()
        } else {
            requestPermissionLauncher.launch(needed.toTypedArray())
        }
    }

    private fun initServices() {
        try {
            startService(Intent(this, CallMonitorService::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "Error starting CallMonitorService: ${e.message}")
        }

        registerReceiver(callEndedReceiver, IntentFilter("com.myra.CALL_ENDED"), RECEIVER_NOT_EXPORTED)

        orbView.postDelayed({ initGeminiLive() }, 300)
    }

    private fun initGeminiLive() {
        geminiLive = GeminiLiveClient(this)
        audioEngine = AudioEngine(this)

        // Wire callbacks
        geminiLive.onConnected = {
            Log.d(TAG, "Gemini connected")
        }

        geminiLive.onSetupComplete = {
            runOnUiThread {
                audioEngine.startRecording()
                audioEngine.startPlayback()
                sendGreeting()
            }
        }

        geminiLive.onAudioReceived = { audioBytes ->
            audioEngine.queueAudio(audioBytes)
            runOnUiThread {
                orbView.setState(OrbState.SPEAKING)
                statusText.text = "বলছে..." // "Speaking..."
            }
        }

        geminiLive.onOutputTranscript = { text ->
            outputBuffer.append(text)
            runOnUiThread {
                statusText.text = "বলছে: $text"
            }
        }

        geminiLive.onInputTranscript = { text ->
            inputBuffer.append(text)
        }

        geminiLive.onTurnComplete = {
            runOnUiThread {
                val input = inputBuffer.toString()
                val output = outputBuffer.toString()
                inputBuffer.clear()
                outputBuffer.clear()

                if (input.isNotEmpty()) {
                    addChatMessage(input, true)
                    // Check for commands if not in call mode
                    if (!isInCallMode) {
                        // Check voice authentication
                        val canExecute = com.maya.assistant.service.MayaCoreService.canExecuteCommands()
                        
                        if (canExecute) {
                            // Voice verified or not enrolled — allow commands
                            val command = commandParser.parse(input)
                            if (command != null) {
                                viewModel.executeCommand(command)
                            } else {
                                // Try screen-aware commands
                                val screenCommand = commandParser.parseScreenCommand(input)
                                if (screenCommand != null) {
                                    executeScreenCommand(screenCommand)
                                }
                                // else: conversation already sent via WebSocket
                            }
                        } else {
                            // Voice NOT verified — conversation only, no commands
                            Log.d("MainActivity", "Voice not verified — commands blocked, conversation only")
                            geminiLive.sendText("আমি তোমার voice চিনতে পারছি না। শুধু কথা বলতে পারবে, কোনো কাজ করবে না।")
                        }
                    }
                }
                if (output.isNotEmpty()) {
                    addChatMessage(output, false)
                }

                orbView.setState(OrbState.IDLE)
                statusText.text = "ট্যাপ করে বলো 💬" // "Tap to speak"
                setActiveMode(false)
            }
        }

        geminiLive.onError = { error ->
            runOnUiThread {
                statusText.text = "Error: $error"
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        audioEngine.onAmplitudeChanged = { rms ->
            runOnUiThread {
                waveformView.setAmplitude(rms)
            }
        }

        geminiLive.connect()
    }

    private fun sendGreeting() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Friend") ?: "Friend"
        val personality = prefs.getString("personality_mode", "gf") ?: "gf"

        val greeting = when (personality) {
            "gf" -> "হেলো $userName! আমি মায়া, তোমার personal AI assistant ❤️ কীভাবে সাহায্য করতে পারি?"
            "professional" -> "Good day $userName. MAYA is online and ready to assist you."
            else -> "হ্যাঁ $userName! আমি MAYA. কী সাহায্য করতে পারি?"
        }

        geminiLive.sendText(greeting)
        addChatMessage(greeting, false)
    }

    private fun startListening() {
        if (isInCallMode) return
        setActiveMode(true)
        orbView.setState(OrbState.LISTENING)
        waveformView.startAnimation()
        statusText.text = "শুনছে..." // "Listening..."
        isMuted = false
    }

    private fun stopListening() {
        orbView.setState(OrbState.IDLE)
        waveformView.stopAnimation()
        statusText.text = "ট্যাপ করে বলো 💬"
        setActiveMode(false)
        isMuted = true
    }

    private fun interruptMaya() {
        audioEngine.clearPlaybackQueue()
        geminiLive.sendInterrupt()
        orbView.setState(OrbState.IDLE)
        statusText.text = "ট্যাপ করে বলো 💬"
        setActiveMode(false)
    }

    private fun setActiveMode(active: Boolean) {
        isActive = active
        val animation = AlphaAnimation(if (active) 0f else 0.08f, if (active) 0.08f else 0f).apply {
            duration = if (active) 300 else 500
            fillAfter = true
        }
        redOverlay.startAnimation(animation)
    }

    private fun announceCall(callerName: String) {
        isInCallMode = true
        setActiveMode(true)
        orbView.setState(OrbState.THINKING)
        statusText.text = "কল আসছে..."

        val message = "স্যার, $callerName এর কল আসছে। উঠাবো নাকি reject করবো?"
        geminiLive.sendText(message)
        addChatMessage(message, false)
    }

    private fun addChatMessage(text: String, isUser: Boolean) {
        val message = ChatMessage(text = text, isUser = isUser)
        chatAdapter.addMessage(message)
        chatRecycler.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun startStatusUpdates() {
        val handler = android.os.Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                updateStatusInfo()
                handler.postDelayed(this, 30000)
            }
        }
        handler.post(runnable)
    }

    private fun updateStatusInfo() {
        // Battery
        val bm = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        batteryText.text = "$level%"

        // RAM
        val runtime = Runtime.getRuntime()
        val usedMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMB = runtime.maxMemory() / (1024 * 1024)
        ramText.text = "${usedMB}MB/${maxMB}MB"

        // Time
        val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        timeText.text = timeFormat.format(java.util.Date())
    }

    // ===== SCREEN COMMAND EXECUTION =====

    private fun executeScreenCommand(command: com.maya.assistant.model.AppCommand) {
        when (command.type) {
            "SCREEN_FACEBOOK_POST" -> {
                val content = command.params["content"] ?: ""
                val raw = command.params["raw"] ?: ""
                statusText.text = "Facebook এ post করছে..."
                orbView.setState(OrbState.THINKING)

                // Step 1: Open Facebook
                viewModel.executeCommand(
                    com.maya.assistant.model.AppCommand(
                        type = "OPEN_APP",
                        params = mapOf("app_name" to "facebook", "package_name" to "com.facebook.katana")
                    )
                )

                // Step 2: After delay, guide user through posting
                orbView.postDelayed({
                // Tell MAYA to help with the post via Gemini
                val postInstruction = if (content.isNotBlank()) {
                    "Facebook এ একটা post করো: $content"
                } else {
                    raw
                }
                geminiLive.sendText("User wants to $postInstruction. Please guide them step by step on what to do on the Facebook screen. Tell them to use screen commands like 'click on X' or 'type X'. Keep it short.")
                statusText.text = "MAYA বলছে কীভাবে post করবেন..."
            }, 3000)
        }

            "SCREEN_CLICK_TEXT" -> {
                val target = command.params["target"] ?: ""
                statusText.text = "'$target' click করছে..."
                val success = viewModel.clickOnScreenText(target)
                val msg = if (success) "'$target' click হয়ে গেল!" else "'$target' পাওয়া যায়নি"
                geminiLive.sendText(msg)
            }

            "SCREEN_TYPE_TEXT" -> {
                val content = command.params["content"] ?: ""
                statusText.text = "টাইপ করছে: $content"
                viewModel.smartTypeOnScreen(content)
                orbView.postDelayed({
                    geminiLive.sendText("Typed '$content' on screen. What next?")
                }, 1500)
            }

            "SCREEN_SCROLL" -> {
                val direction = command.params["direction"] ?: "down"
                viewModel.scrollScreen(direction)
                statusText.text = "Scroll করছে $direction..."
            }

            "SCREEN_READ" -> {
                statusText.text = "Screen পড়ছে..."
                orbView.setState(OrbState.THINKING)

                // Get screen content via accessibility service
                val screenInfo = viewModel.getScreenInfo()
                val screenText = screenInfo.fullText

                if (screenText.isNotBlank()) {
                    // Send to Gemini for interpretation
                    val prompt = """
                        Here is the current screen content from ${screenInfo.appName}:
                        $screenText

                        Clickable items: ${screenInfo.clickableItems.joinToString(", ") { it.text }}

                        User asked: "স্ক্রিনটা বলো / read screen"
                        Summarize what's on the screen in Bangla/English/Hindi. Keep it short.
                    """.trimIndent()
                    geminiLive.sendText(prompt)
                } else {
                    geminiLive.sendText("Accessibility service is not enabled. Please enable it in Settings to read screen content.")
                    statusText.text = "Accessibility চালু নেই — Settings এ যান"
                }
            }

            "SCREEN_SEARCH" -> {
                val query = command.params["query"] ?: ""
                statusText.text = "সার্চ করছে: $query"
                viewModel.smartTypeOnScreen(query)
                orbView.postDelayed({
                    // Press Enter / Search key
                    viewModel.clickOnScreenText("Search")
                    viewModel.clickOnScreenText("সার্চ")
                }, 2000)
            }

            else -> {
                statusText.text = "Unknown screen command: ${command.type}"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        audioEngine.setMuted(true)
    }

    override fun onResume() {
        super.onResume()
        if (!isActive) {
            audioEngine.setMuted(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(callEndedReceiver)
        } catch (e: Exception) {}
        geminiLive.disconnect()
        audioEngine.release()
    }
}
