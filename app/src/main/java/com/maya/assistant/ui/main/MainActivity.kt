package com.maya.assistant.ui.main

import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.রঙ
import android.os.*
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maya.assistant.R
import com.maya.assistant.security.বায়োমেট্রিকManager
import com.maya.assistant.service.অ্যাক্সেসিবিলিটিসাহায্যerService
import com.maya.assistant.services.Foregroundভয়েসService
import com.maya.assistant.ui.settings.সেটিংসActivity
import com.maya.assistant.utils.Constants
import com.maya.assistant.utils.Logger
import com.maya.assistant.utils.PermissionUtils
import com.maya.assistant.utils.prefs
import com.maya.assistant.viewmodel.MainViewModel
import com.maya.assistant.voice.ভয়েসStateManager
import java.text.SimpleতারিখFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MAIN"
    private val viewModel: MainViewModel by viewModels()

    private lateinit var orbView: OrbAnimationView
    private lateinit var waveformView: WaveformView
    private lateinit var statusText: TextView
    private lateinit var micButton: ImageButton
    private lateinit var chatRecycler: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var batteryText: TextView
    private lateinit var ramText: TextView
    private lateinit var timeText: TextView

    private val timeHandler = Handler(Looper.getMainLooper())
    private val timeRunnable = object : Runnable {
        override fun run() {
            updateসিস্টেমতথ্য()
            timeHandler.postDelayed(this, 1000)
        }
    }

    private val responseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val text = intent?.getStringExtra("text") ?: return
            addBotমেসেজ(text)
        }
    }

    companion object {
        const val PERM_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.statusBarরঙ = রঙ.TRANSPARENT
        setContentView(R.layout.activity_main)

        initViews()
        updateসিস্টেমতথ্য()
        timeHandler.post(timeRunnable)

        বায়োমেট্রিকManager.authenticate(
            this,
            onসফল = { setupঅ্যাসিস্ট্যান্ট() },
            onসমস্যা = { setupঅ্যাসিস্ট্যান্ট() },
            onFallback = { setupঅ্যাসিস্ট্যান্ট() }
        )
    }

    private fun setupঅ্যাসিস্ট্যান্ট() {
        PermissionUtils.requestMissing(this, PERM_CODE)
        observeViewModel()
        observeভয়েসState()
        startভয়েসService()
        checkঅ্যাক্সেসিবিলিটি()
    }

    private fun initViews() {
        orbView     = findViewById(R.id.orbView)
        waveformView = findViewById(R.id.waveformView)
        statusText  = findViewById(R.id.statusText)
        micButton   = findViewById(R.id.micButton)
        chatRecycler = findViewById(R.id.chatRecycler)
        batteryText = findViewById(R.id.batteryText)
        ramText     = findViewById(R.id.ramText)
        timeText    = findViewById(R.id.timeText)

        chatAdapter = ChatAdapter()
        chatRecycler.layoutManager = LinearLayoutManager(this).also { it.stackFromEnd = true }
        chatRecycler.adapter = chatAdapter

        micButton.setচালুClickListener {
            val svc = Foregroundভয়েসService.instance
            if (svc != null) {
                val text = "Hey MAYA"
                svc.sendTextToGemini(text)
                addব্যবহারকারীমেসেজ(text)
            } else {
                startভয়েসService()
            }
        }

        micButton.setচালুLongClickListener {
            Foregroundভয়েসService.instance?.reconnectGemini()
            addBotমেসেজ("পুনরায় সংযুক্ত হচ্ছে…")
            true
        }

        findViewById<ImageButton>(R.id.settingsBtn).setচালুClickListener {
            startActivity(Intent(this, সেটিংসActivity::class.java))
        }
    }

    private fun observeভয়েসState() {
        ভয়েসStateManager.state.observe(this) { state ->
            when (state) {
                Constants.STATE_LISTENING -> {
                    orbView.setশুনছে…()
                    micButton.setImageResource(R.drawable.ic_mic_on)
                    statusText.setTextরঙ(0xFF00E5FF.toInt())
                }
                Constants.STATE_THINKING -> {
                    orbView.setভাবছে…()
                    statusText.setTextরঙ(0xFFD500F9.toInt())
                }
                Constants.STATE_SPEAKING -> {
                    orbView.setবলছে…()
                    micButton.setImageResource(R.drawable.ic_mic_off)
                    statusText.setTextরঙ(0xFFFF1744.toInt())
                }
                else -> {
                    orbView.setIdle()
                    micButton.setImageResource(R.drawable.ic_mic_off)
                    statusText.setTextরঙ(0xFFFF1744.toInt())
                }
            }
        }
        ভয়েসStateManager.amplitude.observe(this) { amp ->
            waveformView.updateAmplitude(amp)
            orbView.setAmplitude(amp)
        }
        ভয়েসStateManager.statusমেসেজ.observe(this) { msg ->
            statusText.text = msg
        }
    }

    private fun observeViewModel() {
        viewModel.aiResponse.observe(this) { text ->
            if (!text.isNullOrBlank()) addBotমেসেজ(text)
        }
    }

    private fun startভয়েসService() {
        val apiKey = prefs().getString(Constants.KEY_API_KEY, "") ?: ""
        if (apiKey.isEmpty()) {
            addBotমেসেজ("⚠️ API Key দরকার. অনুগ্রহ করে go to সেটিংস → Gemini API Key দিন.")
            return
        }
        ContextCompat.startForegroundService(this, Intent(this, Foregroundভয়েসService::class.java))
        Logger.d(TAG, "ভয়েস service started")
    }

    private fun checkঅ্যাক্সেসিবিলিটি() {
        if (!অ্যাক্সেসিবিলিটিসাহায্যerService.isচালু(this)) {
            addBotমেসেজ("⚠️ চালু করো অ্যাক্সেসিবিলিটি Service for app control. সেটিংস → অ্যাক্সেসিবিলিটি.")
        }
    }

    fun addব্যবহারকারীমেসেজ(text: String) = runচালুUiThread {
        chatAdapter.addমেসেজ(Chatমেসেজ(text, true))
        chatRecycler.scrollToPosition(chatAdapter.itemগণনা - 1)
    }

    fun addBotমেসেজ(text: String) = runচালুUiThread {
        chatAdapter.addমেসেজ(Chatমেসেজ(text, false))
        chatRecycler.scrollToPosition(chatAdapter.itemগণনা - 1)
    }

    private fun updateসিস্টেমতথ্য() {
        val sdf = SimpleতারিখFormat("HH:mm", Locale.getডিফল্ট())
        timeText.text = sdf.format(তারিখ())
        val bm = getসিস্টেমService(Context.BATTERY_SERVICE) as? android.os.ব্যাটারিManager
        batteryText.text = "${bm?.getIntProperty(android.os.ব্যাটারিManager.BATTERY_PROPERTY_CAPACITY) ?: 0}%"
        val am = getসিস্টেমService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.মেমোরিতথ্য().also { am.getমেমোরিতথ্য(it) }
        ramText.text = "${(mi.totalMem - mi.availMem) / 1048576}MB"
    }

    override fun onচালিয়ে যাও() {
        super.onচালিয়ে যাও()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(responseReceiver, IntentFilter("MAYA_RESPONSE"), Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(responseReceiver, IntentFilter("MAYA_RESPONSE"))
        }
    }

    override fun onবিরতি() {
        super.onবিরতি()
        try { unregisterReceiver(responseReceiver) } catch (_: Exception) {}
    }

    override fun onDestroy() {
        timeHandler.removeকলbacks(timeRunnable)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERM_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startভয়েসService()
        }
    }
}
