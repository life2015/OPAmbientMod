package com.retrox.aodmod.pref

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.app.XposedUtils
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.util.getClassOrNull
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.shared.FileUtils
import com.retrox.aodmod.util.getSharedBoolPref
import com.retrox.aodmod.util.getSharedIntPref
import com.retrox.aodmod.util.getSharedStringPref
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.lang.ref.WeakReference

object XPref {

    private var xSharedPreferences: WeakReference<XSharedPreferences?> = WeakReference(null)

    internal var context: WeakReference<Context?> = WeakReference(null)

    private fun getPref(): SharedPrefsImpl {
        if(getClassOrNull("de.robv.android.xposed.XSharedPreferences") != null) {
            if(context.get() != null){
                //Android R+ - scoped storage prevents use of the /sdcard/Android/aod files (this is a better impl than XSharedPref anyway)
                return SharedPrefsImplRemote(context.get()!!)
            }else {
                var preferences = xSharedPreferences.get()
                if (preferences == null) {
                    preferences = XSharedPreferences(BuildConfig.APPLICATION_ID)
                    val result = preferences.makeWorldReadable()
                    logD("SELinux Pref Status: $result")

                    if (preferences.file == null) {
                        logD("XPref get null, use external pref as XPref")
                        // 从外置读取XSP
                        val file = File(FileUtils.sharedDir, AppPref.externalPrefName)
                        if (file.exists()) {
                            preferences = XSharedPreferences(file)
                        }
                    }

                    preferences.reload()
                    xSharedPreferences = WeakReference(preferences)
                } else {
                    preferences.reload()
                }
                return SharedPrefsImplNative(preferences)
            }
        }else{
            return SharedPrefsImplNative(context.get()?.getSharedPreferences("${BuildConfig.APPLICATION_ID}_preferences", Context.MODE_PRIVATE)!!)
        }
    }

    fun isSettings(): Boolean {
        return getClassOrNull("de.robv.android.xposed.XSharedPreferences") == null
    }

    fun isAndroidQ() = Build.VERSION.SDK_INT > Build.VERSION_CODES.P

    fun getModuleState() = getPref().getBoolean("MODULE_ENABLED", true)

