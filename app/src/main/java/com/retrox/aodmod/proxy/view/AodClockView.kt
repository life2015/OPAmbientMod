package com.retrox.aodmod.proxy.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.view.Gravity
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import android.view.View
import android.widget.LinearLayout
import com.retrox.aodmod.SmaliImports
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.weather.WeatherProvider
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import java.text.SimpleDateFormat
import java.util.*


fun Context.aodClockView(lifecycleOwner: LifecycleOwner): View {
    return constraintLayout {
        textView {
            id = Ids.tv_clock
            textColor = Color.WHITE
            textSize = 50f
            letterSpacing = 0.1f
            setGoogleSans()

            text = SimpleDateFormat(SmaliImports.timeFormat, Locale.ENGLISH).format(Date())
            AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
                text = " " + SimpleDateFormat(SmaliImports.timeFormat, Locale.ENGLISH).format(Date()) + " " // 玄学空格？
            })
        }.lparams(width = wrapContent, height = wrapContent) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            topToTop = PARENT_ID
        }
        textView {
            id = Ids.tv_today
            textColor = Color.WHITE
            textSize = 18f
            setGoogleSans()
            fun generateDateBrief(weatherData: WeatherProvider.WeatherData?): String {
                if (XPref.getAodShowWeather() && weatherData != null) { // 这里变更一下逻辑 没有天气就不显示了
                    return SimpleDateFormat(SmaliImports.systemDateFormat, Locale.ENGLISH).format(Date()) + " ${SmaliImports.bulletSymbol} " + (weatherData.toBriefString())
                } else {
                    return SimpleDateFormat(SmaliImports.systemDateFormat, Locale.ENGLISH).format(Date())
                }
            }
//            text = SimpleDateFormat("E MM. dd", Locale.ENGLISH).format(Date())
            text = generateDateBrief(WeatherProvider.queryWeatherInformation(context, forceRefresh = true))
            AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
                text = generateDateBrief(WeatherProvider.queryWeatherInformation(context, forceRefresh = false))
            })
            WeatherProvider.weatherLiveEvent.observe(lifecycleOwner, Observer {
                it?.let {
                    // 有数据再更新
                    text = generateDateBrief(it)
                }
            })
            gravity = Gravity.CENTER
        }.lparams(width = wrapContent, height = wrapContent) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            topToBottom = Ids.tv_clock
            topMargin = dip(12)
        }
        view {
            backgroundColor = Color.WHITE
            id = Ids.view_divider
//            visibility = View.INVISIBLE
        }.lparams(width = dip(12), height = dip(2)) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            topToBottom = Ids.tv_today
            topMargin = dip(18)
        }
        linearLayout {
            id = Ids.ll_icons
            orientation = LinearLayout.HORIZONTAL

            val refreshBlock = {
                val icons = NotificationManager.notificationMap.values.asSequence()
                    .map { it.notification }
                    .filter { it.extras.getInt(NotificationManager.EXTRA_IMPORTANTCE, 2) > 1 } // 过滤掉不重要通知
//                    .filter { it. }
                    .map {
                        try {
                            it.smallIcon.loadDrawable(context)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            return@map null
                        }
                    }
                    .filterNot { it == null || it is VectorDrawable }
                    .toList()

//                MainHook.logD("icons: $icons")

                removeAllViews()
                icons.take(6).forEach {
                    imageView {
                        setImageDrawable(it)
//                        colorFilter = grayColorFilter
                        imageTintList = ColorStateList.valueOf(Color.WHITE)
                    }.lparams(width = dip(24), height = dip(24)) {
                        horizontalMargin = dip(4)
                    }
                }
            }
            refreshBlock.invoke()
            NotificationManager.notificationStatusLiveData.observe(lifecycleOwner, Observer {
                refreshBlock.invoke()
            })

        }.lparams(width = wrapContent, height = wrapContent) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            topToBottom = Ids.view_divider
            topMargin = dip(24)
        }
    }
}


