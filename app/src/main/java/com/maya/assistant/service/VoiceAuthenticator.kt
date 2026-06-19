package com.maya.assistant.service

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

/**
 * VoiceAuthenticator — Speaker recognition for Jaan
 * 
 * Uses voice feature extraction (MFCC-like) to identify Jaan's voice.
 * Only registered voice will trigger command execution.
 * 
 * Flow:
 * 1. Enrollment: Record Jaan's voice samples → extract features → save profile
 * 2. Verification: Record voice → extract features → compare with profile
 */
class VoiceAuthenticator(private val context: Context) {

    companion object {
        private const val TAG = "VoiceAuth"
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_SIZE = 1024
        private const val PROFILE_FILE = "jaan_voice_profile.bin"
        private const val ENROLL_SAMPLES = 5 // Number of enrollment samples
        private const val MATCH_THRESHOLD = 0.75f // Similarity threshold (0-1)
        private const val FEATURE_SIZE = 13 // MFCC-like feature vector size
    }

    private var audioRecord: AudioRecord? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Voice profile
    private var voiceProfile: FloatArray? = null
    private var isEnrolled = false

    init {
        loadProfile()
    }

    /**
     * Load voice profile from storage
     */
    private fun loadProfile() {
        val file = File(context.filesDir, PROFILE_FILE)
        if (file.exists()) {
            try {
                val bytes = FileInputStream(file).readBytes()
                val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
                val features = FloatArray(FEATURE_SIZE)
                for (i in features.indices) {
                    features[i] = buffer.float
                }
                voiceProfile = features
                isEnrolled = true
                Log.d(TAG, "Voice profile loaded")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading voice profile: ${e.message}")
            }
        } else {
            Log.d(TAG, "No voice profile found — need to enroll")
        }
    }

    /**
     * Enroll Jaan's voice — record multiple samples and create profile
     */
    suspend fun enrollVoice(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Starting voice enrollment...")
            val allFeatures = mutableListOf<FloatArray>()
            
            for (i in 0 until ENROLL_SAMPLES) {
                val audioData = recordAudioSample(3000) // 3 seconds each
                if (audioData != null) {
                    val features = extractFeatures(audioData)
                    allFeatures.add(features)
                    Log.d(TAG, "Enrolled sample ${i + 1}/$ENROLL_SAMPLES")
                }
            }
            
            if (allFeatures.size >= 3) {
                // Average all feature vectors
                val avgProfile = FloatArray(FEATURE_SIZE)
                for (features in allFeatures) {
                    for (j in features.indices) {
                        avgProfile[j] += features[j]
                    }
                }
                for (j in avgProfile.indices) {
                    avgProfile[j] /= allFeatures.size
                }
                
                // Save profile
                saveProfile(avgProfile)
                voiceProfile = avgProfile
                isEnrolled = true
                Log.d(TAG, "Voice enrollment complete!")
                true
            } else {
                Log.e(TAG, "Not enough samples recorded")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error enrolling voice: ${e.message}")
            false
        }
    }

