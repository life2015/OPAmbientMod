package com.retrox.aodmod.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.state.AodState

class ZenModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val zen = intent.getIntExtra("switch_state", 0)
        AodState.aodThreeKeyState.postValue(zen)
    }
}