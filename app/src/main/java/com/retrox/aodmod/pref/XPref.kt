package com.retrox.aodmod.pref

import com.retrox.aodmod.BuildConfig
import de.robv.android.xposed.XSharedPreferences
import java.lang.ref.WeakReference

object XPref {

    private var xSharedPreferences: WeakReference<XSharedPreferences?> = WeakReference(null)

    fun getPref(): XSharedPreferences {
        var preferences = xSharedPreferences.get()
        if (preferences == null) {
            preferences = XSharedPreferences(BuildConfig.APPLICATION_ID)
            preferences.makeWorldReadable()
            preferences.reload()
            xSharedPreferences = WeakReference(preferences)
        } else {
            preferences.reload()
        }
        return preferences
    }

    fun getDisplayMode() = XPref.getPref().getString("AODMODE", "SYSTEM") ?: ""
}