package com.maya.assistant.ai

data class Turn(val role: String, val text: String, val timestamp: Long = সিস্টেম.currentসময়Millis())

object Conversationমেমোরি {
    private val history = mutableListOf<Turn>()
    private const val MAX_TURNS = 20

    fun addব্যবহারকারী(text: String) {
        history.add(Turn("user", text))
        trim()
    }

    fun addঅ্যাসিস্ট্যান্ট(text: String) {
        history.add(Turn("assistant", text))
        trim()
    }

    fun getRecent(n: Int = 6): List<Turn> = history.takeLast(n)

    fun clear() = history.clear()

    private fun trim() {
        if (history.size > MAX_TURNS) {
            history.removeAt(0)
        }
    }

    fun getContextSummary(): String = getRecent().joinToString("\n") {
        "${it.role}: ${it.text}"
    }
}
