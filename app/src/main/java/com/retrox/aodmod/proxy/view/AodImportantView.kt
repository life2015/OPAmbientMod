package com.retrox.aodmod.proxy.view

import android.app.Notification
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.extensions.getNewAodNoteContent
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import de.robv.android.xposed.XposedHelpers
import org.jetbrains.anko.*

fun Context.importantMessageView(lifecycleOwner: LifecycleOwner): View {
    return verticalLayout {
        gravity = Gravity.CENTER_HORIZONTAL

        val note = textView {
            textColor = Color.WHITE
            textSize = 16f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
            visibility = View.GONE
            if (XPref.getAodShowNote() && !getNewAodNoteContent().isBlank()) {
                visibility = View.VISIBLE
                text = getNewAodNoteContent()
            }
        }.lparams(matchParent, wrapContent) {
            horizontalMargin = dip(16)
            bottomMargin = dip(12)
        }

        val title = textView {
            textColor = Color.WHITE
            textSize = 16f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
            text = ""
        }.lparams(matchParent, wrapContent) {
            horizontalMargin = dip(16)
        }

        val content = textView {
            textColor = Color.WHITE
            textSize = 16f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
            text = ""
            setAutoSizeTextTypeUniformWithConfiguration(12, 16, 1, TypedValue.COMPLEX_UNIT_SP)

        }.lparams(matchParent, dip(200)) {
            topMargin = dip(8)
            horizontalMargin = dip(16)
        }

        NotificationManager.notificationStatusLiveData.observe(lifecycleOwner, Observer {
            if (it == null) { // 就是 REFRESHED 状态 因为Sbn无法为空 就这样子吧先
                title.text = ""
                content.text = ""
            }
            it?.let { (sbn, status) ->
                if (status == NotificationManager.POSTED) {
                    val realNotification = NotificationManager.notificationMap[sbn.key]?.notification
                    if (realNotification == null) {
                        title.text = ""
                        content.text = ""
                        return@let // return之前还原View的状态
                    }

//                    MainHook.logD("package Name: ${sbn.packageName} isSensitive: $sensitive")

                    val (appName, messageTitle, messageContent, isOnGoing) = realNotification.getNotificationData()
                    if (isOnGoing) return@Observer
                    title.text = "$appName · $messageTitle"
                    content.text = messageContent
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