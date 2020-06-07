package com.retrox.aodmod.proxy.view.custom.flat

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.os.PowerManager
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.retrox.aodmod.R
import com.retrox.aodmod.SmaliImports
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.extensions.*
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.view.Ids
import com.retrox.aodmod.proxy.view.aodHeadSetView
import com.retrox.aodmod.proxy.view.custom.components.wordClock
import com.retrox.aodmod.receiver.HeadSetReceiver
import com.retrox.aodmod.service.notification.NotificationData
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.state.AodMedia
import com.retrox.aodmod.state.AodState
import com.retrox.aodmod.weather.WeatherProvider
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

// 给跑马灯用的效果 但是也可以用来约束TextView宽度
val maxTextViewWidth = 260

fun Context.flatStyleAodClock(lifecycleOwner: LifecycleOwner): View {
    return verticalLayout {


        if (XPref.getForceWordClockOnFlat()) {
            wordClock(lifecycleOwner) {
                textSize = 29f
                id = Ids.tv_clock
            }.lparams(wrapContent, wrapContent) {
                bottomMargin = dip(20)
            }
        } else {
            textView {
                id = Ids.tv_clock
                textColor = Color.WHITE
                textSize = 42f
                letterSpacing = 0.1f
                setGoogleSans()
                text = SimpleDateFormat(SmaliImports.timeFormat, Locale.ENGLISH).format(Date())
                AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
                    text = SimpleDateFormat(SmaliImports.timeFormat, Locale.ENGLISH).format(Date()) + "  " // 玄学空格？
                })
            }.lparams(wrapContent, wrapContent) {
                bottomMargin = dip(16)
            }
        }


        textView {
            id = Ids.tv_today
            textColor = Color.WHITE
            textSize = 18f
            setGoogleSans()
            fun generateDateBrief(weatherData: WeatherProvider.WeatherData?): SpannableStringBuilder {
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
                        append(weatherData.toBriefString())
                        appendSpace()
                    }
                    if(XPref.getShowAlarm()){
                        appendSpace()
                        append(generateAlarmText(context, false))
                        appendSpace()
                    }
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
            gravity = Gravity.START
        }.lparams(wrapContent, wrapContent) {
            bottomMargin = dip(6)
        }

        textView {// 备忘
            textColor = Color.WHITE
            setGoogleSans()
            letterSpacing = 0.02f
            textSize = 16f

            maxWidth = dip(maxTextViewWidth)
            visibility = View.GONE
            if (XPref.getAodShowNote() && !getNewAodNoteContent().isBlank()) {
                visibility = View.VISIBLE
                text = getNewAodNoteContent()
            }
        }.lparams(wrapContent, wrapContent) {
            bottomMargin = dip(6)
        }

        textView {
            id = Ids.tv_battery
            textColor = Color.WHITE
            setGoogleSans()
            letterSpacing = 0.02f
            textSize = 16f

            AodState.powerState.observe(lifecycleOwner, Observer {
                it?.let {
                    var statusText = if (it.plugged) "Charging" else ""
                    if (it.fastCharge) statusText = "Quick Charging"
                    if (it.charged) statusText = "Charged"
                    if (AodState.sleepMode) statusText += " SleepMode"
                    text = "${it.level}%  $statusText"
                }
            })
        }.lparams(wrapContent, wrapContent)

        // 使用WakeLock来保证Handler计时的准确以及避免休眠
        val animWakeLock = context.getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AODMOD:FlatClockViewAnim")


        val musicView = flatMusicInClock(lifecycleOwner).apply {
            id = Ids.ly_music_control
        }.lparams(wrapContent, wrapContent) {
            topMargin = dip(8)
        }
        addView(musicView)

        val headSetStatusView = aodHeadSetView(lifecycleOwner).apply {
            id = Ids.ly_headset_status
            visibility = View.GONE
        }.lparams(wrapContent, wrapContent) {
            topMargin = dip(8)
        }
        addView(headSetStatusView)

        val headSetViewReset = Runnable {
            val musicViewEnabled = XPref.getMusicAodEnabled()
            TransitionManager.beginDelayedTransition(this)
            findViewById<View>(Ids.ly_headset_status).visibility = View.GONE
            if (musicViewEnabled && AodMedia.aodMediaLiveData.value != null) { // 避免音乐显示关闭的状态被冲走
                findViewById<View>(Ids.ly_music_control).visibility = View.VISIBLE
            }
            if (animWakeLock.isHeld) animWakeLock.release()
        }
        HeadSetReceiver.headSetConnectLiveEvent.observeNewOnly(lifecycleOwner, Observer {
            it?.let {
                // do Animation now
                removeCallbacks(headSetViewReset)
                if (animWakeLock.isHeld) {
                    animWakeLock.release()
                }
                animWakeLock.acquire(10000L)

                val delay = when (it) {
                    is HeadSetReceiver.ConnectionState.HeadSetConnection -> 4000L
                    is HeadSetReceiver.ConnectionState.BlueToothConnection -> 8000L
                    is HeadSetReceiver.ConnectionState.VolumeChange -> 2000L
                    is HeadSetReceiver.ConnectionState.ZenModeChange -> 4000L
                }

                TransitionManager.beginDelayedTransition(this)
                findViewById<View>(Ids.ly_music_control).visibility = View.GONE
                findViewById<View>(Ids.ly_headset_status).visibility = View.VISIBLE

                postDelayed(headSetViewReset, delay)
            }
        })

        val flatNotificationView = flatNotificationInClock(lifecycleOwner).apply {
            id = Ids.ly_flat_clock_notification
            visibility = View.GONE
        }.lparams(wrapContent, wrapContent) {
            topMargin = dip(8)
        }
        addView(flatNotificationView)

        // 暂时不需要Reset
        val flatNotificationViewReset = Runnable {
            TransitionManager.beginDelayedTransition(this)
            findViewById<View>(Ids.ly_flat_clock_notification).visibility = View.GONE
            if (animWakeLock.isHeld) animWakeLock.release()
        }

        NotificationManager.notificationStatusLiveData.observeNewOnly(lifecycleOwner, Observer {
            it?.let {
                if (it.second == NotificationManager.REMOVED) return@let
                if (it.first.notification.getNotificationData().isOnGoing) return@let

//                removeCallbacks(flatNotificationViewReset)
                if (animWakeLock.isHeld) {
                    animWakeLock.release()
                }
                animWakeLock.acquire(10000L)
                TransitionManager.beginDelayedTransition(this)
                flatNotificationView.visibility = View.VISIBLE

            }
        })

        flatNotificationLayout(lifecycleOwner)
    }
}

