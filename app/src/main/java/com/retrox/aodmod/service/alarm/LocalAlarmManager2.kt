package com.retrox.aodmod.service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.receiver.ClockTickReceiver
import java.lang.Exception

/**
 * 缓和策略的AlarmManager
 */
object LocalAlarmManager2 {

    private var alarmManager: AlarmManager? = null
    private var pendingIntentRtc: PendingIntent? = null
    private var pendingIntentRtcWakeUp: PendingIntent? = null

    fun initService(context: Context) {
        if (alarmManager != null) {
            pendingIntentRtc?.let { alarmManager?.cancel(it) }
            pendingIntentRtcWakeUp?.let { alarmManager?.cancel(it) } // 去除残留的AlarmManager
        }
        alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent().apply {
            action = ClockTickReceiver.CUSTOM_PING_RTC
        }
        pendingIntentRtc = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val intentRtcWakeUp = Intent().apply {
            action = ClockTickReceiver.CUSTOM_PING_RTC_WAKEUP
        }
        pendingIntentRtcWakeUp =
            PendingIntent.getBroadcast(context, 0, intentRtcWakeUp, PendingIntent.FLAG_UPDATE_CURRENT)

    }

    fun setUpAlarm() {

        cancelAlarm()

        // todo 分阶设置RTC 和 WakeUP 3min/5min
        try {
            MainHook.logD("Local AlarmManager setup")
            val timeWakeUp = if (XPref.getAlarmTimeCorrection()) 4 * 60 * 1000L else 10 * 60 * 1000L
            alarmManager?.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60 * 1000L, pendingIntentRtc)
            alarmManager?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                timeWakeUp,
                pendingIntentRtc
            )
        } catch (e: Exception) {
            MainHook.logE(msg = "setUpAlarm Failed", t = e)
        }
    }

    fun cancelAlarm() {
        try {
            MainHook.logD("Local AlarmManager cancel")
            if (alarmManager != null) {
                pendingIntentRtc?.let { alarmManager?.cancel(it) }
                pendingIntentRtcWakeUp?.let { alarmManager?.cancel(it) } // 去除残留的AlarmManager
            }
        } catch (e: Exception) {
            MainHook.logE(msg = "CancelAlarm Failed", t = e)
        }
    }
}