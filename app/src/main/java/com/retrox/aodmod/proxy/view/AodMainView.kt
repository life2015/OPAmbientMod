package com.retrox.aodmod.proxy.view

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.transition.TransitionManager
import android.view.View
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import com.retrox.aodmod.state.AodState
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout

fun Context.aodMainView(lifecycleOwner: LifecycleOwner): View {
    return frameLayout {
        backgroundColor = Color.BLACK
        constraintLayout {
            id = Ids.ly_main

            val clockView = aodClockView(lifecycleOwner).apply {
                id = Ids.ly_clock
            }.lparams(width = matchParent, height = wrapContent) {
                startToStart = PARENT_ID
                topToTop = PARENT_ID
                endToEnd = PARENT_ID
                topMargin = dip(120)
            }
            addView(clockView)

            val messageView = importantMessageView(lifecycleOwner).apply {
                id = Ids.ly_important_message
            }.lparams(width = matchParent, height = wrapContent) {
                topToBottom = Ids.ly_clock
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topMargin = dip(46)
            }
            addView(messageView)

            val musicView = aodMusicView(lifecycleOwner).apply {
                id = Ids.ly_music_control

            }.lparams(width = matchParent, height = wrapContent) {
                endToEnd = PARENT_ID
                startToStart = PARENT_ID
                bottomToTop = Ids.tv_battery
                bottomMargin = dip(24)
            }
            if (!XPref.getMusicAodEnabled()) {
                musicView.visibility = View.INVISIBLE
            } else {
                musicView.visibility = View.VISIBLE
            }
            addView(musicView)


            val notificationView = aodNotification(lifecycleOwner).apply {
                id = Ids.ly_notification
                visibility = View.INVISIBLE
            }.lparams(width = matchParent, height = wrapContent) {
                topToBottom = Ids.ly_clock
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topMargin = dip(0)
            }
            addView(notificationView)

            textView("") {
                id = Ids.tv_battery
                textColor = Color.WHITE
                setGoogleSans()
                letterSpacing = 0.02f

                AodState.powerState.observe(lifecycleOwner, Observer {
                    it?.let {
                        var statusText = if (it.plugged) "Charging" else ""
                        if (it.fastCharge) statusText = "Dash Charging"
                        if (it.charged) statusText = "Charged"
                        if (AodState.sleepMode) statusText += " SleepMode"
                        text = "${it.level}%  $statusText"
                    }
                })
            }.lparams(width = wrapContent, height = wrapContent) {
                bottomToBottom = PARENT_ID
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                bottomMargin = dip(24)
            }

            // state animate below
            NotificationManager.notificationStatusLiveData.observeNew(lifecycleOwner, Observer {
                it?.let {
                    if (it.second == NotificationManager.REMOVED) return@let
                    if (it.first.notification.getNotificationData().isOnGoing) return@let

                    findViewById<View>(Ids.ly_important_message).visibility = View.INVISIBLE
                    TransitionManager.beginDelayedTransition(this)
                    findViewById<View>(Ids.view_divider).visibility = View.INVISIBLE
                    findViewById<View>(Ids.ll_icons).visibility = View.INVISIBLE
                    findViewById<View>(Ids.tv_today).visibility = View.INVISIBLE
                    findViewById<View>(Ids.tv_clock).apply {
                        scaleX = 0.7f
                        scaleY = 0.7f
                    }
                    this.applyConstraintSet {
                        setVisibility(Ids.ly_important_message, View.INVISIBLE)
                        setVisibility(Ids.ly_notification, View.VISIBLE)
                        setMargin(Ids.ly_clock, 3, dip(60))
//                    setMargin(Ids.ly_notification, 3, dip(12))
                    }

                    Handler().postDelayed({
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
                        findViewById<View>(Ids.view_divider).visibility = View.VISIBLE
                        findViewById<View>(Ids.ll_icons).visibility = View.VISIBLE
                        findViewById<View>(Ids.tv_today).visibility = View.VISIBLE
                    }, 5000L)
                }
            })
        }.lparams(width = matchParent, height = matchParent)
    }

}

