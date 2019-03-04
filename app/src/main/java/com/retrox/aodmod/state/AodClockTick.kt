package com.retrox.aodmod.state

import android.arch.lifecycle.MutableLiveData
import android.os.Handler
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

    private val delay = 40 * 1000L
    private val runnable = object : Runnable {
        override fun run() {
            /* do what you need to do */
            AodClockTick.tickLiveData.postValue("Tick!")
            MainHook.logD("Tick By Handler Ticker")
            /* and here comes the "trick" */
            handler.postDelayed(this, delay)
        }
    }

    private val handler = Handler()

    private fun startTick() {
        handler.postDelayed(runnable, delay)
    }

    private fun stopTick() {
        handler.removeCallbacks(runnable)
    }
}