package com.retrox.aodmod.service.alarm

import android.app.AlarmManager
import android.app.AlarmManager.OnAlarmListener
import android.content.Context
import android.os.Handler
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.service.alarm.AlarmTimeout.MODE_IGNORE_IF_SCHEDULED
import com.retrox.aodmod.service.alarm.proxy.TickAlarm
import com.retrox.aodmod.state.AodClockTick
import java.util.*

object LocalAlarmTimeOutTicker : TickAlarm {
    override fun startTick() {
        setUpAlarm()
    }

    override fun stopTick() {
        cancelAlarm()
    }

    private lateinit var alarmManager: AlarmManager
    private lateinit var timeTicker: AlarmTimeout
    fun initService(context: Context) {
        alarmManager = context.getSystemService(AlarmManager::class.java)
        val handler = Handler()
        this.timeTicker = AlarmTimeout(
            alarmManager,
            OnAlarmListener {
                scheduleTimeTick()
                MainHook.logD("Tick from AlarmTimeout")
                AodClockTick.tickLiveData.postValue("Tick from AlarmTimeout")
            }, "doze_time_tick_aod", handler
        )

    }

    private fun setUpAlarm() {
        scheduleTimeTick()
    }

    private fun scheduleTimeTick() {
        if (!timeTicker.isScheduled) {
            val currentTimeMillis = System.currentTimeMillis()
            val roundToNextMinute =
                roundToNextMinute(currentTimeMillis) - System.currentTimeMillis()
            timeTicker.schedule(roundToNextMinute, MODE_IGNORE_IF_SCHEDULED)
        }
    }

    private fun roundToNextMinute(j: Long): Long {
        val instance = Calendar.getInstance()
        instance.setTimeInMillis(j)
        instance.set(14, 0)
        instance.set(13, 0)
        instance.add(12, 1)
        return instance.getTimeInMillis()
    }

    private fun cancelAlarm() {
        if (timeTicker.isScheduled) {
            timeTicker.cancel()
        }
    }
}