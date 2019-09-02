package com.retrox.aodmod.service.alarm.proxy

import com.retrox.aodmod.MainHook
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.service.alarm.LocalAlarmManager
import com.retrox.aodmod.service.alarm.LocalAlarmTimeOutTicker
import com.retrox.aodmod.service.alarm.LocalChoreManager

object LocalAlarmProxy : TickAlarm {
    val tickAlarmList = listOf<TickAlarm>(LocalChoreManager, LocalAlarmManager, LocalAlarmTimeOutTicker)

    override fun startTick() {
        val mode = XPref.getAodAlarmMode()
        MainHook.logD("LocalAlarmProxy StartTick, Mode: $mode")
        when(mode) {
            "SYSTEM" -> return
            "AlarmManager-1min" -> LocalAlarmManager.startTick()
            "Chore" -> LocalChoreManager.startTick()
            "Alarm-TimeOutMode" -> LocalAlarmTimeOutTicker.startTick()
        }
    }

    override fun stopTick() {
        tickAlarmList.forEach {
            it.stopTick()
        }
    }
}

interface TickAlarm {
    fun startTick()
    fun stopTick()
}