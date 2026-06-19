package com.maya.assistant.model

data class AppCommand(
    val type: String,
    val params: Map<String, String>
)
