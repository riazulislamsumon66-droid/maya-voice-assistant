package com.maya.assistant.voice

import androidx.lifecycle.MutableLiveData
import com.maya.assistant.utils.Constants

object ভয়েসStateManager {

    val state = MutableLiveData(Constants.STATE_IDLE)
    val statusমেসেজ = MutableLiveData("সিস্টেম প্রস্তুত")
    val amplitude = MutableLiveData(0f)
    val isMicMuted = MutableLiveData(false)

    fun setশুনছে…() {
        state.postValue(Constants.STATE_LISTENING)
        statusমেসেজ.postValue("শুনছে…")
        isMicMuted.postValue(false)
    }

    fun setভাবছে…() {
        state.postValue(Constants.STATE_THINKING)
        statusমেসেজ.postValue("ভাবছে…")
    }

    fun setবলছে…() {
        state.postValue(Constants.STATE_SPEAKING)
        statusমেসেজ.postValue("বলছে…")
        isMicMuted.postValue(true)
    }

    fun setIdle() {
        state.postValue(Constants.STATE_IDLE)
        statusমেসেজ.postValue("সিস্টেম প্রস্তুত")
        isMicMuted.postValue(false)
        amplitude.postValue(0f)
    }

    fun setসমস্যা(msg: String) {
        state.postValue(Constants.STATE_IDLE)
        statusমেসেজ.postValue(msg)
    }

    fun updateAmplitude(rms: Float) {
        amplitude.postValue(rms.coerceIn(0f, 1f))
    }

    fun isAiবলছে…() = state.value == Constants.STATE_SPEAKING

    fun isশুনছে…() = state.value == Constants.STATE_LISTENING
}
