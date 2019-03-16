package com.retrox.aodmod.pref

import com.retrox.aodmod.BuildConfig
import de.robv.android.xposed.XSharedPreferences
import java.lang.ref.WeakReference

object XPref {

    private var xSharedPreferences: WeakReference<XSharedPreferences?> = WeakReference(null)

    private fun getPref(): XSharedPreferences {
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

    fun getDisplayMode() = XPref.getPref().getString("AODMODE", "ALWAYS_ON") ?: ""
    fun getMusicAodEnabled() = XPref.getPref().getBoolean("MUSICSHOWONAOD",true)
    fun getFilpOffMode() = XPref.getPref().getBoolean("FILPOFFSCREEN", true)
    fun getAodShowSensitiveContent() = XPref.getPref().getBoolean("AODSHOWSENSITIVECONTENT", true)
    fun getFontWithSystem() = XPref.getPref().getBoolean("FONTWITHSYSTEM", false)
    fun getMusicOffsetEnabled() = XPref.getPref().getBoolean("MUSICDISPLAYOFFSET", false)
    fun getAutoScreenOffAfterHourEnabled() = XPref.getPref().getBoolean("AUTOCLOSEAFTERHOUR", true)
    fun getAutoBrightnessEnabled() = XPref.getPref().getBoolean("AUTOBRIGHTNESS", true)

}