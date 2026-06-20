package com.maya.assistant.ai

import com.maya.assistant.models.CommandType
import com.maya.assistant.models.VoiceCommand

object IntentAnalyzer {

    // ===== ENGLISH PATTERNS =====
    private val OPEN_PATTERNS_EN = listOf("open", "launch", "start")
    private val CALL_PATTERNS_EN = listOf("call", "phone", "ring", "dial")
    private val WHATSAPP_CALL_EN = listOf("whatsapp call", "video call")
    private val MSG_PATTERNS_EN = listOf("message", "msg", "send")
    private val YOUTUBE_PATTERNS_EN = listOf("youtube", "video play", "play on youtube")
    private val SPOTIFY_PATTERNS_EN = listOf("spotify", "play music", "song")
    private val VOLUME_UP_EN = listOf("volume up", "louder")
    private val VOLUME_DOWN_EN = listOf("volume down", "lower")
    private val FLASHLIGHT_ON_EN = listOf("flashlight on", "torch on", "light on")
    private val FLASHLIGHT_OFF_EN = listOf("flashlight off", "torch off", "light off")

    // ===== BANGLA PATTERNS (বাংলা) =====
    private val OPEN_PATTERNS_BN = listOf(
        "খোলো", "খোল", "open", "চালু করো", "চালু", "স্টার্ট করো", "শুরু করো",
        "launch", "লঞ্চ করো", "দেখাও", "show"
    )
    private val CALL_PATTERNS_BN = listOf(
        "কল করো", "করো", "call", "ফোন করো", "ফোন", "ডায়াল করো",
        "রিং করো", "কল"
    )
    private val MSG_PATTERNS_BN = listOf(
        "মেসেজ করো", "মেসেজ", "বার্তা", "পাঠাও", "send", "লেখো",
        "message", "sms করো", "এসএমএস"
    )
    private val YOUTUBE_PATTERNS_BN = listOf(
        "ইউটিউব", "youtube", "ভিডিও চালাও", "গান শোনাও", "ভিডিও দেখাও",
        "মিউজিক শোনাও"
    )
    private val SPOTIFY_PATTERNS_BN = listOf(
        "স্পটিফাই", "spotify", "গান বাজাও", "মিউজিক চালাও", "গান"
    )
    private val VOLUME_UP_BN = listOf(
        "ভলিউম বাড়াও", "জোরে করো", "বেশি জোরে", "আরো জোরে", "লাউড করো",
        "volume up", "louder"
    )
    private val VOLUME_DOWN_BN = listOf(
        "ভলিউম কমাও", "কম জোরে", "ধীরে করো", "আরো কম", "সাইলেন্ট",
        "volume down", "lower"
    )
    private val FLASHLIGHT_ON_BN = listOf(
        "ফ্ল্যাশলাইট চালু", "টর্চ চালু", "লাইট চালু", "আলো চালু",
        "flashlight on", "torch on", "light on"
    )
    private val FLASHLIGHT_OFF_BN = listOf(
        "ফ্ল্যাশলাইট বন্ধ", "টর্চ বন্ধ", "লাইট বন্ধ", "আলো বন্ধ",
        "flashlight off", "torch off", "light off"
    )

