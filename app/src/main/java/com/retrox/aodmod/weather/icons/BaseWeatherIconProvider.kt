package com.retrox.aodmod.weather.icons

import android.content.Context
import android.text.style.MetricAffectingSpan
import androidx.annotation.DrawableRes
import com.retrox.aodmod.pref.XPref

abstract class BaseWeatherIconProvider {

    abstract fun getWeatherIcon(context: Context, conditionCode: Int) : MetricAffectingSpan

    @DrawableRes
    abstract fun getWeatherIconRes(context: Context, conditionCode: Int) : Int

    companion object {
        fun getWeatherIconProvider(): BaseWeatherIconProvider {
            return when(XPref.getWeatherIconStyle()){
                "google" -> PixelWeatherIconProvider()
                "oneplus" -> OnePlusWeatherIconProvider()
                "emoji" -> EmojiWeatherIconProvider()
                else -> EmojiWeatherIconProvider()
            }
        }
    }

}