    /**
     * Verify if recorded voice matches Jaan's profile
     */
    suspend fun verifyVoice(): Pair<Boolean, Float> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!isEnrolled || voiceProfile == null) {
                return@withContext Pair(false, 0f)
            }
            
            // Record 2 seconds of audio
            val audioData = recordAudioSample(2000)
            if (audioData == null) {
                return@withContext Pair(false, 0f)
            }
            
            val features = extractFeatures(audioData)
            val similarity = cosineSimilarity(features, voiceProfile!!)
            
            Log.d(TAG, "Voice similarity: $similarity (threshold: $MATCH_THRESHOLD)")
            Pair(similarity >= MATCH_THRESHOLD, similarity)
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying voice: ${e.message}")
            Pair(false, 0f)
        }
    }

    /**
     * Record audio sample
     */
    private suspend fun recordAudioSample(durationMs: Int): ByteArray? = withContext(Dispatchers.IO) {
        return@withContext try {
            val minBufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
            )
            
            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minBufferSize * 2
            )
            
            if (record.state != AudioRecord.STATE_INITIALIZED) {
                return@withContext null
            }
            
            record.startRecording()
            
            val buffer = ByteArrayOutputStream()
            val tempBuffer = ByteArray(BUFFER_SIZE)
            val totalSamples = (SAMPLE_RATE * durationMs / 1000) * 2 // 16-bit = 2 bytes per sample
            var totalRead = 0
            
            while (totalRead < totalSamples) {
                val toRead = minOf(tempBuffer.size, totalSamples - totalRead)
                val read = record.read(tempBuffer, 0, toRead)
                if (read > 0) {
                    buffer.write(tempBuffer, 0, read)
                    totalRead += read
                }
            }
            
            record.stop()
            record.release()
            
            buffer.toByteArray()
        } catch (e: Exception) {
            Log.e(TAG, "Error recording audio: ${e.message}")
            null
        }
    }

    /**
     * Extract MFCC-like features from audio
     * Simplified version — for production, use a proper audio feature extraction library
     */
    private fun extractFeatures(audioData: ByteArray): FloatArray {
        val features = FloatArray(FEATURE_SIZE)
        
        // Convert bytes to short array
        val samples = ShortArray(audioData.size / 2)
        val buffer = ByteBuffer.wrap(audioData).order(ByteOrder.LITTLE_ENDIAN)
        for (i in samples.indices) {
            samples[i] = buffer.short
        }
        
        // 1. Zero Crossing Rate
        var zeroCrossings = 0
        for (i in 1 until samples.size) {
            if ((samples[i] >= 0 && samples[i - 1] < 0) || (samples[i] < 0 && samples[i - 1] >= 0)) {
                zeroCrossings++
            }
        }
        features[0] = zeroCrossings.toFloat() / samples.size
        
        // 2. RMS Energy
        var sumSquares = 0.0
        for (sample in samples) {
            sumSquares += (sample * sample).toDouble()
        }
        features[1] = sqrt(sumSquares / samples.size).toFloat() / 32768f
        
        // 3. Spectral Centroid (simplified)
        val frameSize = 512
        val numFrames = samples.size / frameSize
        if (numFrames > 0) {
            var weightedSum = 0.0
            var magnitudeSum = 0.0
            
            for (frame in 0 until minOf(numFrames, 10)) {
                for (k in 0 until frameSize / 2) {
                    val idx = frame * frameSize + k
                    if (idx < samples.size) {
                        val magnitude = abs(samples[idx].toDouble())
                        weightedSum += k * magnitude
                        magnitudeSum += magnitude
                    }
                }
            }
            features[2] = if (magnitudeSum > 0) (weightedSum / magnitudeSum).toFloat() else 0f
        }
        
        // 4-13. Spectral band energies (simplified MFCC-like)
        val bands = 10
        val bandSize = (samples.size / 2) / bands
        for (b in 0 until bands) {
            var energy = 0.0
            for (i in 0 until bandSize) {
                val idx = b * bandSize + i
                if (idx < samples.size) {
                    energy += (samples[idx] * samples[idx]).toDouble()
                }
            }
            features[3 + b] = sqrt(energy / bandSize).toFloat() / 32768f
        }
        
        return features
    }

    /**
     * Cosine similarity between two feature vectors
     */
    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denom = sqrt(normA) * sqrt(normB)
        return if (denom > 0) (dot / denom).coerceIn(0f, 1f) else 0f
    }

    private fun saveProfile(profile: FloatArray) {
        try {
            val file = File(context.filesDir, PROFILE_FILE)
            val buffer = ByteBuffer.allocate(profile.size * 4).order(ByteOrder.LITTLE_ENDIAN)
            for (f in profile) {
                buffer.putFloat(f)
            }
            FileOutputStream(file).use { it.write(buffer.array()) }
            Log.d(TAG, "Voice profile saved")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving voice profile: ${e.message}")
        }
    }

    fun isVoiceEnrolled(): Boolean = isEnrolled

    fun stop() {
        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {}
    }
}
