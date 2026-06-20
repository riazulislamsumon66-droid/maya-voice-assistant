package com.maya.assistant.voice

import com.maya.assistant.utils.AudioUtils
import com.maya.assistant.utils.Logger

/**
 * Simple energy-based ভয়েস Activity Detector
 */
class ভয়েসActivityDetector(
    private val onSpeechশুরু করো: () -> Unit,
    private val onSpeechEnd: () -> Unit
) {
    private val TAG = "VAD"

    private val SPEECH_THRESHOLD = 0.015f   // RMS threshold to detect speech
    private val SILENCE_FRAMES = 25          // ~500ms silence to detect end of speech
    private val SPEECH_FRAMES = 3            // Consecutive frames to confirm speech start

    private var silenceগণনা = 0
    private var speechগণনা = 0
    private var isSpeechসক্রিয় = false

    fun processChunk(pcm: ByteArray) {
        val rms = AudioUtils.calculateRms(pcm)
        ভয়েসStateManager.updateAmplitude(rms)

        if (rms > SPEECH_THRESHOLD) {
            silenceগণনা = 0
            speechগণনা++

            if (!isSpeechসক্রিয় && speechগণনা >= SPEECH_FRAMES) {
                isSpeechসক্রিয় = true
                Logger.d(TAG, "Speech detected (rms=$rms)")
                onSpeechশুরু করো()
            }
        } else {
            speechগণনা = 0
            if (isSpeechসক্রিয়) {
                silenceগণনা++
                if (silenceগণনা >= SILENCE_FRAMES) {
                    isSpeechসক্রিয় = false
                    silenceগণনা = 0
                    Logger.d(TAG, "Silence detected")
                    onSpeechEnd()
                }
            }
        }
    }

    fun reset() {
        silenceগণনা = 0
        speechগণনা = 0
        isSpeechসক্রিয় = false
    }
}
