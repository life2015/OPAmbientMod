package com.retrox.aodmod.proxy.view

import android.app.Notification
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.Gravity
import android.view.View
import com.retrox.aodmod.extensions.setGoogleSans
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
        }

        val content = textView {
            textColor = Color.WHITE
            textSize = 16f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
        }.lparams {
            topMargin = dip(8)
        }

        NotificationManager.notificationStatusLiveData.observe(lifecycleOwner, Observer {
            it?.let { (sbn, status) ->
                // todo: 考虑检测全部的Notification消息获取关键字
                if (status == NotificationManager.POSTED) {
                    val (appName, messageTitle, messageContent, onGoing) = sbn.notification.getNotificationData()
                    if (onGoing) return@Observer
                    title.text = "$appName · $messageTitle"
                    content.text = messageContent
                } else if (status == NotificationManager.REMOVED) {
                    title.text = ""
                    content.text = ""
                }
            }
        })
    }
}