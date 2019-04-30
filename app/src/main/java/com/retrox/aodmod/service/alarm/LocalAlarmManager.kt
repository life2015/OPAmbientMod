package com.retrox.aodmod.service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.receiver.ClockTickReceiver
import com.retrox.aodmod.service.alarm.proxy.TickAlarm
import java.lang.Exception

/**
 * 一分钟一次的唤醒
 */
object LocalAlarmManager : TickAlarm {
    override fun startTick() {
        setUpAlarm()
    }

    override fun stopTick() {
        cancelAlarm()
    }

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    fun initService(context: Context) {
        alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent().apply {
            action = ClockTickReceiver.CUSTOM_PING
        }
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun setUpAlarm() {

        cancelAlarm()

        try {
            MainHook.logD("Local AlarmManager setup")
            val type = if (XPref.getAlarmTimeCorrection()) AlarmManager.RTC_WAKEUP else AlarmManager.RTC
            alarmManager.setRepeating(type, System.currentTimeMillis(), 60 * 1000L, pendingIntent)
        } catch (e: Exception) {
            MainHook.logE(msg = "setUpAlarm Failed", t = e)
        }
    }

    private fun cancelAlarm() {
        try {
            MainHook.logD("Local AlarmManager cancel")
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            MainHook.logE(msg = "CancelAlarm Failed", t = e)
        }
    }
}