private fun Context.flatMusicInClock(lifecycleOwner: LifecycleOwner): View {
    return linearLayout {
        orientation = LinearLayout.HORIZONTAL

        val image = imageView {
            setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_music))
            visibility = View.GONE
        }.lparams(width = dip(24), height = dip(24)) {
            gravity = Gravity.BOTTOM
            rightMargin = dip(4)
        }

        val musicText = marqueTextView {
            //            gravity = Gravity.CENTER
            id = Ids.tv_music
            textColor = Color.WHITE
            textSize = 16f
            gravity = Gravity.BOTTOM
            setGoogleSans()

        }.lparams(width = dip(maxTextViewWidth), height = wrapContent) {
            //            horizontalMargin = dip(4)
            gravity = Gravity.BOTTOM
        }

        visibility = View.GONE

        AodMedia.aodMediaLiveData.observe(lifecycleOwner, Observer {
            logD("Receive Media Data $it")
            if (!XPref.getMusicAodEnabled()) { // 修复音乐显示关不掉的bug
                return@Observer
            }
            TransitionManager.beginDelayedTransition(this.rootView as ViewGroup)
            if (it == null) {
                visibility = View.GONE
            }
            it?.let {
                visibility = View.VISIBLE
                musicText.text = "${it.name} - ${it.artist}"
//                musicText.stopScroll()
//                musicText.startScroll()
            }
        })
    }
}

private fun Context.flatNotificationInClock(lifecycleOwner: LifecycleOwner): View {
    return verticalLayout {

        var postTime = -1L
        var notificationData: NotificationData? = null

        val title = textView {
            textColor = Color.WHITE
            setGoogleSans()
            textSize = 14f
        }.lparams(wrapContent, wrapContent)

        val content = textView {
            textColor = Color.WHITE
            setGoogleSans()
            textSize = 14f
            maxLines = 10
            ellipsize = TextUtils.TruncateAt.END
            maxWidth = dip(maxTextViewWidth + 30)

//            setAutoSizeTextTypeUniformWithConfiguration(12,16,1, TypedValue.COMPLEX_UNIT_SP)
        }.lparams(wrapContent, wrapContent) {
            topMargin = dip(4)
        }

        AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
            if (postTime < 0) return@Observer
            val l = System.currentTimeMillis() - postTime
            val hour = l / (60 * 60 * 1000)
            val min = l / (60 * 1000) - hour * 60
            if (hour.toInt() > 0) {
                title.text = "${notificationData?.appName} · ${hour.toInt()} hour"
            } else if (min.toInt() == 0) {
                title.text = "${notificationData?.appName} · recently"
            } else {
                title.text = "${notificationData?.appName} · ${min.toInt()} min"
            }

            logD("SAod Log tick")
        })


        logD("observe noti !")// 这里 liveevent本身observe会烂 -> 后来修复了
        NotificationManager.notificationStatusLiveData.observe(lifecycleOwner, Observer {
            it?.let { (sbn, status) ->
                if (status == "Removed") return@let
                if (it.first.notification.getNotificationData().isOnGoing) return@let

                val notification = NotificationManager.notificationMap[sbn.key]?.notification ?: return@let
                notification.getNotificationData().let {
                    postTime = sbn.postTime
                    notificationData = it

                    title.text = "${it.appName} · now"
                    val contentText = it.title + "\n" + it.content
                    if (contentText.length > 100) {
                        content.textSize = 14f
                    }
                    content.text = contentText

                    logD("AOD Noti Size ${content.text.toString().length}")
                }

//                val icon = notification.smallIcon.loadDrawable(context)
//                notificationImage.setImageDrawable(icon)
            }
        })

    }
}

private fun _LinearLayout.flatNotificationLayout(lifecycleOwner: LifecycleOwner): View {
    return linearLayout {
        id = Ids.ll_icons
        orientation = LinearLayout.HORIZONTAL

        val refreshBlock = Runnable {
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

//            MainHook.logD("icons: $icons")

            removeAllViews()
            icons.take(6).forEach {
                imageView {
                    setImageDrawable(it)
//                        colorFilter = grayColorFilter
                    imageTintList = ColorStateList.valueOf(Color.WHITE)
                }.lparams(width = dip(20), height = dip(20)) {
                    rightMargin = dip(6)
                    gravity = Gravity.CENTER_VERTICAL
                }
            }
        }
        refreshBlock.run()
        NotificationManager.notificationStatusLiveData.observe(lifecycleOwner, Observer {
            removeCallbacks(refreshBlock)
            postDelayed(refreshBlock, 500L)
        })

    }.lparams(width = wrapContent, height = wrapContent) {
        topMargin = dip(12)
    }
}