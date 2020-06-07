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

class OnePlusWeatherIconProvider : BaseWeatherIconProvider() {

    companion object {
        private val weatherMap = mapOf(
            //Sunny
            Pair(1001, R.drawable.op_ic_weather_sunny),
            //Sunny Intervals
            Pair(1002, R.drawable.op_ic_weather_overcast),
            //Cloudy
            Pair(1003, R.drawable.op_ic_weather_cloudy),
            //Overcast
            Pair(1004, R.drawable.op_ic_weather_overcast),
            //Drizzle
            Pair(1005, R.drawable.op_ic_weather_rain),
            //Rain
            Pair(1006, R.drawable.op_ic_weather_rain),
            //Showers
            Pair(1007, R.drawable.op_ic_weather_rain),
            //Downpour
            Pair(1008, R.drawable.op_ic_weather_rain),
            //Rainstorm
            Pair(1009, R.drawable.op_ic_weather_rain),
            //Sleet
            Pair(1010, R.drawable.op_ic_weather_sleet),
            //Flurry
            Pair(1011, R.drawable.op_ic_weather_snow),
            //Snow
            Pair(1012, R.drawable.op_ic_weather_snow),
            //Snowstorm
            Pair(1013, R.drawable.op_ic_weather_sandstorm),
            //Hail
            Pair(1014, R.drawable.op_ic_weather_sleet),
            //Thundershower
            Pair(1015, R.drawable.op_ic_weather_rain),
            //Sandstorm
            Pair(1016, R.drawable.op_ic_weather_typhoon),
            //Fog
            Pair(1017, R.drawable.op_ic_weather_fog),
            //Hurricane
            Pair(1018, R.drawable.op_ic_weather_typhoon),
            //Haze
            Pair(1019, R.drawable.op_ic_weather_haze),
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