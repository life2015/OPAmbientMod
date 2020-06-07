package com.retrox.aodmod.proxy.view.custom.pixel

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import org.jetbrains.anko.*

fun Context.importantMessageView(lifecycleOwner: LifecycleOwner): View {
    return verticalLayout {
        gravity = Gravity.CENTER_HORIZONTAL

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
            maxLines = 3
            ellipsize = TextUtils.TruncateAt.END
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

                    val (appName, messageTitle, messageContent, onGoing) = realNotification.getNotificationData()
                    if (onGoing) return@Observer
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