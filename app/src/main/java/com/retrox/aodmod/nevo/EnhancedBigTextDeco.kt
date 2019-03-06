package com.retrox.aodmod.nevo

import android.app.Notification
import android.util.Log
import com.oasisfeng.nevo.sdk.MutableStatusBarNotification
import com.oasisfeng.nevo.sdk.NevoDecoratorService

class EnhancedBigTextDeco : NevoDecoratorService() {
    val TAG2 = "EnhancedBigTextDeco"

    override fun apply(evolving: MutableStatusBarNotification) {
        Log.d(TAG2, "apply key -> ${evolving.key}")
    }

    override fun onNotificationRemoved(key: String?, reason: Int) {
        super.onNotificationRemoved(key, reason)
        Log.d(TAG2, "removed -> ${key}")


    }
}