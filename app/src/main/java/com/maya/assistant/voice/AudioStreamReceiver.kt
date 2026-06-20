package com.maya.assistant.voice

class AudioStreamReceiver(private val player: AudioPlayer) {

    fun receive(data: ByteArray) {
        if (data.isNotEmpty()) player.playChunk(data)
    }

    fun clear() = player.clearAndFlush()
}