    // ===== HINDI PATTERNS (हिंदी) =====
    private val OPEN_PATTERNS_HI = listOf(
        "खोलो", "खोल", "open", "चालू करो", "चालू", "स्टार्ट करो", "शुरू करो",
        "launch", "लॉन्च करो", "दिखाओ", "show"
    )
    private val CALL_PATTERNS_HI = listOf(
        "कॉल करो", "करो", "call", "फोन करो", "फोन", "डायल करो",
        "रिंग करो", "कॉल"
    )
    private val MSG_PATTERNS_HI = listOf(
        "मैसेज करो", "मैसेज", "संदेश", "भेजो", "send", "लिखो",
        "message", "sms करो", "एसएमएस"
    )
    private val YOUTUBE_PATTERNS_HI = listOf(
        "यूट्यूब", "youtube", "वीडियो चलाओ", "गाना सुनाओ", "वीडियो दिखाओ",
        "म्यूजिक सुनाओ"
    )
    private val SPOTIFY_PATTERNS_HI = listOf(
        "स्पॉटिफाई", "spotify", "गाना बजाओ", "म्यूजिक चलाओ", "गाना"
    )
    private val VOLUME_UP_HI = listOf(
        "वॉल्यूम बढ़ाओ", "जोरे करो", "ज्यादा जोरे", "और जोरे", "लाउड करो",
        "volume up", "louder"
    )
    private val VOLUME_DOWN_HI = listOf(
        "वॉल्यूम कम करो", "कम जोरे", "धीमे करो", "और कम", "साइलेंट",
        "volume down", "lower"
    )
    private val FLASHLIGHT_ON_HI = listOf(
        "फ्लैशलाइट चालू", "टॉर्च चालू", "लाइट चालू", "रोशनी चालू",
        "flashlight on", "torch on", "light on"
    )
    private val FLASHLIGHT_OFF_HI = listOf(
        "फ्लैशलाइट बंद", "टॉर्च बंद", "लाइट बंद", "रोशनी बंद",
        "flashlight off", "torch off", "light off"
    )

    // Combined patterns (all languages)
    private val OPEN_PATTERNS = OPEN_PATTERNS_EN + OPEN_PATTERNS_BN + OPEN_PATTERNS_HI
    private val CALL_PATTERNS = CALL_PATTERNS_EN + CALL_PATTERNS_BN + CALL_PATTERNS_HI
    private val WHATSAPP_CALL = WHATSAPP_CALL_EN + listOf("হোয়াটসঅ্যাপ কল", "व्हाट्सएप कॉल")
    private val MSG_PATTERNS = MSG_PATTERNS_EN + MSG_PATTERNS_BN + MSG_PATTERNS_HI
    private val YOUTUBE_PATTERNS = YOUTUBE_PATTERNS_EN + YOUTUBE_PATTERNS_BN + YOUTUBE_PATTERNS_HI
    private val SPOTIFY_PATTERNS = SPOTIFY_PATTERNS_EN + SPOTIFY_PATTERNS_BN + SPOTIFY_PATTERNS_HI
    private val VOLUME_UP = VOLUME_UP_EN + VOLUME_UP_BN + VOLUME_UP_HI
    private val VOLUME_DOWN = VOLUME_DOWN_EN + VOLUME_DOWN_BN + VOLUME_DOWN_HI
    private val FLASHLIGHT_ON = FLASHLIGHT_ON_EN + FLASHLIGHT_ON_BN + FLASHLIGHT_ON_HI
    private val FLASHLIGHT_OFF = FLASHLIGHT_OFF_EN + FLASHLIGHT_OFF_BN + FLASHLIGHT_OFF_HI

