package com.retrox.aodmod.weather.icons

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.MetricAffectingSpan
import androidx.core.graphics.drawable.toBitmap
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.toDp
import com.retrox.aodmod.extensions.toPx

class PixelWeatherIconProvider : BaseWeatherIconProvider() {

    companion object {
        private val weatherMap = mapOf(
            //Sunny
            Pair(1001, R.drawable.weather_google_sunny),
            //Sunny Intervals
            Pair(1002, R.drawable.weather_google_partly_cloudy),
            //Cloudy
            Pair(1003, R.drawable.weather_google_cloudy),
            //Overcast
            Pair(1004, R.drawable.weather_google_mostly_cloudy),
            //Drizzle
            Pair(1005, R.drawable.weather_google_drizzle),
            //Rain
            Pair(1006, R.drawable.weather_google_showers),
            //Showers
            Pair(1007, R.drawable.weather_google_scattered_showers),
            //Downpour
            Pair(1008, R.drawable.weather_google_heavy_rain),
            //Rainstorm
            Pair(1009, R.drawable.weather_google_heavy_rain),
            //Sleet
            Pair(1010, R.drawable.weather_google_wintry_mix),
            //Flurry
            Pair(1011, R.drawable.weather_google_snow_flurries),
            //Snow
            Pair(1012, R.drawable.weather_google_snow_showers),
            //Snowstorm
            Pair(1013, R.drawable.weather_google_snow_showers),
            //Hail
            Pair(1014, R.drawable.weather_google_wintry_mix),
            //Thundershower
            Pair(1015, R.drawable.weather_google_scattered_thunderstorms),
            //Sandstorm
            Pair(1016, R.drawable.weather_google_windy),
            //Fog
            Pair(1017, R.drawable.weather_google_haze_fog),
            //Hurricane
            Pair(1018, R.drawable.weather_google_windy),
            //Haze
            Pair(1019, R.drawable.weather_google_haze_fog),
            //None
            Pair(9999, R.drawable.ic_module_help_white)
        )
    }

    override fun getWeatherIcon(context: Context, conditionCode: Int): MetricAffectingSpan {
        val icon = ResourceUtils.getInstance(context).getDrawable(weatherMap[conditionCode] ?: R.drawable.ic_module_help_white)
        return ImageSpan(context, icon.toBitmap(20.toPx, 20.toPx), DynamicDrawableSpan.ALIGN_CENTER)
    }

    override fun getWeatherIconRes(context: Context, conditionCode: Int): Int {
        return weatherMap[conditionCode] ?: 0
    }

}