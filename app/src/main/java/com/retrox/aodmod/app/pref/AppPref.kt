package com.retrox.aodmod.app.pref

import android.annotation.SuppressLint
import android.util.Log
import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.app.App
import com.retrox.aodmod.shared.FileUtils
import com.retrox.aodmod.shared.SharedLogger
import java.io.File


object AppPref {
    var moduleState by shared("MODULE_ENABLED", true)

    var aodMode by shared("AODMODE", "ALWAYS_ON") // or ALWAYS_ON
    var musicShowOnAod by shared("MUSICSHOWONAOD", true)
    var filpOffScreen by shared("FILPOFFSCREEN", true)
    var aodShowSensitiveContent by shared("AODSHOWSENSITIVECONTENT", true)
    var fontWithSystem by shared("FONTWITHSYSTEM", false)
    var musicDisplayOffset by shared("MUSICDISPLAYOFFSET", false)
    var autoCloseAfterHour by shared("AUTOCLOSEAFTERHOUR", true)
    var autoBrightness by shared("AUTOBRIGHTNESS", true)
    var alarmTimeCorrection by shared("ALARMTIMECORRECTION", true)
    var aodShowWeather by shared("AODSHOWWEATHER", true)
    var aodShowNote by shared("AODSHOWNOTE", false)
    var aodNoteContent by shared("AODNOTECONTENT", "")
    var aodLayoutTheme by shared("AODLAYOUTTHEME", "Default")
    var aodAlarmMode by shared("AODALARMMODE", "Alarm-TimeOutMode")
    var aodPickCheck by shared("AODPICKCHECK", false)
    var autoCloseBySeconds by shared("AUTOCLOSEBYSECONDS", false)
    var autoCloseByNightMode by shared("AUTOCLOSEBYNIGHTMODE", false)
    var forceEnglishWordClock by shared("FORCEENGLISHWORDCLOCK", false)
    var forceShowWordClockOnFlat by shared("FORCESHOWWORDCLOCKONFLAT", true)
    var use24h by shared("USE_24_HOUR", true)
    var dateFormat by shared("DATE_FORMAT", "EEE, d MMM")
    var useAmPm by shared("USE_AM_PM_MARKER", false)
    var weatherShowSymbol by shared("WEATHER_SHOW_SYMBOL", true)
    var weatherShowCondition by shared("WEATHER_SHOW_CONDITION", true)
    var weatherShowTemperature by shared("WEATHER_SHOW_TEMPERATURE", true)
    var weatherShowCity by shared("KEY_WEATHER_SHOW_CITY", false)
    var showAlarm by shared("KEY_SHOW_ALARM", true)
    var showAlarmEmoji by shared("KEY_SHOW_ALARM_EMOJI", true)
    var showBullets by shared("KEY_SHOW_BULLETS", true)
    var useSystemMusic by shared("USE_SYSTEM_MUSIC", false)
    var usePixelMusicIcon by shared("USE_PIXEL_MUSIC_ICON", false)
    var batteryEnableForceDoze by shared("ENABLE_FORCE_DOZE", false)
    var batteryEnableBatterySaver by shared("ENABLE_BATTERY_SAVER", false)
    var batteryLowerRefreshRate by shared("ENABLE_LOWER_REFRESH_RATE", false)
    var batteryLowestBrightness by shared("ENABLE_LOWEST_BRIGHTNESS", false)
    var weatherIconStyle by shared("WEATHER_ICON_STYLE", "google")
    var onePlusClockStyle by shared("ONEPLUS_CLOCK_STYLE", 0)
    var lyricsEnabled by shared("LYRICS_ENABLED", false)
    var pixelSmallMusic by shared("PIXEL_SMALL_MUSIC", false)
    var hideDivider by shared("HIDE_DIVIDER", false)
    var sensitiveApps by shared("SENSITIVE_APPS", "")
    var ambientMusic by shared("AMBIENT_MUSIC", false)

    //Static translations stored for when there isn't yet a context
    var translationConstantLightMode by shared("xposed_constant_light_mode_7pro", "System Enhancement - 7 Pro")
    var translationConstantLightModeNS by shared("xposed_constant_light_mode_7pro_ns", "System Enhancement - 7 Pro is not supported")

    val externalPrefName = "${BuildConfig.APPLICATION_ID}_external_pref.xml"

    @SuppressLint("SetWorldReadable")
    fun setWorldReadable() {
        val dataDir = File(App.application.getApplicationInfo().dataDir)
        val prefsDir = File(dataDir, "shared_prefs")
        val prefsFile = File(prefsDir, BuildConfig.APPLICATION_ID + "_preferences.xml")
        if (prefsFile.exists()) {
            for (file in arrayOf<File>(dataDir, prefsDir, prefsFile)) {
                file.setReadable(true, false)
                file.setExecutable(true, false)
            }
        }
    }

    fun flushPrefChangeToSDcard() {
        val dataDir = File(App.application.applicationInfo.dataDir)
        val prefsDir = File(dataDir, "shared_prefs")
        val prefsFile = File(prefsDir, BuildConfig.APPLICATION_ID + "_preferences.xml")
        if (prefsFile.exists()) {
            Log.d("AODMOD AppPref", "Pref Changed, Flush to sdcard")
            SharedLogger.writeLog("AODMOD AppPref:" + "Pref Changed, Flush to sdcard")
            FileUtils.createSharedFileDir()
            val externalPrefFile = File(FileUtils.sharedDir, externalPrefName)
            externalPrefFile.createNewFile()
            try {
                prefsFile.copyTo(externalPrefFile, true)
            }catch (e: FileAlreadyExistsException){
                //Happens sometimes, still applies
            }
        }
    }
}