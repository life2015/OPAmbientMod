package com.retrox.aodmod.proxy.view

import android.app.Notification
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.Gravity
import android.view.View
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import de.robv.android.xposed.XposedHelpers
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

fun Context.importantMessageView(lifecycleOwner: LifecycleOwner): View {
    return verticalLayout {
        gravity = Gravity.CENTER_HORIZONTAL

        val title = textView {
            textColor = Color.WHITE
            textSize = 16f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
            text = ""
        }

        val content = textView {
            textColor = Color.WHITE
            textSize = 16f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
            text = ""
        }.lparams {
            topMargin = dip(8)
        }

        NotificationManager.notificationStatusLiveData.observe(lifecycleOwner, Observer {
            it?.let { (sbn, status) ->
                if (status == NotificationManager.POSTED) {
                    val realNotification = NotificationManager.notificationMap[sbn.key]?.notification
                    if (realNotification == null) {
                        title.text = ""
                        content.text = ""
                        return@let // return之前还原View的状态
                    }
                    val sensitive = sbn.packageName?.let inner@{
                        if (!XPref.getAodShowSensitiveContent()) {
                            val sensitiveApps = listOf(
                                "com.android.phone",
                                "com.tencent.mm",
                                "com.tencent.tim",
                                "com.tencent.mobileqq",
                                "com.android.mms"
                            )
                            return@inner (sensitiveApps.contains(it))
                        } else return@inner false
                    } ?: false

                    MainHook.logD("package Name: ${sbn.packageName} isSensitive: $sensitive")

                    val (appName, messageTitle, messageContent, onGoing) = realNotification.getNotificationData()
                    if (onGoing) return@Observer
                    title.text = "$appName · $messageTitle"
                    content.text = if (sensitive) " " else messageContent
                } else if (status == NotificationManager.REMOVED) {
                    title.text = ""
                    content.text = ""
                } else if (status == NotificationManager.REFRESHED) {
                    title.text = ""
                    content.text = ""
                }
            }
        })
    }
}