package com.retrox.aodmod.state

import android.arch.lifecycle.MutableLiveData

object AodClockTick {
    val tickLiveData = MutableLiveData<Any>() // 转发Broadcast Receiver的Tick
}