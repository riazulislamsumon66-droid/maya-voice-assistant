package com.maya.assistant.automation

object অটোmationStateManager {
    enum class State { IDLE, RUNNING, PAUSED, ERROR }

    @Volatile var state: State = State.IDLE
        private set

    fun setRunning() { state = State.RUNNING }
    fun setIdle() { state = State.IDLE }
    fun setবিরতিd() { state = State.PAUSED }
    fun setসমস্যা() { state = State.ERROR }
    fun isRunning() = state == State.RUNNING
}
