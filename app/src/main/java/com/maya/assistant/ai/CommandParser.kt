package com.maya.assistant.ai

import com.maya.assistant.model.AppCommand

class CommandParser {

    private val appMap = mapOf(
        "youtube" to "com.google.android.youtube",
        "whatsapp" to "com.whatsapp",
        "instagram" to "com.instagram.android",
        "facebook" to "com.facebook.katana",
        "chrome" to "com.android.chrome",
        "gmail" to "com.google.android.gm",
        "maps" to "com.google.android.apps.maps",
        "google maps" to "com.google.android.apps.maps",
        "spotify" to "com.spotify.music",
        "netflix" to "com.netflix.mediaclient",
        "twitter" to "com.twitter.android",
        "x" to "com.twitter.android",
        "telegram" to "org.telegram.messenger",
        "snapchat" to "com.snapchat.android",
        "settings" to "com.android.settings",
        "calculator" to "com.google.android.calculator",
        "calendar" to "com.google.android.calendar",
        "clock" to "com.google.android.deskclock",
        "phone" to "com.android.dialer",
        "contacts" to "com.android.contacts",
        "play store" to "com.android.vending",
        "amazon" to "in.amazon.mShop.android.shopping",
        "flipkart" to "com.flipkart.android",
        "paytm" to "net.one97.paytm",
        "phonepe" to "com.phonepe.app",
        "gpay" to "com.google.android.apps.nbu.paisa.user",
        "google pay" to "com.google.android.apps.nbu.paisa.user",
        "zoom" to "us.zoom.videomeetings",
        "meet" to "com.google.android.apps.meetings",
        "teams" to "com.microsoft.teams",
        "tiktok" to "com.zhiliaoapp.musically",
        "discord" to "com.discord",
        "linkedin" to "com.linkedin.android",
        "camera" to "com.android.camera",
        "gallery" to "com.google.android.apps.photos",
        "photos" to "com.google.android.apps.photos",
        "file manager" to "com.android.filemanager",
        "files" to "com.google.android.apps.nbu.files",
        "music" to "com.google.android.music",
        "video" to "com.google.android.videos",
        "drive" to "com.google.android.apps.docs",
        "docs" to "com.google.android.apps.docs.editors.docs",
        "sheets" to "com.google.android.apps.docs.editors.sheets",
        "slides" to "com.google.android.apps.docs.editors.slides",
        "keep" to "com.google.android.keep",
        "notes" to "com.google.android.keep",
        "weather" to "com.google.android.apps.weather",
        "news" to "com.google.android.apps.magazines",
        "podcasts" to "com.google.android.apps.podcasts",
        "books" to "com.google.android.apps.books",
        "play books" to "com.google.android.apps.books",
        "authenticator" to "com.google.android.apps.authenticator2",
        "translate" to "com.google.android.apps.translate",
        "google translate" to "com.google.android.apps.translate",
        "fitness" to "com.google.android.apps.fitness",
        "fit" to "com.google.android.apps.fitness",
        "wallet" to "com.google.android.apps.walletnfcrel",
        "google wallet" to "com.google.android.apps.walletnfcrel"
    )

    fun parse(text: String): AppCommand? {
        val lower = text.lowercase().trim()

        // === OPEN APP ===
        // English: "open youtube", "launch chrome", "start spotify"
        // Bangla: "youtube kholo", "chrome open karo", "spotify shuru koro"
        // Hindi: "youtube kholo", "chrome launch karo"
        val openPatterns = listOf(
            Regex("""^open\s+(.+)$"""),
            Regex("""^launch\s+(.+)$"""),
            Regex("""^start\s+(.+)$"""),
            Regex("""^(.+)\s+kholo$"""),
            Regex("""^(.+)\s+khola$"""),
            Regex("""^(.+)\s+open\s+karo$"""),
            Regex("""^(.+)\s+shuru\s+karo$"""),
            Regex("""^(.+)\s+start\s+karo$"""),
            Regex("""^(.+)\s+chalao$"""),
            Regex("""^(.+)\s+chola$""")
        )
        for (pattern in openPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val appName = match.groupValues[1].trim()
                val packageName = appMap[appName]
                return AppCommand(
                    type = "OPEN_APP",
                    params = mapOf("app_name" to appName, "package_name" to (packageName ?: ""))
                )
            }
        }

