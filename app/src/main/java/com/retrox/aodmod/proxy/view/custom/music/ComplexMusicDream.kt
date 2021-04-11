package com.retrox.aodmod.proxy.view.custom.music

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.PowerManager
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.SmaliImports
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.extensions.setGradientTest
import com.retrox.aodmod.proxy.AbsDreamView
import com.retrox.aodmod.proxy.DreamProxy
import com.retrox.aodmod.proxy.view.Ids
import com.retrox.aodmod.proxy.view.aodHeadSetView
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.receiver.HeadSetReceiver
import com.retrox.aodmod.remote.lyric.LrcSync
import com.retrox.aodmod.service.notification.NotificationData
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.state.AodMedia
import com.retrox.aodmod.state.AodState
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import java.text.SimpleDateFormat
import java.util.*

class ComplexMusicDream(dreamProxy: DreamProxy) : AbsDreamView(dreamProxy) {
    override val layoutTheme: String
        get() = "PureMusic2"

    companion object {
        private val clock = View.generateViewId()
        private val bottomContainer = View.generateViewId()
        private val musicView = View.generateViewId()
        private val notiViewId = View.generateViewId()
    }

    override fun onCreateView(): View {
        return context.constraintLayout root@{

            context.clockView(this@ComplexMusicDream).lparams(matchParent, wrapContent) {
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                topToTop = PARENT_ID
                topMargin = dip(120)
                horizontalMargin = dip(44)
            }.apply {
                id = clock
                this@root.addView(this)
            }

            context.musicView(this@ComplexMusicDream).lparams(matchParent, wrapContent) {
                startToStart = PARENT_ID
                topToBottom = clock
                topMargin = dip(16)
                horizontalMargin = dip(44)
            }.apply {
                id = musicView
                this@root.addView(this)
            }

            context.notiView(this@ComplexMusicDream).lparams(matchParent, wrapContent) {
                startToStart = PARENT_ID
                topToBottom = musicView
                topMargin = dip(44)
                horizontalMargin = dip(44)
            }.apply {
                id = notiViewId
                this@root.addView(this)
            }


            val bottomContainer = verticalLayout {
                id = bottomContainer

                val musicLyric = textView {
                    textColor = Color.WHITE
                    textSize = 15f
                    letterSpacing = 0.05f
                    setGoogleSans()
                    visibility = View.GONE
                    gravity = Gravity.CENTER_HORIZONTAL

                    LrcSync.currentLrcRowLive.observe(this@ComplexMusicDream, Observer {
                        if (it == null) {
                            visibility = View.GONE
                        } else {
                            visibility = View.VISIBLE
                            text = it.content
                        }
                    })

                }.lparams(matchParent, wrapContent)


            }.lparams(matchParent, wrapContent) {
                bottomMargin = dip(24)
                startToStart = PARENT_ID
                endToEnd = PARENT_ID
                bottomToBottom = PARENT_ID
                horizontalMargin = dip(44)
            }

            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                applyRecursively {
                    when (it) {
                        is TextView -> it.setGradientTest()
                        is ImageView -> it.imageTintList =
                            ColorStateList.valueOf(Color.parseColor(ThemeManager.getCurrentColorPack().tintColor))
                    }
                }
            }
        }
    }



    fun Context.notiView(lifecycleOwner: LifecycleOwner) = verticalLayout {
        val notiView = this
        visibility = View.GONE

        val notificationLabel = textView {
            textColor = Color.WHITE
            textSize = 18f
            letterSpacing = 0.05f
            setGoogleSans(style = "Medium")
            text = "Notifications"
            gravity = Gravity.CENTER_HORIZONTAL
        }.lparams(wrapContent, wrapContent) {
            bottomMargin = dip(12)
            gravity = Gravity.START
        }
        val notificationTitle = textView {
            textColor = Color.WHITE
            textSize = 14f
            letterSpacing = 0.05f
            setGoogleSans()
            gravity = Gravity.START
        }.lparams(matchParent, wrapContent) {
            bottomMargin = dip(6)
            gravity = Gravity.START
        }
        val notificationContent = textView {
            textColor = Color.WHITE
            textSize = 14f
            letterSpacing = 0.05f
            setGoogleSans()
            gravity = Gravity.START
        }.lparams(matchParent, wrapContent) {
            bottomMargin = dip(12)
            gravity = Gravity.START
        }

        var currentId = 0
        var currentNotificationData: NotificationData? = null

        NotificationManager.notificationStatusLiveData.observeNewOnly(lifecycleOwner, Observer {
            it?.let {
                if (it.first.notification.getNotificationData().appName == "微信") {
                    MainHook.logD(it.toString())
                }
            }
        })

        NotificationManager.notificationStatusLiveData.observeNewOnly(lifecycleOwner, Observer {
            it?.let { (sbn, status) ->
                if (status == "Removed" && currentId == sbn.id) { // cancel noti when it is removed
                    notiView.visibility = View.GONE
                }
                currentId = sbn.id // update current id

                if (status == "Removed") return@let
                if (it.first.notification.getNotificationData().shouldBeSkipped) return@let

                val notification = NotificationManager.notificationMap[sbn.key]?.notification ?: return@let
                currentNotificationData = notification.getNotificationData()

                notification.getNotificationData().let {
                    TransitionManager.beginDelayedTransition(this)
                    notificationTitle.text = "${it.appName}  ${it.title}"
                    notificationContent.text = "${it.content}"
                    notiView.visibility = View.VISIBLE
                }

//                val icon = notification.smallIcon.loadDrawable(context)
//                notificationImage.setImageDrawable(icon)
            }
        })
    }

    override fun onAvoidScreenBurnt(mainView: View, lastTime: Long) {
        val horizontal = 0 // 这个模式避免左右移动
        val vertical = Random().nextInt(700) - 600 // 更大的移动范围 (-600, 100)

        val res = listOf(clock, musicView, notiViewId).map {
            val view = mainView.findViewById<View>(it)
            ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -vertical.toFloat())
                .setDuration(if (lastTime == 0L) /*加入初始位移 避免烧屏*/ 0L else 800L)
        }.toList()

        val set = AnimatorSet()
        set.playTogether(res)
        set.start()

        val musicLrcView = mainView.findViewById<View>(bottomContainer)
        val vertical2 = Random().nextInt(40) - 12  // 更大的移动范围 (-12, 28)
        musicLrcView.animate()
            .translationY(-vertical2.toFloat())
            .setDuration(if (lastTime == 0L) /*加入初始位移 避免烧屏*/ 0L else 800L)
            .start()
    }
}

