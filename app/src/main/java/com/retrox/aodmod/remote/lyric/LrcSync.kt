package com.retrox.aodmod.remote.lyric

import android.view.Choreographer
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.extensions.LiveEvent

object LrcSync {
    private val choreographer = Choreographer.getInstance()
    private var syncStartTime: Long = 0
    private var syncStartTimeAbsMills = 0L
    private var rows: List<LrcRow>? = null


    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            choreographer.removeFrameCallback(this)

            val timeOffset = System.currentTimeMillis() - syncStartTimeAbsMills // 计算时间比起首次同步的偏移
            val currentTime = syncStartTime + timeOffset // 加上初始同步时间
            val row = getSuggestRow(currentTime)
            if (currentLrcRowLive.value != row) {
                currentLrcRowLive.value = row
//                MainHook.logD("LRC Sync $currentTime $row ")
            }

            choreographer.postFrameCallbackDelayed(this, 500L)
        }
    }

    val currentLrcRowLive = LiveEvent<LrcRow>()

    fun startLrcSync(playTime: Long) {
        syncStartTime = playTime
        syncStartTimeAbsMills = System.currentTimeMillis()
        choreographer.postFrameCallback(frameCallback)
    }

    fun stopSync() {
        choreographer.removeFrameCallback(frameCallback)
        currentLrcRowLive.value = null
    }

    fun applyRawLrc(rawLrc: String) {
        val temp = DefaultLrcBuilder.getLrcRows(rawLrc)
        MainHook.logD("applyRawLrc Rows , size: $temp")
        rows = temp
    }

    fun clearLrcRows() {
        rows = null
    }

    fun getSuggestRow(timeOffset: Long) = rows?.getSuggestRow(timeOffset)

    private fun List<LrcRow>.getSuggestRow(timeOffset: Long) = find {
        it.startTime < timeOffset && it.endTime > timeOffset
    }

}