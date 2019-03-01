package com.retrox.aodmod.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.state.AodState

class PowerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: ""
        if (action == "android.intent.action.BATTERY_CHANGED") {
            val level = (100.0f * intent.getIntExtra("level", 0) / intent.getIntExtra("scale", 100)).toInt()
            val plugged = intent.getIntExtra("plugged", 0) != 0
            val status = intent.getIntExtra("status", 1)
            val charged = status == 5
            val charging = status == 2
            val fastCharge = intent.getIntExtra("fastcharge_status", 0) > 0

            MainHook.logD("电池信息：容量:$level 插入状态:$plugged Status:$status 充电完成:$charged 充电中:$charging Dash闪充:$fastCharge")

            val powerData = PowerData(level, plugged, status, charged, charging, fastCharge)
            if (powerData != AodState.powerState.value) {
                AodState.powerState.postValue(powerData)
            }
        }
    }

}

data class PowerData(val level: Int, val plugged: Boolean, val status: Int, val charged: Boolean, val charging: Boolean, val fastCharge: Boolean)