package com.retrox.aodmod.app.pref

import android.annotation.SuppressLint
import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.app.App
import java.io.File


object AppPref {
    var aodMode by shared("AODMODE","ALWAYS_ON") // or ALWAYS_ON
    var musicShowOnAod by shared("MUSICSHOWONAOD", true)
    var filpOffScreen by shared("FILPOFFSCREEN", true)
    var aodShowSensitiveContent by shared("AODSHOWSENSITIVECONTENT", true)
    var fontWithSystem by shared("FONTWITHSYSTEM", false)
    var musicDisplayOffset by shared("MUSICDISPLAYOFFSET", false)
    var autoCloseAfterHour by shared("AUTOCLOSEAFTERHOUR", true)


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
}