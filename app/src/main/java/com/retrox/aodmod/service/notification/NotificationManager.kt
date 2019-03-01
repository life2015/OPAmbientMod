package com.retrox.aodmod.service.notification

import android.app.AndroidAppHelper
import android.app.Notification
import android.arch.lifecycle.MutableLiveData
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.retrox.aodmod.MainHook
import de.robv.android.xposed.XposedHelpers

object NotificationManager {

    const val POSTED = "Posted"
    const val REMOVED = "Removed"
    const val REFRESHED = "Refreshed"

    val notificationStatusLiveData = MutableLiveData<Pair<StatusBarNotification, String>>() // 建议拿到后从Map二次查表确认

    val notificationMap = mutableMapOf<String, StatusBarNotification>()

    fun onNotificationPosted(sbn: StatusBarNotification, rankingMap: NotificationListenerService.RankingMap) {
        notificationMap[sbn.key] = sbn
        val notification = sbn.notification
        notification.debugMessage()
        if (notification.channelId != "后台服务图标") notificationStatusLiveData.postValue(sbn to "Posted")
    }

    fun removeNotification(sbn: StatusBarNotification, rankingMap: NotificationListenerService.RankingMap) {
        notificationMap.remove(sbn.key)
        sbn.notification.debugMessage(type = "Removed")
        notificationStatusLiveData.postValue(sbn to "Removed")
    }

    fun addNotification(sbn: StatusBarNotification, rankingMap: NotificationListenerService.RankingMap) {
        notificationMap[sbn.key] = sbn
        sbn.notification.debugMessage(type = "Added")
    }

    fun resetState() {
        notificationStatusLiveData.postValue(null)
        notificationMap.clear()
    }

    fun notifyRefresh() {
        notificationStatusLiveData.postValue(null)
    }
}

fun Notification.debugMessage(type: String = "Posted") {
    val (appName, title, content, isOnGoing) = getNotificationData()
    MainHook.logD("通知调试: type: $type 应用->$appName 标题->$title 内容->$content OnGoing->$isOnGoing ")
}

fun Notification.getNotificationData(): NotificationData {
    val builder = Notification.Builder.recoverBuilder(AndroidAppHelper.currentApplication().applicationContext, this)
    val appName = XposedHelpers.callMethod(builder, "loadHeaderAppName") as String

    val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
    val content = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: "" // 不能直接取String Spannable的时候会CastException
    val isOnGoing = flags and Notification.FLAG_ONGOING_EVENT

    return NotificationData(appName, title, content, isOnGoing > 0)
}

data class NotificationData(val appName: String, val title: String, val content: String, val isOnGoing: Boolean)