package com.retrox.aodmod.app.pref

import android.annotation.SuppressLint
import android.util.Log
import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.app.App
import com.retrox.aodmod.shared.FileUtils
import com.retrox.aodmod.shared.SharedLogger
import java.io.File


object AppPref {
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
            prefsFile.copyTo(externalPrefFile, true)
        }
    }
}