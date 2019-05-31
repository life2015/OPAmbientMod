package com.retrox.aodmod.proxy.view.custom.music

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.PowerManager
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.extensions.setGradientTest
import com.retrox.aodmod.proxy.view.Ids
import com.retrox.aodmod.proxy.view.aodHeadSetView
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.receiver.HeadSetReceiver
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.state.AodMedia
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import java.text.SimpleDateFormat
import java.util.*

fun Context.pureAodMusicMainView(lifecycleOwner: LifecycleOwner): View {
    return frameLayout {
        backgroundColor = Color.BLACK

        constraintLayout {
            id = Ids.ly_main

//            val clockView = flatStyleAodClock(lifecycleOwner).apply {
//                id = Ids.ly_clock
//            }.lparams(width = matchParent, height = wrapContent) {
//                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
//                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//                topMargin = dip(140)
//                leftMargin = dip(36)
//            }
//            addView(clockView)

            val musicView = aodMusicView(lifecycleOwner).lparams(width = wrapContent, height = wrapContent) {
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topToTop = PARENT_ID
                topMargin = dip(240)
            }
            addView(musicView)


//            textView {
//                id = Ids.tv_battery
//                textColor = Color.WHITE
//                setGoogleSans()
//                letterSpacing = 0.02f
//
//                AodState.powerState.observe(lifecycleOwner, Observer {
//                    it?.let {
//                        var statusText = if (it.plugged) "Charging" else ""
//                        if (it.fastCharge) statusText = "Dash Charging"
//                        if (it.charged) statusText = "Charged"
//                        if (AodState.sleepMode) statusText += " SleepMode"
//                        text = "${it.level}%  $statusText"
//                    }
//                })
//            }.lparams(width = wrapContent, height = wrapContent) {
//                bottomToBottom = PARENT_ID
//                startToStart = PARENT_ID
//                endToEnd = PARENT_ID
//                bottomMargin = dip(24)
//            }


            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                applyRecursively {
                    when (it) {
                        is TextView -> it.setGradientTest()
                        is ImageView -> it.imageTintList =
                            ColorStateList.valueOf(Color.parseColor(ThemeManager.getCurrentColorPack().tintColor))
                    }
                }
            }

        }.lparams(width = matchParent, height = matchParent)
    }
}

private fun Context.aodMusicView(lifecycleOwner: LifecycleOwner): View {
    return verticalLayout {
        textView {
            id = Ids.tv_clock
            textColor = Color.WHITE
            textSize = 26f
            letterSpacing = 0.1f
            setGoogleSans()
            text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date())
            AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
                text = "  " + SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date()) + "  " // 玄学空格？
            })
        }.lparams(wrapContent, wrapContent) {
            bottomMargin = dip(14)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val musicName = textView {
            textColor = Color.WHITE
            textSize = 18f
            letterSpacing = 0.02f
            setGoogleSans(style = "Medium")
            text = "Dark Side Of The Moon"
            gravity = Gravity.CENTER_HORIZONTAL
        }.lparams(wrapContent, wrapContent) {
            bottomMargin = dip(8)
            gravity = Gravity.CENTER_HORIZONTAL
            horizontalMargin = dip(16)
        }

        val musicArtist = textView {
            textColor = Color.WHITE
            textSize = 16f
            letterSpacing = 0.05f
            setGoogleSans()
            visibility = View.GONE
            gravity = Gravity.CENTER_HORIZONTAL
        }.lparams(wrapContent, wrapContent) {
            bottomMargin = dip(12)
            gravity = Gravity.CENTER_HORIZONTAL
            horizontalMargin = dip(16)
        }

        val unReadNotification = textView {
            textColor = Color.WHITE
            textSize = 14f
            letterSpacing = 0.05f
            setGoogleSans()
            visibility = View.GONE
        }.lparams(wrapContent, wrapContent) {
            bottomMargin = dip(8)
            gravity = Gravity.CENTER_HORIZONTAL
        }


        val headSetStatusView = aodHeadSetView(lifecycleOwner).apply {
            id = Ids.ly_headset_status
            visibility = View.INVISIBLE
        }.lparams(wrapContent, wrapContent) {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        addView(headSetStatusView)

        // 使用WakeLock来保证Handler计时的准确以及避免休眠
        val animWakeLock = context.getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AODMOD:PureMusicViewAnim")

        val headSetViewReset = Runnable {
            TransitionManager.beginDelayedTransition(this)
            findViewById<View>(Ids.ly_headset_status).visibility = View.GONE
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
                findViewById<View>(Ids.ly_headset_status).visibility = View.VISIBLE

                postDelayed(headSetViewReset, delay)
            }
        })

        if (AodMedia.aodMediaLiveData.value != null) {
            AodMedia.aodMediaLiveData.observe(lifecycleOwner, Observer {
                if (it == null) {
                    musicArtist.visibility = View.GONE
                    musicName.text = "Dark Side Of The Moon"
                    return@Observer
                }

                musicArtist.visibility = View.VISIBLE
                musicName.text = it.name
                musicArtist.text = it.artist
            })
        }

        var currentId = 0

        NotificationManager.notificationStatusLiveData.observeNewOnly(lifecycleOwner, Observer {
            it?.let { (sbn, status) ->
                if (currentId == 0){
                    currentId = sbn.id
                } else if (status == "Removed" && currentId == sbn.id) {
                    unReadNotification.text = ""
                    unReadNotification.visibility = View.GONE
                }

                if (status == "Removed") return@let
                if (it.first.notification.getNotificationData().isOnGoing) return@let

                val notification = NotificationManager.notificationMap[sbn.key]?.notification ?: return@let
                notification.getNotificationData().let {
                    unReadNotification.visibility = View.VISIBLE
                    unReadNotification.text = "${it.appName} 有未读通知"
                }

//                val icon = notification.smallIcon.loadDrawable(context)
//                notificationImage.setImageDrawable(icon)
            }
        })

    }
}
