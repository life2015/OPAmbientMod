package com.retrox.aodmod.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.state.AodClockTick

class ClockTickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AodClockTick.tickLiveData.postValue("Tick!")
        MainHook.logD("Tick Intent Received")
    }
}