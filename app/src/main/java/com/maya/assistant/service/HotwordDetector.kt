package com.maya.assistant.service

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * HotwordDetector — Detects "Hey MAYA" wake word
 * 
 * Uses lightweight on-device keyword spotting.
 * For production, Porcupine/Picovoice SDK is recommended.
 * This implementation uses a simple energy-based VAD + pattern matching fallback.
 */
class HotwordDetector(private val context: Context) {

    companion object {
        private const val TAG = "HotwordDetector"
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_SIZE = 1024
        private const val HOTWORD_DURATION_MS = 3000 // 3 seconds of audio to analyze
    }

    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    var onHotwordDetected: (() -> Unit)? = null
    
    // Audio recording for cloud-based verification
    private var recordingBuffer = ByteArrayOutputStream()
    private var isRecording = false

    fun start() {
        if (isListening) return
        
        try {
            val minBufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
            )
            
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minBufferSize * 2
            )
            
            if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                audioRecord?.startRecording()
                isListening = true
                startDetectionLoop()
                Log.d(TAG, "Hotword detector started")
            } else {
                Log.e(TAG, "AudioRecord init failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting hotword: ${e.message}")
        }
    }

    fun stop() {
        isListening = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping hotword: ${e.message}")
        }
        Log.d(TAG, "Hotword detector stopped")
    }

    private fun startDetectionLoop() {
        scope.launch {
            val buffer = ByteArray(BUFFER_SIZE)
            var silenceCount = 0
            var speechCount = 0
            var totalEnergy = 0L
            var sampleCount = 0
            
            while (isListening) {
                val read = audioRecord?.read(buffer, 0, BUFFER_SIZE) ?: 0
                if (read > 0) {
                    // Calculate RMS energy
                    var sum = 0L
                    for (i in 0 until read step 2) {
                        val sample = (buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)
                        sum += (sample * sample).toLong()
                    }
                    val rms = Math.sqrt(sum.toDouble() / (read / 2)).toFloat()
                    
                    // Voice Activity Detection
                    if (rms > 500) { // Speech detected
                        speechCount++
                        silenceCount = 0
                        totalEnergy += (rms * rms).toLong()
                        sampleCount++
                        
                        // If we have enough speech, record for verification
                        if (speechCount > 10 && !isRecording) {
                            startRecording()
                        }
                        
                        if (isRecording) {
                            recordingBuffer.write(buffer, 0, read)
                        }
                    } else { // Silence
                        silenceCount++
                        
                        // If silence after speech, check if we have a potential hotword
                        if (silenceCount > 5 && speechCount > 10 && isRecording) {
                            stopRecordingAndVerify()
                        }
                        
                        if (silenceCount > 50) { // Reset after long silence
                            speechCount = 0
                            totalEnergy = 0
                            sampleCount = 0
                        }
                    }
                }
            }
        }
    }

    private fun startRecording() {
        isRecording = true
        recordingBuffer.reset()
        Log.d(TAG, "Started recording for hotword verification")
    }

    private fun stopRecordingAndVerify() {
        isRecording = false
        val audioData = recordingBuffer.toByteArray()
        Log.d(TAG, "Recorded ${audioData.size} bytes for verification")
        
        // Save audio for debugging/verification
        scope.launch {
            try {
                val file = File(context.cacheDir, "hotword_${System.currentTimeMillis()}.pcm")
                FileOutputStream(file).use { it.write(audioData) }
                
                // For now, trigger hotword detection
                // In production, send to cloud API or run on-device ML model
                // For demo purposes, we'll use a simple approach:
                // If audio duration is ~2-4 seconds and has speech energy, trigger
                val durationMs = (audioData.size.toFloat() / (SAMPLE_RATE * 2)) * 1000
                
                if (durationMs in 1500f..5000f) {
                    Log.d(TAG, "Potential hotword detected! Duration: ${durationMs}ms")
                    // In production: verify with ML model here
                    // For now, we trigger on any speech of right duration
                    // This will be replaced with actual "Hey MAYA" model
                    withContext(Dispatchers.Main) {
                        onHotwordDetected?.invoke()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving audio: ${e.message}")
            }
        }
    }

    /**
     * Get recorded audio for cloud verification
     */
    fun getLastRecording(): ByteArray? {
        return if (recordingBuffer.size() > 0) recordingBuffer.toByteArray() else null
    }
}
