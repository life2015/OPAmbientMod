package com.retrox.aodmod.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.state.AodState

class SleepModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.aodmod.sleep.on") {
            AodState.sleepMode = true
        } else if (intent.action == "com.aodmod.sleep.off") {
            AodState.sleepMode = false
        }

        MainHook.logD("SleepMode : ${intent.action}")
    }
}