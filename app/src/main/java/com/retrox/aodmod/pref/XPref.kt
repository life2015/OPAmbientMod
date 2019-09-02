package com.retrox.aodmod.pref

import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.shared.FileUtils
import de.robv.android.xposed.XSharedPreferences
import java.io.File
import java.lang.ref.WeakReference

object XPref {

    private var xSharedPreferences: WeakReference<XSharedPreferences?> = WeakReference(null)

    private fun getPref(): XSharedPreferences {
        var preferences = xSharedPreferences.get()
        if (preferences == null) {
            preferences = XSharedPreferences(BuildConfig.APPLICATION_ID)
            val result = preferences.makeWorldReadable()
            MainHook.logD("SELinux Pref Status: $result")

//            if (!result) {
//                // 从外置读取XSP
//                preferences = XSharedPreferences(File(FileUtils.sharedDir, AppPref.externalPrefName))
//            }

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
    fun getAlarmTimeCorrection() = XPref.getPref().getBoolean("ALARMTIMECORRECTION", true)
    fun getAodShowWeather() = XPref.getPref().getBoolean("AODSHOWWEATHER", true)
    fun getAodShowNote() = XPref.getPref().getBoolean("AODSHOWNOTE", false)
    fun getAodNoteContent() = XPref.getPref().getString("AODNOTECONTENT", "") ?: ""
    fun getAodLayoutTheme() = XPref.getPref().getString("AODLAYOUTTHEME", "Flat") ?: "Flat"
    fun getAodAlarmMode() = XPref.getPref().getString("AODALARMMODE", "Chore") ?: "Chore"
    fun getAodPickCheckEnabled() = XPref.getPref().getBoolean("AODPICKCHECK", false)
    fun getAodAutoCloseBySeconds() = XPref.getPref().getBoolean("AUTOCLOSEBYSECONDS", false)


}