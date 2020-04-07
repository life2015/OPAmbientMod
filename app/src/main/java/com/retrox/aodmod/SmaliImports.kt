package com.retrox.aodmod

import com.retrox.aodmod.pref.XPref.getDateFormat
import com.retrox.aodmod.pref.XPref.getIs24h
import com.retrox.aodmod.pref.XPref.getIsAmPm
import com.retrox.aodmod.pref.XPref.getShowBullets
import java.text.SimpleDateFormat
import java.util.*

object SmaliImports {

    val systemDateFormat: String
        get() = getDateFormat()

    val timeFormat: String
        get() = if (getIs24h()) {
            "HH:mm"
        } else {
            if (getIsAmPm()) {
                "h:mm a"
            } else {
                "h:mm"
            }
        }

    fun getFormattedTime(timeInMillis: Long): String {
        val time = Date(timeInMillis)
        return if (getIs24h()) {
            val format =
                SimpleDateFormat("HH:mm", Locale.getDefault())
            format.format(time)
        } else {
            if (getIsAmPm()) {
                val format =
                    SimpleDateFormat("h:mm a", Locale.getDefault())
                format.format(time)
            } else {
                val format =
                    SimpleDateFormat("h:mm", Locale.getDefault())
                format.format(time)
            }
        }
    }

    val bulletSymbol: String
        get() = if (getShowBullets()) {
            " • "
        } else {
            ""
        }

    fun getEmojiForCode(code: Int?): String {
        /*
         * Weather codes (from Lawnchair)
        SUNNY(1001, WeatherIconManager.Icon.CLEAR),
        SUNNY_INTERVALS(1002, WeatherIconManager.Icon.MOSTLY_CLEAR),
        CLOUDY(1003, WeatherIconManager.Icon.CLOUDY),
        OVERCAST(1004, WeatherIconManager.Icon.OVERCAST),
        DRIZZLE(1005, WeatherIconManager.Icon.PARTLY_CLOUDY_W_SHOWERS),
        RAIN(1006, WeatherIconManager.Icon.RAIN),
        SHOWER(1007, WeatherIconManager.Icon.SHOWERS),
        DOWNPOUR(1008, WeatherIconManager.Icon.RAIN),
        RAINSTORM(1009, WeatherIconManager.Icon.RAIN),
        SLEET(1010, WeatherIconManager.Icon.SLEET),
        FLURRY(1011, WeatherIconManager.Icon.FLURRIES),
        SNOW(1012, WeatherIconManager.Icon.SNOW),
        SNOWSTORM(1013, WeatherIconManager.Icon.SNOWSTORM),
        HAIL(1014, WeatherIconManager.Icon.HAIL),
        THUNDERSHOWER(1015, WeatherIconManager.Icon.THUNDERSTORMS),
        SANDSTORM(1016, WeatherIconManager.Icon.SANDSTORM),
        FOG(1017, WeatherIconManager.Icon.FOG),
        HURRICANE(1018, WeatherIconManager.Icon.HURRICANE),
        HAZE(1019, WeatherIconManager.Icon.HAZY),
        NONE(9999, WeatherIconManager.Icon.NA);
         */
        return when (code) {
            1001 -> "☀️"
            1002 -> "⛅️"
            1003 -> "☁️"
            1004 -> "🌥"
            1005 -> "🌦"
            1006 -> "🌧"
            1007 -> "🌦"
            1008 -> "🌧"
            1009 -> "🌧"
            1010 -> "🌧"
            1011 -> "🌨"
            1012 -> "🌨"
            1013 -> "🌨"
            1014 -> "🌨"
            1015 -> "⛈"
            1016 -> "💨"
            1017 -> "🌫"
            1018 -> "💨"
            1019 -> "🌫"
            else -> "❓"
        }
    }
}