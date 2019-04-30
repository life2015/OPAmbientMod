package com.retrox.aodmod.state

import android.app.AndroidAppHelper
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.os.SystemClock
import android.view.Choreographer
import com.retrox.aodmod.MainHook

/**
 * 因为系统广播不靠谱 所以用自己的Handler了
 */
object AodClockTick {
    val tickLiveData = object : MutableLiveData<Any>() {
        override fun onActive() {
            super.onActive()
            startTick()
            MainHook.logD("Aod Clock start Tick")
        }

        override fun onInactive() {
            super.onInactive()
            stopTick()
            MainHook.logD("Aod Clock stop Tick")
        }
    }

    private val delay = 60 * 1000L
    private val runnable = object : Runnable {
        override fun run() {
            AodClockTick.tickLiveData.postValue("Tick!")
            MainHook.logD("Tick By Handler Ticker")
            val now = SystemClock.uptimeMillis()
            val time = (1000 - (now % 1000)) + now + 60*1000L
            handler.postAtTime(this, time)
        }
    }

    private val handler = Handler(AndroidAppHelper.currentApplication().mainLooper)

    private fun startTick() {
        handler.postDelayed(runnable, delay)
    }

    private fun stopTick() {
        handler.removeCallbacks(runnable)
    }
}