package com.retrox.aodmod.weather.icons

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.MetricAffectingSpan
import androidx.core.graphics.drawable.toBitmap
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.toDp
import com.retrox.aodmod.extensions.toPx

class EmojiWeatherIconProvider : BaseWeatherIconProvider() {

    override fun getWeatherIcon(context: Context, conditionCode: Int): MetricAffectingSpan {
        return BoringSpan()
    }

    companion object {
        private val weatherMap = mapOf(
            //Sunny
            Pair(1001, R.drawable.ic_emoji_sunny),
            //Sunny Intervals
            Pair(1002, R.drawable.ic_emoji_partly_cloudy),
            //Cloudy
            Pair(1003, R.drawable.ic_emoji_cloudy),
            //Overcast
            Pair(1004, R.drawable.ic_emoji_overcast),
            //Drizzle
            Pair(1005, R.drawable.ic_emoji_rain),
            //Rain
            Pair(1006, R.drawable.ic_emoji_rain),
            //Showers
            Pair(1007, R.drawable.ic_emoji_rain),
            //Downpour
            Pair(1008, R.drawable.ic_emoji_rain),
            //Rainstorm
            Pair(1009, R.drawable.ic_emoji_rain),
            //Sleet
            Pair(1010, R.drawable.ic_emoji_snow),
            //Flurry
            Pair(1011, R.drawable.ic_emoji_snow),
            //Snow
            Pair(1012, R.drawable.ic_emoji_snow),
            //Snowstorm
            Pair(1013, R.drawable.ic_emoji_snow),
            //Hail
            Pair(1014, R.drawable.ic_emoji_snow),
            //Thundershower
            Pair(1015, R.drawable.ic_emoji_thunderstorms),
            //Sandstorm
            Pair(1016, R.drawable.ic_emoji_fog),
            //Fog
            Pair(1017, R.drawable.ic_emoji_fog),
            //Hurricane
            Pair(1018, R.drawable.ic_emoji_wind),
            //Haze
            Pair(1019, R.drawable.ic_emoji_fog),
            //None
            Pair(9999, R.drawable.ic_module_help_white)
        )

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

    override fun getWeatherIconRes(context: Context, conditionCode: Int): Int {
        return weatherMap[conditionCode] ?: 0
    }

    class BoringSpan : MetricAffectingSpan() {
        override fun updateMeasureState(textPaint: TextPaint) {
        }

        override fun updateDrawState(tp: TextPaint?) {
        }
    }

}