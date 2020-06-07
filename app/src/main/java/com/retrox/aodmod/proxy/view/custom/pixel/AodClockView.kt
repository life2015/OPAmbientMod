package com.retrox.aodmod.proxy.view.custom.pixel

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.retrox.aodmod.R
import com.retrox.aodmod.SmaliImports
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.extensions.*
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.state.AodMedia
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
            textSize = 54f
            letterSpacing = 0.02f
            setShadowLayer(5f, 0f, 0f, Color.parseColor("#b2000000"))
            setGoogleSans("Light", 100)


            text = SimpleDateFormat(SmaliImports.timeFormat, Locale.ENGLISH).format(Date())
            AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
                text = SimpleDateFormat(SmaliImports.timeFormat, Locale.ENGLISH).format(Date())
            })
        }.lparams(width = wrapContent, height = wrapContent) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            topToTop = PARENT_ID
            bottomToBottom = PARENT_ID
        }

        val musicText = textView {
            id = Ids.tv_music_small
            visibility = View.GONE
            textColor = Color.WHITE
            textSize = 17f
            compoundDrawablePadding = dip(8)
            setGoogleSans()
            rightPadding = dip(12)
            setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getInstance().getDrawable(R.drawable.ic_music_unfilled), null, null, null)
        }.lparams(width = wrapContent, height = wrapContent) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            topToBottom = Ids.tv_clock
            topMargin = dip(8)
        }

        val tvToday = textView {
            id = Ids.tv_today
            textColor = Color.WHITE
            textSize = 17f
            setLineSpacing(dip(4).toFloat(), 1.0f)
            setGoogleSans()
            fun generateTodayText(weatherData: WeatherProvider.WeatherData?): SpannableStringBuilder {
                return SpannableStringBuilder().apply {
                    append(SimpleDateFormat(SmaliImports.systemDateFormat, Locale.getDefault()).format(Date()))
                    if(XPref.getAodShowWeather() && weatherData != null){
                        SmaliImports.bulletSymbol.let {
                            if(it.isNotEmpty()) {
                                append(it)
                            }else{
                                appendSpace()
                            }
                        }
                        append(weatherData.toBriefString(false))
                        appendSpace()
                    }
                    if(XPref.getShowAlarm()){
                        appendSpace()
                        append(generateAlarmText(context, false))
                        appendSpace()
                    }
                    if(XPref.getAodShowNote() && XPref.getAodNoteContent().isNotEmpty()){
                        appendln()
                        append(XPref.getAodNoteContent())
                    }
                }
            }
//            text = SimpleDateFormat("E MM. dd", Locale.ENGLISH).format(Date())
            text = generateTodayText(WeatherProvider.queryWeatherInformation(context, forceRefresh = true))
            AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
                text = generateTodayText(WeatherProvider.queryWeatherInformation(context, forceRefresh = false))
            })
            WeatherProvider.weatherLiveEvent.observe(lifecycleOwner, Observer {
                it?.let {
                    // 有数据再更新
                    text = generateTodayText(it)
                }
            })
            gravity = Gravity.CENTER
        }.lparams(width = wrapContent, height = wrapContent) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            //topToBottom = Ids.tv_clock
            topToBottom = Ids.tv_music_small
            topMargin = dip(8)
        }

        AodMedia.aodMediaLiveData.observe(lifecycleOwner, Observer {
            logD("Receive Media Data $it")
            if (!XPref.getPixelSmallMusic()) {
                return@Observer
            }

            if(it == null){
                tvToday.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = Ids.tv_clock
                }
                musicText.visibility = View.GONE
                return@Observer
            }

            musicText.visibility = View.VISIBLE
            tvToday.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = Ids.tv_music_small
            }
            val musicPlayer = it.app
            var musicPlayerIcon = ResourceUtils.getInstance(context).getDrawable(R.drawable.ic_music_unfilled)
            //Play it safe
            try {
                val icons = NotificationManager.notificationMap.values.asSequence()
                icons.filter { item -> item.packageName == musicPlayer && item.isOngoing }
                    .forEach { notification ->
                        val drawable = notification.notification.smallIcon.loadDrawable(context)
                        val bitmap: Bitmap = Bitmap.createBitmap(
                            drawable.intrinsicWidth + dip(8),
                            drawable.intrinsicHeight + dip(8),
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        drawable.setBounds(dip(4), dip(4), canvas.width - dip(4), canvas.height - dip(4))
                        drawable.draw(canvas)
                        musicPlayerIcon = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, dip(24), dip(24), false))
                        return@forEach
                    }
            }catch (e: java.lang.Exception){
                e.printStackTrace()
            }
            musicText.setCompoundDrawablesWithIntrinsicBounds(musicPlayerIcon, null, null, null)
            musicText.text = "${it.artist.concatMusic()} - ${it.name.concatMusic()}"
        })
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
                    }.lparams(width = dip(20), height = dip(24)) {
                        horizontalMargin = dip(4)
                        topMargin = dip(24)
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
            topToBottom = Ids.tv_today
        }
    }
}