    fun getDisplayMode() = getPref().getString("AODMODE", "ALWAYS_ON") ?: ""
    fun getMusicAodEnabled() = getPref().getBoolean("MUSICSHOWONAOD",true)
    fun getFilpOffMode() = getPref().getBoolean("FILPOFFSCREEN", true)
    fun getAodShowSensitiveContent() = getPref().getBoolean("AODSHOWSENSITIVECONTENT", true)
    fun getFontWithSystem() = getPref().getBoolean("FONTWITHSYSTEM", false)
    fun getMusicOffsetEnabled() = getPref().getBoolean("MUSICDISPLAYOFFSET", false)
    fun getAutoScreenOffAfterHourEnabled() = getPref().getBoolean("AUTOCLOSEAFTERHOUR", true)
    fun getAutoBrightnessEnabled() = getPref().getBoolean("AUTOBRIGHTNESS", true)
    fun getAlarmTimeCorrection() = getPref().getBoolean("ALARMTIMECORRECTION", true)
    fun getAodShowWeather() = getPref().getBoolean("AODSHOWWEATHER", true)
    fun getAodShowNote() = getPref().getBoolean("AODSHOWNOTE", false)
    fun getAodNoteContent() = getPref().getString("AODNOTECONTENT", "") ?: ""
    fun getAodLayoutTheme() = getPref().getString("AODLAYOUTTHEME", "Flat") ?: "Flat"
    fun getAodAlarmMode() = getPref().getString("AODALARMMODE", "Alarm-TimeOutMode") ?: "Alarm-TimeOutMode"
    fun getAodPickCheckEnabled() = getPref().getBoolean("AODPICKCHECK", false)
    fun getAodAutoCloseBySeconds() = getPref().getBoolean("AUTOCLOSEBYSECONDS", false)
    fun getAodNightModeAutoOff() = getPref().getBoolean("AUTOCLOSEBYNIGHTMODE", false)
    fun getForceEnglishWordClock() = getPref().getBoolean("FORCEENGLISHWORDCLOCK", false)
    fun getForceWordClockOnFlat() = getPref().getBoolean("FORCESHOWWORDCLOCKONFLAT", true)
    fun getDateFormat() = getPref().getString("DATE_FORMAT", "EEE, d MMM")
    fun getIs24h() = getPref().getBoolean("USE_24_HOUR", true)
    fun getIsAmPm() = getPref().getBoolean("USE_AM_PM_MARKER", false)
    fun getWeatherShowSymbol() = getPref().getBoolean("WEATHER_SHOW_SYMBOL", true)
    fun getWeatherShowCondition() = getPref().getBoolean("WEATHER_SHOW_CONDITION", true)
    fun getWeatherShowTemperature() = getPref().getBoolean("WEATHER_SHOW_TEMPERATURE", true)
    fun getWeatherShowCity() = getPref().getBoolean("KEY_WEATHER_SHOW_CITY", true)
    fun getShowAlarm() = getPref().getBoolean("KEY_SHOW_ALARM", true)
    fun getShowAlarmEmoji() = getPref().getBoolean("KEY_SHOW_ALARM_EMOJI", true)
    fun getShowBullets() = getPref().getBoolean("KEY_SHOW_BULLETS", true)
    fun getUseSystemMusic() = getPref().getBoolean("USE_SYSTEM_MUSIC", false)
    fun getUsePixelMusicIcon() = getPref().getBoolean("USE_PIXEL_MUSIC_ICON", false)
    fun getBatteryEnableForceDoze() = getPref().getBoolean("ENABLE_FORCE_DOZE", false)
    fun getBatteryEnableBatterySaver() = getPref().getBoolean("ENABLE_BATTERY_SAVER", false)
    fun getBatteryLowerRefreshRate() = getPref().getBoolean("ENABLE_LOWER_REFRESH_RATE", false)
    fun getBatteryLowestBrightness() = getPref().getBoolean("ENABLE_LOWEST_BRIGHTNESS", false)
    fun getWeatherIconStyle() = getPref().getString("WEATHER_ICON_STYLE", "google")
    fun getOnePlusClockStyle() = getPref().getInt("ONEPLUS_CLOCK_STYLE", 0)
    fun getLyricsEnabled() = getPref().getBoolean("LYRICS_ENABLED", false)
    fun getPixelSmallMusic() = getPref().getBoolean("PIXEL_SMALL_MUSIC", false)
    fun getHideDivider() = getPref().getBoolean("HIDE_DIVIDER", false)

    //Static translations stored for when there isn't yet a context
    fun getTranslationConstantLightMode() = getPref().getString("xposed_constant_light_mode_7pro", "System Enhancement - 7 Pro")
    fun getTranslationConstantLightModeNS() = getPref().getString("xposed_constant_light_mode_7pro_ns", "System Enhancement - 7 Pro is not supported")

    class SharedPrefsImplNative(private val sharedPreferences: SharedPreferences) : SharedPrefsImpl {
        override fun getBoolean(key: String, defValue: Boolean): Boolean {
            return sharedPreferences.getBoolean(key, defValue)
        }

        override fun getString(key: String, defValue: String): String {
            return sharedPreferences.getString(key, defValue)
        }

        override fun getInt(key: String, defValue: Int): Int {
            return sharedPreferences.getInt(key, defValue)
        }
    }

    class SharedPrefsImplRemote(private val context: Context): SharedPrefsImpl {
        override fun getBoolean(key: String, defValue: Boolean): Boolean {
            return getSharedBoolPref(context, key, defValue)
        }

        override fun getString(key: String, defValue: String): String {
            return getSharedStringPref(context, key, defValue) ?: defValue
        }

        override fun getInt(key: String, defValue: Int): Int {
            return getSharedIntPref(context, key, defValue)
        }
    }

    interface SharedPrefsImpl {
        fun getBoolean(key: String, defValue: Boolean): Boolean
        fun getString(key: String, defValue: String): String
        fun getInt(key: String, defValue: Int): Int
    }

}