@SuppressLint("MissingPermission")
fun Context.clockView(lifecycleOwner: LifecycleOwner) = constraintLayout {
    val timeView = textView {
        id = Ids.tv_clock
        textColor = Color.WHITE
        textSize = 36f
        letterSpacing = 0.1f
        setGoogleSans()
        text = SimpleDateFormat(SmaliImports.timeFormat, Locale.ENGLISH).format(Date())
        AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
            text = SimpleDateFormat(SmaliImports.timeFormat, Locale.ENGLISH).format(Date()) + "  " // 玄学空格？
        })
    }.lparams(wrapContent, wrapContent) {
        bottomMargin = dip(10)
        startToStart = PARENT_ID
        topToTop = PARENT_ID
    }

    val dateView = textView {
        textColor = Color.WHITE
        textSize = 16f
        letterSpacing = 0.1f
        setGoogleSans()
        val dateFormat = SimpleDateFormat("MM/dd EE", Locale.ENGLISH)
        text = dateFormat.format(Date())

        AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
            text = dateFormat.format(Date())
        })
    }.lparams(wrapContent, wrapContent) {
        startToStart = PARENT_ID
        topToBottom = timeView.id
    }


    val battery = textView {
        id = Ids.tv_battery
        textColor = Color.WHITE
        setGoogleSans()
        letterSpacing = 0.02f
        textSize = 15f

        AodState.powerState.observe(lifecycleOwner, Observer {
            it?.let {
                var statusText = if (it.plugged) "Charging" else ""
                if (it.fastCharge) statusText = "Quick Charging"
                if (it.charged) statusText = "Charged"
                if (AodState.sleepMode) statusText += " SleepMode"
                text = "${it.level}%  $statusText"
            }
        })
    }.lparams(width = wrapContent, height = wrapContent) {
        bottomToBottom = PARENT_ID
        endToEnd = PARENT_ID
    }

    val headSetStatusView = aodHeadSetView(lifecycleOwner).apply {
        id = Ids.ly_headset_status
        visibility = View.INVISIBLE

        findViewById<View>(Ids.iv_headSet).visibility = View.GONE
        findViewById<TextView>(Ids.tv_headSetStatus).apply {
            gravity = Gravity.END
            textSize = 15f
            maxWidth = dip(200)
        }

    }.lparams(wrapContent, wrapContent) {
        endToEnd = PARENT_ID
        bottomToBottom = timeView.id
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

}

fun Context.musicView(lifecycleOwner: LifecycleOwner) = verticalLayout {

    val musicName = textView {
        textColor = Color.WHITE
        textSize = 16f
        letterSpacing = 0.02f
        setGoogleSans(style = "Medium")
        text = "Dark Side Of The Moon"
        visibility = View.GONE
        gravity = Gravity.START
    }.lparams(matchParent, wrapContent) {
        bottomMargin = dip(8)
        gravity = Gravity.START
    }

    val musicArtist = textView {
        textColor = Color.WHITE
        textSize = 14f
        letterSpacing = 0.05f
        setGoogleSans()
        visibility = View.GONE
        gravity = Gravity.START
    }.lparams(matchParent, wrapContent) {
        bottomMargin = dip(12)
        gravity = Gravity.START
    }

    AodMedia.aodMediaLiveData.observe(lifecycleOwner, Observer {
        if (it == null) {
            musicArtist.visibility = View.GONE
            musicName.visibility = View.GONE
            LrcSync.stopSync()
            return@Observer
        }

        musicArtist.visibility = View.VISIBLE
        musicName.visibility = View.VISIBLE
        if(it.overriddenFullString.isEmpty()) {
            musicName.text = it.name
            musicArtist.text = it.artist
        }else{
            musicName.text = it.getMusicString()
            musicArtist.text = ""
        }
    })
}