package com.retrox.aodmod.proxy.view.custom.pixel

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.PowerManager
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginTop
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.retrox.aodmod.R
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.extensions.setGradientTest
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.view.SharedIds
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.receiver.HeadSetReceiver
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import com.retrox.aodmod.state.AodMedia
import com.retrox.aodmod.state.AodState
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import java.lang.String
import kotlin.math.roundToInt


fun Context.aodPixelView(lifecycleOwner: LifecycleOwner): View {
    return constraintLayout {
        id = Ids.ly_main

        val clockView = aodClockView(lifecycleOwner).apply {
            id = Ids.ly_clock
            Log.d("AodId", "clock ${toHex(id)}")
        }.lparams(width = matchParent, height = wrapContent) {
            startToStart = PARENT_ID
            bottomToTop = Ids.centerView
            endToEnd = PARENT_ID
            topMargin = getTopMargin()
        }
        addView(clockView)

        val messageView = importantMessageView(lifecycleOwner).apply {
            id = Ids.ly_important_message
            Log.d("AodId", "important message ${toHex(id)}")
        }.lparams(width = matchParent, height = wrapContent) {
            topToBottom = Ids.ly_clock
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
            topMargin = dip(16)
        }
        addView(messageView)

        val musicView = aodPixelMusicView(lifecycleOwner).apply {
            id = Ids.ly_music_control
            Log.d("AodId", "music view ${toHex(id)}")
        }.lparams(width = matchParent, height = wrapContent) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            if (XPref.getMusicOffsetEnabled()) {
                bottomToBottom = PARENT_ID
                bottomMargin = dip(180)
            } else {
                bottomToTop = Ids.tv_battery
                bottomMargin = dip(24)
            }
        }
        if (!XPref.getMusicAodEnabled()) {
            musicView.visibility = View.INVISIBLE
        } else {
            if(!XPref.getPixelSmallMusic()) {
                musicView.visibility = View.VISIBLE
            }
        }
        addView(musicView)

//            val threeKeyView = aodThreeKeyView(lifecycleOwner).apply {
//                id = Ids.ly_three_key
//            }.lparams(width = wrapContent, height = wrapContent) {
//                startToStart = PARENT_ID
//                endToEnd = PARENT_ID
//                bottomToTop = Ids.ly_clock
//                bottomMargin = dip(6)
//            }
//            addView(threeKeyView)


        val notificationView = aodNotification(lifecycleOwner).apply {
            id = Ids.ly_notification
            visibility = View.INVISIBLE
            Log.d("AodId", "notification view ${toHex(id)}")
        }.lparams(width = matchParent, height = wrapContent) {
            topToTop = PARENT_ID
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
        }

        notificationView.post {
            updateNotificationLocation()
        }

        addView(notificationView)

        val headSetStatusView = aodHeadSetView(lifecycleOwner).apply {
            id = Ids.ly_headset_status
            visibility = View.INVISIBLE
            Log.d("AodId", "headset status ${toHex(id)}")
        }.lparams(width = matchParent, height = wrapContent) {
            endToEnd = PARENT_ID
            startToStart = PARENT_ID
            if (XPref.getMusicOffsetEnabled()) {
                bottomToBottom = PARENT_ID
                bottomMargin = dip(180)
            } else {
                bottomToTop = Ids.tv_battery
                bottomMargin = dip(24)
            }
        }
        addView(headSetStatusView)

        textView {
            id = Ids.tv_battery
            textColor = Color.WHITE
            setGoogleSans()
            letterSpacing = 0.02f
            Log.d("AodId", "battery ${toHex(id)}")
            AodState.powerState.observe(lifecycleOwner, Observer {
                it?.let {
                    var statusText = if (it.plugged) " • " + ResourceUtils.getInstance(context).getString(R.string.charging_charging) else ""
                    if (it.fastCharge) statusText = " • " + ResourceUtils.getInstance(context).getString(R.string.charging_quick_charging)
                    if (it.charged) statusText = " • " + ResourceUtils.getInstance(context).getString(R.string.charging_charged)
                    if (AodState.sleepMode) statusText += " • " + ResourceUtils.getInstance(context).getString(R.string.charging_sleep_mode)
                    text = "${it.level}%$statusText"
                }
            })
        }.lparams(width = wrapContent, height = wrapContent) {
            bottomToBottom = PARENT_ID
            startToStart = PARENT_ID
            endToEnd = PARENT_ID
            bottomMargin = dip(24)
        }

        // 使用WakeLock来保证Handler计时的准确以及避免休眠
        val animWakeLock = context.getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AODMOD:MainViewAnim")

        val headSetViewReset = Runnable {
            val musicViewEnabled = XPref.getMusicAodEnabled()
            TransitionManager.beginDelayedTransition(this)
            findViewById<View>(Ids.ly_headset_status).visibility = View.INVISIBLE
            if (musicViewEnabled && AodMedia.aodMediaLiveData.value != null) { // 避免音乐显示关闭的状态被冲走
                if(!XPref.getPixelSmallMusic()) {
                    findViewById<View>(Ids.ly_music_control).visibility = View.VISIBLE
                }
            }
            if (animWakeLock.isHeld) animWakeLock.release()
        }
        if(!XPref.isSettings()) {
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
                    findViewById<View>(Ids.ly_music_control).visibility = View.INVISIBLE
                    findViewById<View>(Ids.ly_headset_status).visibility = View.VISIBLE

                    postDelayed(headSetViewReset, delay)
                }
            })
        }

        val notificationAnimReset = Runnable {
            TransitionManager.beginDelayedTransition(this)
            this.applyConstraintSet {
                setVisibility(Ids.ly_important_message, View.VISIBLE)
                setVisibility(Ids.ly_notification, View.INVISIBLE)
                setMargin(Ids.ly_clock, 3, dip(120))
//                        setMargin(Ids.ly_notification, 3, dip(46))
            }
            findViewById<View>(Ids.tv_clock).apply {
                scaleX = 1.0f
                scaleY = 1.0f
            }
            findViewById<View>(Ids.ly_notification).visibility = View.INVISIBLE
            findViewById<View>(Ids.ll_icons).visibility = View.VISIBLE
            findViewById<View>(Ids.tv_today).visibility = View.VISIBLE
            if(XPref.getPixelSmallMusic() && AodMedia.aodMediaLiveData.value != null) {
                findViewById<View>(Ids.tv_music_small).visibility = View.VISIBLE
            }

            if (animWakeLock.isHeld) animWakeLock.release()
        }
        // state animate below
        NotificationManager.notificationStatusLiveData.observeNewOnly(lifecycleOwner, Observer {
            it?.let {
                if (it.second == NotificationManager.REMOVED) return@let
                if (it.first.notification.getNotificationData().shouldBeSkipped) return@let

                removeCallbacks(notificationAnimReset) // 尝试修复通知动画的状态问题
                if (animWakeLock.isHeld) {
                    animWakeLock.release()
                }
                animWakeLock.acquire(10000L)

                findViewById<View>(Ids.ly_important_message).visibility = View.INVISIBLE
                TransitionManager.beginDelayedTransition(this)
                findViewById<View>(Ids.ll_icons).visibility = View.INVISIBLE
                findViewById<View>(Ids.tv_today).visibility = View.INVISIBLE
                findViewById<View>(Ids.tv_music_small).visibility = View.GONE
                findViewById<View>(Ids.tv_clock).apply {
                    scaleX = 0.7f
                    scaleY = 0.7f
                }
                this.applyConstraintSet {
                    setVisibility(Ids.ly_important_message, View.INVISIBLE)
                    setVisibility(Ids.ly_notification, View.VISIBLE)
//                    setMargin(Ids.ly_notification, 3, dip(12))
                }

                postDelayed(notificationAnimReset, 5000L)
            }
        })

        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            applyRecursively {
                val pack = ThemeManager.getCurrentColorPack()
                logD("Pack ${pack.themeName}")
                when (it) {
                    is TextView -> it.setGradientTest()
                    is ImageView -> it.imageTintList = ColorStateList.valueOf(Color.parseColor(pack.tintColor))
                }
            }
        }
        if(!XPref.isSettings()) {
            val filterView = View(context).apply {
                id = SharedIds.filterView
                backgroundColor = Color.BLACK
                alpha = 0f
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                elevation = 8f
            }

            addView(filterView)
        }

        val centerView = View(context).apply {
            id = Ids.centerView
            layoutParams = ConstraintLayout.LayoutParams(10,10).apply {
                if(!XPref.isSettings()) {
                    topToTop = PARENT_ID
                }
                bottomToBottom = PARENT_ID
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                bottomMargin = dip(16)
            }
        }
        addView(centerView)

    }


}

private fun ConstraintLayout.updateNotificationLocation(){
    val clockTextView = findViewById<TextView>(Ids.tv_clock)
    val iv_notification_image = findViewById<ImageView>(Ids.iv_notification_image)

    iv_notification_image.layoutParams.apply {
        this as LinearLayout.LayoutParams
        val intArray = intArrayOf(0, 0)
        clockTextView.getLocationOnScreen(intArray)
        val marginTopSize = kotlin.run {
            var top = intArray[1]
            top += clockTextView.height// * 0.7).roundToInt()
            top += dip(16)
            top
        }
        Log.d("OPAodMod", "updating notification location to $marginTopSize")
        topMargin = marginTopSize
        iv_notification_image.requestLayout()
        iv_notification_image.invalidate()
    }
}

fun toHex(n: Int): kotlin.String {
    return String.format("0x%02X", 0xFF and n)
}

private fun View.getTopMargin() : Int {
    return if(XPref.isSettings()){
        0
    }else{
        dip(120)
    }
}

