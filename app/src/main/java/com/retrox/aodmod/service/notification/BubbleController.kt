package com.retrox.aodmod.service.notification

import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.retrox.aodmod.app.util.isBubble
import de.robv.android.xposed.XposedHelpers

object BubbleController {

    var bubbleController: Any? = null
    var notificationEntryClass: Class<*>? = null

    /**
     *  For use when hiding the notification icon if the notification has been suppressed by the system.
     *  This usually happens if a bubble has been opened and closed, but the message not cleared by the app.
     *  Fixes 'ghost' notification icons such as Facebook Messenger
     */
    fun isBubbleNotificationSuppressedFromShade(statusBarNotification: StatusBarNotification): Boolean {
        //Only supported on R+
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return false
        try {
            val ranking = NotificationListenerService.Ranking().apply {
                XposedHelpers.setObjectField(this, "mKey", statusBarNotification.key)
            }
            val fakeNotificationEntry = notificationEntryClass?.getConstructor(
                StatusBarNotification::class.java,
                NotificationListenerService.Ranking::class.java,
                Long::class.java
            )
                ?.newInstance(statusBarNotification, ranking, 0) ?: return false
            return XposedHelpers.callMethod(
                bubbleController,
                "isBubbleNotificationSuppressedFromShade",
                arrayOf(notificationEntryClass),
                fakeNotificationEntry
            ) as Boolean
        }catch (e: Exception){
            //Issue with bubbles, always show icon
            return false
        }
    }

}