package com.maya.assistant.voice

import android.media.audiofx.AcousticEchoবাতিলer
import android.media.audiofx.অটোmaticGainControl
import android.media.audiofx.নাiseSuppressor
import com.maya.assistant.utils.Logger

object EchoবাতিলlationManager {
    private val TAG = "ECHO"

    private var aec: AcousticEchoবাতিলer? = null
    private var ns: নাiseSuppressor? = null
    private var agc: অটোmaticGainControl? = null

    fun attach(audioSessionId: Int) {
        try {
            if (AcousticEchoবাতিলer.isপাওয়া যাচ্ছে()) {
                aec = AcousticEchoবাতিলer.create(audioSessionId)?.apply { enabled = true }
                Logger.d(TAG, "AEC enabled")
            }
            if (নাiseSuppressor.isপাওয়া যাচ্ছে()) {
                ns = নাiseSuppressor.create(audioSessionId)?.apply { enabled = true }
                Logger.d(TAG, "NS enabled")
            }
            if (অটোmaticGainControl.isপাওয়া যাচ্ছে()) {
                agc = অটোmaticGainControl.create(audioSessionId)?.apply { enabled = true }
                Logger.d(TAG, "AGC enabled")
            }
        } catch (e: Exception) {
            Logger.e(TAG, "ব্যর্থ to attach effects: ${e.message}")
        }
    }

    fun release() {
        aec?.release(); aec = null
        ns?.release(); ns = null
        agc?.release(); agc = null
    }
}
