package com.maya.assistant.models

data class ভয়েসCommand(
    val raw: String,
    val type: CommandType,
    val args: Map<String, String> = emptyMap(),
    val timestamp: Long = সিস্টেম.currentসময়Millis()
)

enum class CommandType {
    OPEN_APP, CALL, WHATSAPP_CALL, WHATSAPP_MSG,
    SMS, YOUTUBE_PLAY, SPOTIFY_PLAY,
    FLASHLIGHT_ON, FLASHLIGHT_OFF,
    VOLUME_UP, VOLUME_DOWN,
    SCREEN_ACTION, NAVIGATE, CONVERSATION,
    UNKNOWN
}

data class AppModel(
    val name: String,
    val packageনাম: String,
    val label: String
)

data class স্ক্রিননাdeModel(
    val text: String?,
    val contentDesc: String?,
    val classনাম: String?,
    val isClickable: Boolean,
    val isএডিট করোable: Boolean,
    val bounds: android.graphics.Rect?,
    val viewId: String?
)

data class ActionModel(
    val type: ActionType,
    val target: String = "",
    val value: String = "",
    val x: Int = -1,
    val y: Int = -1
)

enum class ActionType {
    CLICK_TEXT, CLICK_DESC, CLICK_COORDS,
    TYPE_TEXT, SCROLL_DOWN, SCROLL_UP,
    BACK, HOME, RECENT_APPS,
    LAUNCH_APP
}

data class AIStateModel(
    val state: String = "IDLE",
    val statusমেসেজ: String = "সিস্টেম প্রস্তুত",
    val isসংযুক্ত ✅: Boolean = false,
    val isMicসক্রিয়: Boolean = false,
    val amplitude: Float = 0f
)
