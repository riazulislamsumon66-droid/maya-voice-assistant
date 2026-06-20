package com.maya.assistant.voice

/**
 * Receives audio chunks from Gemini and routes to AudioPlayer
 */
class AudioStreamReceiver(private val player: AudioPlayer) {

    fun receive(data: ByteArray) {
        if (data.isনাtEmpty()) player.playChunk(data)
    }

    fun clear() = player.clearAndবন্ধ করো()
}
