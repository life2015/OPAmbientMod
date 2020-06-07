package com.retrox.aodmod.proxy.view.custom.oneplus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.retrox.aodmod.R
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.extensions.toPx
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.state.AodMedia
import com.retrox.aodmod.weather.WeatherProvider
import com.retrox.aodmod.weather.icons.BaseWeatherIconProvider
import org.jetbrains.anko.*

fun Context.aodWeatherView(lifecycleOwner: LifecycleOwner): View {
    val weatherIconProvider = BaseWeatherIconProvider.getWeatherIconProvider()
    return linearLayout {
        visibility = if(XPref.getAodShowWeather()) View.VISIBLE else View.INVISIBLE
        orientation = LinearLayout.HORIZONTAL
        bottomPadding = 8.toPx
        clipToPadding = true
        gravity = Gravity.CENTER_HORIZONTAL
        val imageIcon = imageView {
            id = Ids.iv_weather_icon
            visibility = if(XPref.getWeatherShowSymbol()){
                View.VISIBLE
            }else{
                View.GONE
            }
        }.lparams(width = dip(24), height = dip(24)) {
            gravity = Gravity.CENTER
            marginEnd = dip(8)
        }

        verticalLayout {
            gravity = Gravity.CENTER_HORIZONTAL
            val weatherTopLine = textView("") {
                //            gravity = Gravity.CENTER
                id = Ids.tv_weather_top_line
                textColor = Color.WHITE
                textSize = 16f
                bottomPadding = dip(4)
                gravity = Gravity.CENTER_HORIZONTAL
                setGoogleSans()
            }.lparams(width = wrapContent, height = wrapContent)
            val weatherBottomLine = textView("") {
                id = Ids.tv_weather_bottom_line
                textColor = Color.WHITE
                textSize = 14f
                gravity = Gravity.CENTER_HORIZONTAL
                setGoogleSans()
            }.lparams(width = wrapContent, height = wrapContent)

            WeatherProvider.weatherLiveEvent.observe(lifecycleOwner, Observer {
                it?.let {
                    val icon = weatherIconProvider.getWeatherIconRes(context, it.weatherCode)
                    imageIcon.setImageDrawable(ResourceUtils.getInstance(context).getDrawable(icon))
                    val topLineBuilder = StringBuilder().apply {
                        if(XPref.getWeatherShowCondition()){
                            append(it.weatherName)
                        }
                        if(XPref.getWeatherShowTemperature()){
                            append(" ${it.temperature}${it.temperatureUnit}")
                        }
                    }
                    weatherTopLine.text = topLineBuilder.toString()
                    val bottomLineBuilder = StringBuilder().apply {
                        if(XPref.getWeatherShowTemperature()){
                            append("${it.temperatureLow}${it.temperatureUnit} / ${it.temperatureHigh}${it.temperatureUnit}")
                        }
                        if(XPref.getWeatherShowCity()){
                            append(" - ${it.cityName}")
                        }
                    }
                    weatherBottomLine.text = bottomLineBuilder.toString()
                    visibility = View.VISIBLE
                } ?: run {
                    visibility = View.INVISIBLE
                }
            })
        }
    }

}