    fun analyze(text: String): VoiceCommand {
        val lower = text.lowercase().trim()

        // Structured command passthrough
        val structured = parseStructured(text)
        if (structured != null) return structured

        // Natural language analysis
        return when {
            FLASHLIGHT_ON.any { lower.contains(it) } ->
                VoiceCommand(text, CommandType.FLASHLIGHT_ON)

            FLASHLIGHT_OFF.any { lower.contains(it) } ->
                VoiceCommand(text, CommandType.FLASHLIGHT_OFF)

            VOLUME_UP.any { lower.contains(it) } ->
                VoiceCommand(text, CommandType.VOLUME_UP)

            VOLUME_DOWN.any { lower.contains(it) } ->
                VoiceCommand(text, CommandType.VOLUME_DOWN)

            WHATSAPP_CALL.any { lower.contains(it) } -> {
                val name = extractAfter(lower, WHATSAPP_CALL)
                VoiceCommand(text, CommandType.WHATSAPP_CALL, mapOf("name" to name))
            }

            CALL_PATTERNS.any { lower.startsWith(it) || lower.contains(" $it ") } -> {
                val name = extractAfter(lower, CALL_PATTERNS)
                VoiceCommand(text, CommandType.CALL, mapOf("name" to name))
            }

            MSG_PATTERNS.any { lower.contains(it) } -> {
                val parts = lower.split(" to ", " ko ", " কে ", " को ")
                val name = if (parts.size > 1) parts[1].split(" ")[0] else ""
                VoiceCommand(text, CommandType.WHATSAPP_MSG, mapOf("name" to name, "message" to text))
            }

            OPEN_PATTERNS.any { lower.startsWith(it) || lower.contains("$it ") } -> {
                val appName = extractAfter(lower, OPEN_PATTERNS)
                VoiceCommand(text, CommandType.OPEN_APP, mapOf("app" to appName))
            }

            YOUTUBE_PATTERNS.any { lower.contains(it) } -> {
                val song = extractAfter(lower, YOUTUBE_PATTERNS)
                VoiceCommand(text, CommandType.YOUTUBE_PLAY, mapOf("query" to song))
            }

            SPOTIFY_PATTERNS.any { lower.contains(it) } -> {
                val song = extractAfter(lower, SPOTIFY_PATTERNS)
                VoiceCommand(text, CommandType.SPOTIFY_PLAY, mapOf("query" to song))
            }

            else -> VoiceCommand(text, CommandType.CONVERSATION)
        }
    }

    private fun parseStructured(text: String): VoiceCommand? {
        val t = text.trim()
        return when {
            t.startsWith("OPEN_APP", true) -> {
                val app = t.removePrefix("OPEN_APP").removePrefix(":").trim()
                VoiceCommand(t, CommandType.OPEN_APP, mapOf("app" to app))
            }
            t.startsWith("CALL", true) && !t.startsWith("WHATSAPP_CALL", true) -> {
                val name = t.removePrefix("CALL").trim()
                VoiceCommand(t, CommandType.CALL, mapOf("name" to name))
            }
            t.startsWith("WHATSAPP_CALL", true) -> {
                val name = t.removePrefix("WHATSAPP_CALL").trim()
                VoiceCommand(t, CommandType.WHATSAPP_CALL, mapOf("name" to name))
            }
            t.startsWith("WHATSAPP_MSG", true) -> {
                val rest = t.removePrefix("WHATSAPP_MSG").trim()
                val parts = rest.split(" ", limit = 2)
                VoiceCommand(t, CommandType.WHATSAPP_MSG, mapOf(
                    "name" to (parts.getOrNull(0) ?: ""),
                    "message" to (parts.getOrNull(1) ?: "")
                ))
            }
            t.startsWith("YOUTUBE_PLAY", true) -> {
                val q = t.removePrefix("YOUTUBE_PLAY").trim()
                VoiceCommand(t, CommandType.YOUTUBE_PLAY, mapOf("query" to q))
            }
            t.startsWith("SPOTIFY_PLAY", true) -> {
                val q = t.removePrefix("SPOTIFY_PLAY").trim()
                VoiceCommand(t, CommandType.SPOTIFY_PLAY, mapOf("query" to q))
            }
            t.equals("FLASHLIGHT_ON", true) -> VoiceCommand(t, CommandType.FLASHLIGHT_ON)
            t.equals("FLASHLIGHT_OFF", true) -> VoiceCommand(t, CommandType.FLASHLIGHT_OFF)
            t.equals("VOLUME_UP", true) -> VoiceCommand(t, CommandType.VOLUME_UP)
            t.equals("VOLUME_DOWN", true) -> VoiceCommand(t, CommandType.VOLUME_DOWN)
            else -> null
        }
    }

    private fun extractAfter(text: String, patterns: List<String>): String {
        for (p in patterns) {
            val idx = text.indexOf(p)
            if (idx != -1) {
                return text.substring(idx + p.length).trim()
            }
        }
        return ""
    }
}
