package com.maya.assistant.ai

object AIResponseManager {

    private val THINKING_FILTERS = listOf(
        // English filters
        "Responding to the Greeting",
        "I've registered the Hindi greeting.",
        "Now, I'm formulating",
        "Interpreting user command",
        "adhering to appropriate cultural etiquette.",
        "Formulating",
        "Let me think",
        "Processing",
        "Responding to",
        "I've registered",
        // Bangla filters (বাংলা)
        "উত্তর দিচ্ছি",
        "ভাবছি",
        "প্রসেসিং",
        "বিশ্লেষণ করছি"
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
