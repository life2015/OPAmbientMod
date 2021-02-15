package com.retrox.aodmod.service.notification

import android.app.AndroidAppHelper
import android.app.Notification
import androidx.lifecycle.MutableLiveData
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.extensions.LiveEvent
import com.retrox.aodmod.extensions.getApplicationContext
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.state.AodMedia
import de.robv.android.xposed.XposedHelpers
import java.lang.Exception

object NotificationManager {

    const val EXTRA_PACKAGE = "aodmod_extra_package"
    const val EXTRA_IMPORTANTCE = "aodmod_extra_importance"

    const val POSTED = "Posted"
    const val REMOVED = "Removed"
    const val REFRESHED = "Refreshed"

    val notificationStatusLiveData = LiveEvent<Pair<StatusBarNotification, String>>() // 建议拿到后从Map二次查表确认

    val notificationMap = mutableMapOf<String, StatusBarNotification>()

    fun onNotificationPosted(sbn: StatusBarNotification, rankingMap: NotificationListenerService.RankingMap) {
        sbn.notification.extras.putString(EXTRA_PACKAGE, sbn.packageName)

        notificationMap.putNotification(sbn.key, sbn)
        val notification = sbn.notification
        notification.debugMessage()

        val ranking = getRanking(sbn, rankingMap)

        sbn.notification.extras.putInt(EXTRA_IMPORTANTCE, ranking.importance)
        if (ranking.importance > 1) {
            notificationStatusLiveData.postValue(sbn to "Posted")
        }

    }

    fun removeNotification(sbn: StatusBarNotification, rankingMap: NotificationListenerService.RankingMap) {
        notificationMap.remove(sbn.key)
        sbn.notification.debugMessage(type = "Removed")
        notificationStatusLiveData.postValue(sbn to "Removed")
    }

    fun addNotification(sbn: StatusBarNotification, rankingMap: NotificationListenerService.RankingMap) {
        val ranking = getRanking(sbn, rankingMap)
        sbn.notification.extras.putInt(EXTRA_IMPORTANTCE, ranking.importance)

        notificationMap.putNotification(sbn.key, sbn)
        sbn.notification.debugMessage(type = "Added")
    }

    fun resetState() {
        notificationStatusLiveData.postValue(null)
        notificationMap.clear()
    }

    private fun getRanking(
        sbn: StatusBarNotification,
        rankingMap: NotificationListenerService.RankingMap
    ): NotificationListenerService.Ranking {
        val ranking = NotificationListenerService.Ranking()
        rankingMap.getRanking(sbn.key, ranking)
        return ranking
    }

    fun notifyRefresh() {
        notificationStatusLiveData.postValue(null)

        // todo 按理说不应该放在这里 但是也没啥办法
        val musicActive = notificationMap.values.any {
            val hasMediaSession = XposedHelpers.callMethod(it.notification, "hasMediaSession") as Boolean
            hasMediaSession
        }
        val musicNotification = notificationMap.values.any {
            // 避免一部分人不开系统通知栏
            it.packageName == "com.tencent.qqmusic" || it.packageName == "com.netease.cloudmusic" || it.packageName == "code.name.monkey.retromusic" || it.packageName == "tv.danmaku.bili"
        }
        if (!musicActive && !musicNotification) {
            AodMedia.aodMediaLiveData.postValue(null) // Media InActive 媒体不活跃
        }
    }
}

fun Notification.debugMessage(type: String = "Posted") {
    val (appName, title, content, isOnGoing) = getNotificationData()
    val hasMediaSession = XposedHelpers.callMethod(this, "hasMediaSession") as Boolean
    val extras = extras
    val messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES)
    val histMessages = extras.getParcelableArray(Notification.EXTRA_HISTORIC_MESSAGES)

//    Only Use when Debug
//    MainHook.logD("通知调试: type: $type 应用->$appName 标题->$title 内容->$content OnGoing->$isOnGoing hasMeidaSession: $hasMediaSession visbility: $visibility  priority: $priority")
}

fun Notification.getNotificationData(): NotificationData {

    val builder = Notification.Builder.recoverBuilder(getApplicationContext(), this)
    val method = builder.javaClass.getMethod("loadHeaderAppName")
    val appName = method.invoke(builder) as String

    val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
    val content = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: "" // 不能直接取String Spannable的时候会CastException
    val isOnGoing = flags and Notification.FLAG_ONGOING_EVENT

    val packageName = extras.getString(NotificationManager.EXTRA_PACKAGE)
    val sensitive = packageName?.let inner@{
        if (!XPref.getAodShowSensitiveContent()) {
            val sensitiveApps = XPref.getSensitiveApps()
            return@inner (sensitiveApps.contains(it))
        } else return@inner false
    } ?: false

    val realContent = if (sensitive) "Sensitive notification" else content

    return NotificationData(appName, title, realContent, isOnGoing > 0)
}

fun MutableMap<String, StatusBarNotification>.putNotification(key: String, notification: StatusBarNotification){
    //Check notification with matching group is not already added and if it is, remove it
    try {
        if (notification.isGroup) {
            this.forEach {
                if (it.value.groupKey == notification.groupKey) this.remove(it.key)
            }
        }
    }catch (e: Exception){
        this[key] = notification
    }

    //Add the new notification
    this[key] = notification
}


data class NotificationData(val appName: String, val title: String, val content: String, val isOnGoing: Boolean)