package com.retrox.aodmod.pref

import android.app.AndroidAppHelper
import android.provider.Settings

object SystemPref {
    fun getNightModeStat(): Boolean = Settings.Secure.getInt(AndroidAppHelper.currentApplication().contentResolver, "night_display_activated", 0) == 1

    // 方法名X结尾，意味着建议在Xposed环境调用
    fun getNightAutoOffX(): Boolean = getNightModeStat() && XPref.getAodNightModeAutoOff()
}