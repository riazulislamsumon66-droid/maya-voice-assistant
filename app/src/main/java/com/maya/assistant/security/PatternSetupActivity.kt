package com.maya.assistant.security

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.maya.assistant.R
import java.util.*

class PatternSetupActivity : AppCompatActivity(), TextToSpeech.চালুInitListener {

    private lateinit var patternLockView: PatternLockView
    private lateinit var instructionText: TextView
    private lateinit var retryBtn: Button
    private lateinit var cancelBtn: Button
    
    private var tts: TextToSpeech? = null
    private var firstPattern: List<Int>? = null
    private var isনিশ্চিত করোing = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pattern_setup)

        initViews()
        tts = TextToSpeech(this, this)
    }

    private fun initViews() {
        patternLockView = findViewById(R.id.patternLockView)
        instructionText = findViewById(R.id.instructionText)
        retryBtn = findViewById(R.id.retryBtn)
        cancelBtn = findViewById(R.id.cancelBtn)

        patternLockView.listener = object : PatternLockView.PatternListener {
            override fun onPatternশুরু করোed() {
                instructionText.text = if (isনিশ্চিত করোing) "আবার এঁকো..." else "কানেক্ট করতে থাকো..."
            }

            override fun onPatternComplete(pattern: List<Int>) {
                handlePattern(pattern)
            }

            override fun onPatternপরিষ্কার করোed() {}
        }

        retryBtn.setচালুClickListener { resetSetup() }
        cancelBtn.setচালুClickListener { finish() }
    }

    private fun handlePattern(pattern: List<Int>) {
        if (!isনিশ্চিত করোing) {
            // First time drawing
            if (pattern.size < PatternLockView.MIN_PATTERN_LENGTH) {
                patternLockView.showসমস্যা()
                instructionText.text = "Bohat chota hai! কমপক্ষে 4টা dots connect করো"
                speak("কমপক্ষে 4টা dots connect করো", true)
                handler.postDelayed({ patternLockView.clearPattern() }, 800)
                return
            }

            firstPattern = pattern
            patternLockView.showসফল()
            speak("এখন আবার এঁকে confirm করো", true)
            
            handler.postDelayed({
                isনিশ্চিত করোing = true
                instructionText.text = "নিশ্চিত করো karne ke liye dobara draw karo"
                patternLockView.clearPattern()
                retryBtn.visibility = View.VISIBLE
            }, 700)
            
        } else {
            // নিশ্চিত করোing the pattern
            val first = firstPattern ?: return
            if (pattern.joinToString("-") == first.joinToString("-")) {
                // সফল!
                PatternManager.savePattern(this, pattern)
                PatternManager.enablePatternLock(this)
                নিরাপত্তাManager.setAppLockচালু(this, true)
                patternLockView.showসফল()
                instructionText.text = "Pattern সেট হয়ে গেছে!"
                speak("Pattern সেট হয়ে গেছে! Ab aapka app safe hai.", true)
                
                handler.postDelayed({
                    setResult(RESULT_ঠিক আছে)
                    finish()
                }, 1500)
            } else {
                // Mismatch
                patternLockView.showসমস্যা()
                instructionText.text = "Pattern match nahi hua! আবার চেষ্টা করো"
                speak("Pattern match nahi hua! Dobara try karo", true)
                handler.postDelayed({ patternLockView.clearPattern() }, 800)
            }
        }
    }

    private fun resetSetup() {
        firstPattern = null
        isনিশ্চিত করোing = false
        patternLockView.clearPattern()
        retryBtn.visibility = View.INVISIBLE
        instructionText.text = "Naya pattern draw karo"
        speak("Thik hai, naya pattern draw karo", true)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("bn", "BD")
        }
    }

    private fun speak(text: String, flush: Boolean) {
        val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, mode, null, null)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
