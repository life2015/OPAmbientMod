package com.retrox.aodmod.proxy.view

import android.app.Notification
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import org.jetbrains.anko.*

fun Context.aodNotification(lifecycleOwner: LifecycleOwner): View {
    return verticalLayout {
        val notificationImage = imageView {
            contentDescription = "通知图标"
        }.lparams(width = dip(24), height = dip(24)) {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val appName = textView {
            textColor = Color.WHITE
            textSize = 20f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
        }.lparams(width = matchParent, height = wrapContent) {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = dip(16)
            horizontalMargin = dip(16)
        }

        val title = textView {
            textColor = Color.WHITE
            textSize = 18f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
        }.lparams(width = matchParent, height = wrapContent) {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = dip(46)
            horizontalMargin = dip(16)

        }

        val content = textView {
            textColor = Color.WHITE
            textSize = 16f
            setGoogleSans()
            gravity = Gravity.CENTER_HORIZONTAL
//            setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            setAutoSizeTextTypeUniformWithConfiguration(12,18,1,TypedValue.COMPLEX_UNIT_SP)

        }.lparams(width = matchParent, height = dip(200)) {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = dip(26)
            horizontalMargin = dip(16)
        }


        NotificationManager.notificationStatusLiveData.observe(lifecycleOwner, Observer {
            it?.let { (sbn, status) ->
                if (status == "Removed") return@let
//                if (sbn.notification.flags and Notification.FLAG_ONGOING_EVENT != 0) return@Observer // 去掉OnGoing Notification

                if (it.first.notification.getNotificationData().isOnGoing) return@let

                val notification = NotificationManager.notificationMap[sbn.key]?.notification ?: return@let
                notification.getNotificationData().let {
                    appName.text = it.appName
                    title.text = it.title
                    content.text = it.content
                }
                val icon = notification.smallIcon.loadDrawable(context)
                notificationImage.setImageDrawable(icon)
            }
        })
    }
}

fun<T> LiveData<T>.observeNew(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    val prevValue = value
    observe(lifecycleOwner, Observer {
        if (it != prevValue) {
            observer.onChanged(it)
        }
    })
}