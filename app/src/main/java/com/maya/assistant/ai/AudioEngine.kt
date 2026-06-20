package com.maya.assistant.ai

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue

class AudioEngine(private val context: Context) {

    companion object {
        private const val TAG = "AudioEngine"
        private const val MIC_SAMPLE_RATE = 16000
        private const val SPEAKER_SAMPLE_RATE = 24000
        private const val CHANNEL_CONFIG_IN = AudioFormat.CHANNEL_IN_MONO
        private const val CHANNEL_CONFIG_OUT = AudioFormat.CHANNEL_OUT_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val CHUNK_SIZE = 1024
    }

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var isRecording = false
    private var isPlaying = false
    private var isMuted = false

    private val audioQueue = ConcurrentLinkedQueue<ByteArray>()

    // Callbacks
    var onAmplitudeChanged: ((Float) -> Unit)? = null
    var onSpeakingStarted: (() -> Unit)? = null
    var onSpeakingStopped: (() -> Unit)? = null

    fun startRecording() {
        val minBufferSize = AudioRecord.getMinBufferSize(
            MIC_SAMPLE_RATE, CHANNEL_CONFIG_IN, AUDIO_FORMAT
        )

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                MIC_SAMPLE_RATE,
                CHANNEL_CONFIG_IN,
                AUDIO_FORMAT,
                minBufferSize * 2
            )

            if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                audioRecord?.startRecording()
                isRecording = true
                startRecordingThread()
                Log.d(TAG, "Recording started")
            } else {
                Log.e(TAG, "AudioRecord initialization failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting recording: ${e.message}")
        }
    }

    private fun startRecordingThread() {
        Thread {
            while (isRecording) {
                val buffer = ByteArray(CHUNK_SIZE)
                val bytesRead = audioRecord?.read(buffer, 0, CHUNK_SIZE) ?: 0
                if (bytesRead > 0 && !isMuted) {
                    // Calculate RMS amplitude
                    var sum = 0.0
                    for (i in 0 until bytesRead step 2) {
                        val sample = (buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)
                        sum += (sample * sample).toDouble()
                    }
                    val rms = Math.sqrt(sum / (bytesRead / 2)).toFloat() / 32768f
                    onAmplitudeChanged?.invoke(rms.coerceIn(0f, 1f))
                }
            }
        }.start()
    }

    fun getAudioData(): ByteArray? {
        if (isMuted) return null
        val buffer = ByteArray(CHUNK_SIZE)
        val bytesRead = audioRecord?.read(buffer, 0, CHUNK_SIZE) ?: 0
        return if (bytesRead > 0) buffer else null
    }

    fun startPlayback() {
        val minBufferSize = AudioTrack.getMinBufferSize(
            SPEAKER_SAMPLE_RATE, CHANNEL_CONFIG_OUT, AUDIO_FORMAT
        )

        try {
            audioTrack = AudioTrack(
                AudioManager.USAGE_MEDIA,
                SPEAKER_SAMPLE_RATE,
                CHANNEL_CONFIG_OUT,
                AUDIO_FORMAT,
                minBufferSize * 2,
                AudioTrack.MODE_STREAM
            )

            if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
                audioTrack?.play()
                isPlaying = true
                startPlaybackThread()
                Log.d(TAG, "Playback started")
            } else {
                Log.e(TAG, "AudioTrack initialization failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting playback: ${e.message}")
        }
    }

    private fun startPlaybackThread() {
        Thread {
            while (isPlaying) {
                val data = audioQueue.poll()
                if (data != null) {
                    audioTrack?.write(data, 0, data.size)
                } else {
                    Thread.sleep(10)
                }
            }
        }.start()
    }

    fun queueAudio(pcmBytes: ByteArray) {
        if (isPlaying) {
            audioQueue.add(pcmBytes)
            onSpeakingStarted?.invoke()
        }
    }

    fun clearPlaybackQueue() {
        audioQueue.clear()
        audioTrack?.pause()
        audioTrack?.flush()
        audioTrack?.play()
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun release() {
        isRecording = false
        isPlaying = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        audioQueue.clear()
        Log.d(TAG, "AudioEngine released")
    }
}
