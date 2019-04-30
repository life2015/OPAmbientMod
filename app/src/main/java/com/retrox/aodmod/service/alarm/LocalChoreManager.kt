package com.retrox.aodmod.service.alarm

import android.view.Choreographer
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.service.alarm.proxy.TickAlarm
import com.retrox.aodmod.state.AodClockTick

/**
 * 使用Chore来计时 更加准时
 */
object LocalChoreManager: TickAlarm {
    private val choreographer = Choreographer.getInstance()

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            AodClockTick.tickLiveData.postValue("Tick!")
            MainHook.logD("Tick From Choreographer")
            choreographer.removeFrameCallback(this)
            choreographer.postFrameCallbackDelayed(this, 60000L)
        }
    }

    override fun startTick() {
        val currentMins = System.currentTimeMillis() / 60000L
        val needCurrentMills = (currentMins + 1) * 60000L
        val window = needCurrentMills - System.currentTimeMillis()
        MainHook.logD("Choreographer Start Tick 时间对齐: ${window / 1000L}")
        choreographer.postFrameCallbackDelayed(frameCallback, window + 3000L)
    }

    override fun stopTick() {
        MainHook.logD("Choreographer Stop Tick")
        choreographer.removeFrameCallback(frameCallback)
    }

}