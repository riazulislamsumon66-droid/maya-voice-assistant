package com.maya.assistant.ai

object AIResponseManager {

    private val THINKING_FILTERS = listOf(
        "Greeting এ respond করছি",
        "I've registered the হিন্দি greeting.",
        "নাw, I'm formulating",
        "User command interpret করছি",
        "উপযুক্ত cultural etiquette মেনে।",
        "Formulating",
        "Let me think",
        "Processing"
    )

    fun clean(raw: String): String {
        var result = raw
            .replace("```", "")
            .replace("**", "")
            .replace("*", "")
        for (filter in THINKING_FILTERS) {
            result = result.replace(filter, "")
        }
        return result.trim()
    }

    fun extractCommand(text: String): String? {
        val commands = listOf(
            "OPEN_APP", "CALL", "WHATSAPP_CALL", "WHATSAPP_MSG",
            "YOUTUBE_PLAY", "SPOTIFY_PLAY", "FLASHLIGHT_ON",
            "FLASHLIGHT_OFF", "VOLUME_UP", "VOLUME_DOWN", "SMS"
        )
        val upper = text.uppercase()
        for (cmd in commands) {
            val index = upper.indexOf(cmd)
            if (index != -1) {
                return text.substring(index).trim().lines().first().replace("`", "").trim()
            }
        }
        return null
    }
}