        // === CLOSE APP ===
        // English: "close youtube", "exit chrome"
        // Bangla: "whatsapp band karo", "youtube bondho koro"
        // Hindi: "whatsapp band karo", "youtube close karo"
        val closePatterns = listOf(
            Regex("""^close\s+(.+)$"""),
            Regex("""^exit\s+(.+)$"""),
            Regex("""^quit\s+(.+)$"""),
            Regex("""^(.+)\s+band\s+karo$"""),
            Regex("""^(.+)\s+bondho\s+koro$"""),
            Regex("""^(.+)\s+close\s+karo$"""),
            Regex("""^(.+)\s+band\s+koro$"""),
            Regex("""^(.+)\s+off\s+karo$""")
        )
        for (pattern in closePatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val appName = match.groupValues[1].trim()
                return AppCommand(
                    type = "CLOSE_APP",
                    params = mapOf("app_name" to appName)
                )
            }
        }

        // === CALL ===
        // English: "call mom", "call +919876543210"
        // Bangla: "amma ke call karo", "baba ke phone koro"
        // Hindi: "mummy ko call karo", "papa ko phone karo"
        val callPatterns = listOf(
            Regex("""^call\s+(.+)$"""),
            Regex("""^phone\s+(.+)$"""),
            Regex("""^(.+)\s+ko\s+call\s+karo$"""),
            Regex("""^(.+)\s+ke\s+call\s+karo$"""),
            Regex("""^(.+)\s+ke\s+phone\s+karo$"""),
            Regex("""^(.+)\s+ko\s+phone\s+karo$"""),
            Regex("""^(.+)\s+ke\s+dial\s+karo$"""),
            Regex("""^(.+)\s+ko\s+dial\s+karo$"""),
            Regex("""^(.+)\s+call\s+dhao$"""),
            Regex("""^(.+)\s+call\s+kora$"""),
            Regex("""^(.+)\s+phone\s+kora$"""),
            Regex("""^call\s+karo\s+(.+)$"""),
            Regex("""^phone\s+koro\s+(.+)$""")
        )
        for (pattern in callPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val target = match.groupValues[1].trim()
                return AppCommand(
                    type = "CALL",
                    params = mapOf("target" to target)
                )
            }
        }

        // === SMS ===
        // English: "send sms to mom", "message mom"
        // Bangla: "amma ke sms karo", "baba ke message dao"
        // Hindi: "mummy ko message bhejo"
        val smsPatterns = listOf(
            Regex("""^send\s+sms\s+to\s+(.+)$"""),
            Regex("""^message\s+(.+)$"""),
            Regex("""^text\s+(.+)$"""),
            Regex("""^(.+)\s+ke\s+sms\s+karo$"""),
            Regex("""^(.+)\s+ko\s+sms\s+bhejo$"""),
            Regex("""^(.+)\s+ke\s+message\s+karo$"""),
            Regex("""^(.+)\s+ko\s+message\s+bhejo$"""),
            Regex("""^(.+)\s+ke\s+msg\s+karo$"""),
            Regex("""^(.+)\s+ko\s+msg\s+bhejo$"""),
            Regex("""^sms\s+karo\s+(.+)$"""),
            Regex("""^message\s+dhao\s+(.+)$""")
        )
        for (pattern in smsPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val target = match.groupValues[1].trim()
                return AppCommand(
                    type = "SMS",
                    params = mapOf("target" to target, "message" to "")
                )
            }
        }

        // === WHATSAPP MESSAGE ===
        // English: "whatsapp mom", "send whatsapp to mom"
        // Bangla: "amma ke whatsapp karo", "baba ke whatsapp message dao"
        // Hindi: "mummy ko whatsapp karo"
        val whatsappMsgPatterns = listOf(
            Regex("""^whatsapp\s+(.+)$"""),
            Regex("""^send\s+whatsapp\s+to\s+(.+)$"""),
            Regex("""^(.+)\s+ke\s+whatsapp\s+karo$"""),
            Regex("""^(.+)\s+ko\s+whatsapp\s+bhejo$"""),
            Regex("""^(.+)\s+ke\s+whatsapp\s+message\s+karo$"""),
            Regex("""^(.+)\s+ko\s+whatsapp\s+message\s+bhejo$"""),
            Regex("""^whatsapp\s+message\s+(.+)$""")
        )
        for (pattern in whatsappMsgPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val target = match.groupValues[1].trim()
                return AppCommand(
                    type = "WHATSAPP_MSG",
                    params = mapOf("target" to target, "message" to "")
                )
            }
        }

        // === WHATSAPP CALL ===
        val whatsappCallPatterns = listOf(
            Regex("""^whatsapp\s+call\s+(.+)$"""),
            Regex("""^(.+)\s+ko\s+whatsapp\s+call\s+karo$"""),
            Regex("""^(.+)\s+ke\s+whatsapp\s+call\s+karo$"""),
            Regex("""^(.+)\s+ke\s+whatsapp\s+phone\s+karo$""")
        )
        for (pattern in whatsappCallPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val target = match.groupValues[1].trim()
                return AppCommand(
                    type = "WHATSAPP_CALL",
                    params = mapOf("target" to target)
                )
            }
        }

        // === PRIME CONTACT CALL ===
        // English: "call my close friend", "call my second contact"
        // Bangla: "amar close friend ke call karo", "amar 2nd contact ke call koro"
        // Hindi: "mere close friend ko call karo"
        val primeCallPatterns = listOf(
            Regex("""^call\s+my\s+(.+)$"""),
            Regex("""^my\s+(.+)\s+ko\s+call\s+karo$"""),
            Regex("""^amar\s+(.+)\s+ke\s+call\s+karo$"""),
            Regex("""^mera\s+(.+)\s+ko\s+call\s+karo$"""),
            Regex("""^meri\s+(.+)\s+ko\s+call\s+karo$"""),
            Regex("""^amar\s+(.+)\s+ke\s+phone\s+karo$"""),
            Regex("""^close\s+friend\s+call\s+karo$"""),
            Regex("""^best\s+friend\s+call\s+karo$""")
        )
        for (pattern in primeCallPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val contactDesc = match.groupValues[1].trim()
                val index = getPrimeContactIndex(contactDesc)
                return AppCommand(
                    type = "PRIME_CALL",
                    params = mapOf("index" to index.toString(), "contact_desc" to contactDesc)
                )
            }
        }

        // === PRIME CONTACT MESSAGE ===
        val primeMsgPatterns = listOf(
            Regex("""^message\s+my\s+(.+)$"""),
            Regex("""^my\s+(.+)\s+ko\s+message\s+bhejo$"""),
            Regex("""^amar\s+(.+)\s+ke\s+message\s+karo$"""),
            Regex("""^mera\s+(.+)\s+ko\s+message\s+bhejo$"""),
            Regex("""^meri\s+(.+)\s+ko\s+message\s+bhejo$"""),
            Regex("""^close\s+friend\s+ke\s+message\s+karo$"""),
            Regex("""^close\s+friend\s+ke\s+sms\s+karo$""")
        )
        for (pattern in primeMsgPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val contactDesc = match.groupValues[1].trim()
                val index = getPrimeContactIndex(contactDesc)
                return AppCommand(
                    type = "PRIME_MSG",
                    params = mapOf("index" to index.toString(), "contact_desc" to contactDesc)
                )
            }
        }

        // === VOLUME ===
        if (lower.contains("volume up") || lower.contains("volume badhao") ||
            lower.contains("volume barao") || lower.contains("awaz badhao") ||
            lower.contains("aawaz barao") || lower.contains("volume increase")) {
            return AppCommand(type = "VOLUME_UP", params = emptyMap())
        }
        if (lower.contains("volume down") || lower.contains("volume kam karo") ||
            lower.contains("volume kom karo") || lower.contains("awaz kam karo") ||
            lower.contains("aawaz kom karo") || lower.contains("volume decrease") ||
            lower.contains("volume komano")) {
            return AppCommand(type = "VOLUME_DOWN", params = emptyMap())
        }
        if (lower.contains("mute") || lower.contains("shanti karo") ||
            lower.contains("chup karo") || lower.contains("silent")) {
            return AppCommand(type = "MUTE", params = emptyMap())
        }

        // === FLASHLIGHT / TORCH ===
        if (lower.contains("torch on") || lower.contains("flashlight on") ||
            lower.contains("torch on karo") || lower.contains("flashlight on karo") ||
            lower.contains("torch jalao") || lower.contains("flashlight jalao") ||
            lower.contains("light on") || lower.contains("bati jalao")) {
            return AppCommand(type = "FLASHLIGHT_ON", params = emptyMap())
        }
        if (lower.contains("torch off") || lower.contains("flashlight off") ||
            lower.contains("torch off karo") || lower.contains("flashlight off karo") ||
            lower.contains("torch bondho") || lower.contains("flashlight bondho") ||
            lower.contains("light off") || lower.contains("bati bondho")) {
            return AppCommand(type = "FLASHLIGHT_OFF", params = emptyMap())
        }

        // === WIFI ===
        if (lower.contains("wifi on") || lower.contains("wifi on karo") ||
            lower.contains("wifi chalu karo") || lower.contains("wifi chalao")) {
            return AppCommand(type = "WIFI_ON", params = emptyMap())
        }
        if (lower.contains("wifi off") || lower.contains("wifi off karo") ||
            lower.contains("wifi bondho karo") || lower.contains("wifi band karo")) {
            return AppCommand(type = "WIFI_OFF", params = emptyMap())
        }

        // === BLUETOOTH ===
        if (lower.contains("bluetooth on") || lower.contains("bluetooth on karo") ||
            lower.contains("bluetooth chalu karo") || lower.contains("bluetooth chalao")) {
            return AppCommand(type = "BLUETOOTH_ON", params = emptyMap())
        }
        if (lower.contains("bluetooth off") || lower.contains("bluetooth off karo") ||
            lower.contains("bluetooth bondho karo") || lower.contains("bluetooth band karo")) {
            return AppCommand(type = "BLUETOOTH_OFF", params = emptyMap())
        }

        // === SCREENSHOT ===
        if (lower.contains("screenshot") || lower.contains("screen shot") ||
            lower.contains("capture screen") || lower.contains("screen capture")) {
            return AppCommand(type = "SCREENSHOT", params = emptyMap())
        }

        // === MUSIC CONTROL ===
        if (lower.contains("play music") || lower.contains("gan bajao") ||
            lower.contains("music shuru") || lower.contains("gaan shuru") ||
            lower.contains("play song") || lower.contains("gana bajao")) {
            return AppCommand(type = "PLAY_MUSIC", params = emptyMap())
        }
        if (lower.contains("pause music") || lower.contains("stop music") ||
            lower.contains("music roko") || lower.contains("gan bondho") ||
            lower.contains("music rakho") || lower.contains("gana bondho")) {
            return AppCommand(type = "PAUSE_MUSIC", params = emptyMap())
        }
        if (lower.contains("next song") || lower.contains("next track") ||
            lower.contains("porer gan") || lower.contains("next music") ||
            lower.contains("porer gaan")) {
            return AppCommand(type = "NEXT_SONG", params = emptyMap())
        }
        if (lower.contains("previous song") || lower.contains("previous track") ||
            lower.contains("ager gan") || lower.contains("previous music") ||
            lower.contains("ager gaan")) {
            return AppCommand(type = "PREV_SONG", params = emptyMap())
        }

        // No command matched — let MAYA handle as conversation
        return null
    }

    private fun getPrimeContactIndex(desc: String): Int {
        val lower = desc.lowercase()
        return when {
            lower.contains("first") || lower.contains("1st") || lower.contains("1") ||
            lower.contains("close friend") || lower.contains("best friend") ||
            lower.contains("priya") || lower.contains("love") ||
            lower.contains("jaan") || lower.contains("favorite") -> 0

            lower.contains("second") || lower.contains("2nd") || lower.contains("2") -> 1
            lower.contains("third") || lower.contains("3rd") || lower.contains("3") -> 2
            lower.contains("fourth") || lower.contains("4th") || lower.contains("4") -> 3
            lower.contains("fifth") || lower.contains("5th") || lower.contains("5") -> 4
            else -> 0
        }
    }

    // ===== SCREEN-AWARE COMMANDS =====
    // These return commands that use screen reading for context

    /**
     * Parse screen-aware voice commands
     * Called when regular parse returns null but user wants to interact with screen
     */
    fun parseScreenCommand(text: String): AppCommand? {
        val lower = text.lowercase().trim()

        // Facebook / Social Media Post
        // English: "post on facebook", "write a post", "share on facebook"
        // Bangla: "facebook e post karo", "fb te post kor", "post dao facebook e"
        // Hindi: "facebook mein post karo", "fb mein likh do"
        if (lower.contains("post") && (lower.contains("facebook") || lower.contains("fb") || lower.contains("facebook e"))) {
            val content = extractPostContent(lower)
            return AppCommand(
                type = "SCREEN_FACEBOOK_POST",
                params = mapOf("content" to content, "raw" to text)
            )
        }

        // Click/tap on screen elements
        // English: "click on X", "tap on X", "press X"
        // Bangla: "X e click karo", "X e tap koro", "X চাপ দাও"
        // Hindi: "X pe click karo", "X dabao"
        val clickPatterns = listOf(
            Regex("""^click\s+on\s+(.+)$"""),
            Regex("""^tap\s+on\s+(.+)$"""),
            Regex("""^press\s+(.+)$"""),
            Regex("""^(.+)\s+e\s+click\s+karo$"""),
            Regex("""^(.+)\s+pe\s+click\s+karo$"""),
            Regex("""^(.+)\s+e\s+tap\s+koro$"""),
            Regex("""^(.+)\s+pe\s+tap\s+karo$"""),
            Regex("""^click\s+karo\s+(.+)$"""),
            Regex("""^tap\s+koro\s+(.+)$""")
        )
        for (pattern in clickPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val target = match.groupValues[1].trim()
                return AppCommand(
                    type = "SCREEN_CLICK_TEXT",
                    params = mapOf("target" to target)
                )
            }
        }

        // Type/Write on screen
        // English: "type X", "write X", "enter X"
        // Bangla: "X type karo", "X likho", "X lekho"
        // Hindi: "X type karo", "X likh do"
        val typePatterns = listOf(
            Regex("""^type\s+(.+)$"""),
            Regex("""^write\s+(.+)$"""),
            Regex("""^enter\s+(.+)$"""),
            Regex("""^likho\s+(.+)$"""),
            Regex("""^lekho\s+(.+)$"""),
            Regex("""^(.+)\s+type\s+karo$"""),
            Regex("""^(.+)\s+likho$"""),
            Regex("""^(.+)\s+lekho$"""),
            Regex("""^type\s+karo\s+(.+)$""")
        )
        for (pattern in typePatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val content = match.groupValues[1].trim()
                return AppCommand(
                    type = "SCREEN_TYPE_TEXT",
                    params = mapOf("content" to content)
                )
            }
        }

        // Scroll screen
        // English: "scroll down", "scroll up", "page down"
        // Bangla: "scroll koro", "niche scroll koro", "upore scroll koro"
        // Hindi: "scroll karo", "neeche scroll karo"
        if (lower.contains("scroll down") || lower.contains("niche scroll") ||
            lower.contains("neeche scroll") || lower.contains("scroll niche")) {
            return AppCommand(type = "SCREEN_SCROLL", params = mapOf("direction" to "down"))
        }
        if (lower.contains("scroll up") || lower.contains("upore scroll") ||
            lower.contains("oopar scroll") || lower.contains("scroll upore")) {
            return AppCommand(type = "SCREEN_SCROLL", params = mapOf("direction" to "up"))
        }

        // Read screen
        // English: "read screen", "what's on screen", "screen ta kemon"
        // Bangla: "screen ta bolo", "screen e ki ache", "screen poro"
        // Hindi: "screen kya hai", "screen batao"
        if (lower.contains("read screen") || lower.contains("screen") &&
            (lower.contains("bolo") || lower.contains("batao") || lower.contains("kemon") ||
             lower.contains("ki ache") || lower.contains("kya hai") || lower.contains("poro"))) {
            return AppCommand(type = "SCREEN_READ", params = emptyMap())
        }

        // Search on current app
        // English: "search for X", "search X"
        // Bangla: "X search karo", "X khujo"
        // Hindi: "X search karo"
        val searchPatterns = listOf(
            Regex("""^search\s+for\s+(.+)$"""),
            Regex("""^search\s+(.+)$"""),
            Regex("""^(.+)\s+search\s+karo$"""),
            Regex("""^(.+)\s+khujo$""")
        )
        for (pattern in searchPatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val query = match.groupValues[1].trim()
                return AppCommand(
                    type = "SCREEN_SEARCH",
                    params = mapOf("query" to query)
                )
            }
        }

        return null
    }

    private fun extractPostContent(text: String): String {
        // Try to extract actual post content from the voice command
        val patterns = listOf(
            Regex("""post\s+(?:on\s+)?(?:facebook|fb)\s*(?:that\s+)?["']?(.+?)["']?$"""),
            Regex("""(?:facebook|fb)\s*(?:te|e|mein)\s*post\s*(?:karo|kor|do)\s*["']?(.+?)["']?$"""),
            Regex("""post\s+(?:karo|kor|do)\s*(?:facebook|fb)\s*(?:te|e|mein)?\s*["']?(.+?)["']?$"""),
            Regex("""post\s+(?:content|text)?\s*[:=]?\s*["']?(.+?)["']?$""")
        )
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                val content = match.groupValues[1].trim()
                if (content.isNotBlank() && content.length > 3) return content
            }
        }
        return ""
    }
}
