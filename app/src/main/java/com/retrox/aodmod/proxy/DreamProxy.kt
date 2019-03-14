package com.retrox.aodmod.proxy

import android.animation.ObjectAnimator
import android.app.AndroidAppHelper
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.os.Handler
import android.service.dreams.DreamService
import android.support.constraint.ConstraintLayout
import android.transition.TransitionManager
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.sensor.FlipOffSensor
import com.retrox.aodmod.proxy.sensor.LightSensor
import com.retrox.aodmod.proxy.view.Ids
import com.retrox.aodmod.proxy.view.aodMainView
import com.retrox.aodmod.receiver.ReceiverManager
import com.retrox.aodmod.service.notification.NotificationCollectorService
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.state.AodState
import de.robv.android.xposed.XposedHelpers
import android.arch.lifecycle.Observer
import android.os.Looper
import com.retrox.aodmod.extensions.UserInfoUtils
import java.util.*

class DreamProxy(override val dreamService: DreamService) : DreamProxyInterface, LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    var lastScreenOnTime = 0L

    val context: Context = dreamService
    val windowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager;
    }
    val lazyInitBlock by lazy {
        ReceiverManager.register()
        NotificationCollectorService(AndroidAppHelper.currentApplication().applicationContext)
        "OK"
    }
    var mainView: View? = null
    override fun onCreate() {
        XposedHelpers.callMethod(dreamService, "setWindowless", true)
        MainHook.logD("DreamProxy -> OnCreate")
        lazyInitBlock.length
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onAttachedToWindow() {
        MainHook.logD("DreamProxy -> onAttachedToWindow")
    }

    override fun onDreamingStarted() {
        AodState.dreamState.postValue(AodState.DreamState.ACTIVE)
        MainHook.logD("DreamProxy -> onDreamingStarted")
        lastScreenOnTime = System.currentTimeMillis()

        val layout = context.aodMainView(this) as ViewGroup
        val aodMainLayout = layout.findViewById<ConstraintLayout>(Ids.ly_main)
        mainView = layout
        windowManager.addView(mainView, getAodViewLayoutParams())

        aodMainLayout.visibility = View.INVISIBLE
        setScreenDoze()

        Handler(Looper.getMainLooper()).post {
            XposedHelpers.callMethod(dreamService, "startDozing")
            aodMainLayout.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(aodMainLayout, View.ALPHA, 0f, 1f).apply {
                duration = 800L
            }.start()
        }

        // 翻转 口袋
        if (XPref.getFilpOffMode()) {
            FlipOffSensor.flipSensorLiveData.observe(this, Observer {
                it?.let {
                    MainHook.logD("Flip State: $it")
                    AodClockTick.tickLiveData.postValue("Tick from Light Sensor")

                    when (it.suggestState) {
                        FlipOffSensor.Flip_ON -> setScreenDoze()
                        FlipOffSensor.Flip_OFF -> setScreenOff()
                    }
                }
            })
        }

        if (XPref.getAutoBrightnessEnabled()) {
            LightSensor.lightSensorLiveData.observe(this, Observer {
                it?.let { (suggestAlpha, _) ->
                    MainHook.logD("Light Sensor Alpha: $suggestAlpha")
                    AodClockTick.tickLiveData.postValue("Tick from Light Sensor")
                    aodMainLayout.alpha = suggestAlpha
                }
            })
        }

        var lastScreenBurnUpdate = System.currentTimeMillis()
        // 防烧屏
        AodClockTick.tickLiveData.observe(this, Observer {

            if (System.currentTimeMillis() - lastScreenBurnUpdate < 1000L * 30L) return@Observer // 避免太能挪动了...

            lastScreenBurnUpdate = System.currentTimeMillis()
            val vertical = Random().nextInt(50)
            val horizontal = Random().nextInt(20) - 10

            TransitionManager.beginDelayedTransition(layout)
            aodMainLayout.apply {
                translationX = horizontal.toFloat()
                translationY = -vertical.toFloat()
            }

            if ((System.currentTimeMillis() - lastScreenOnTime) > 60 * 60 * 1000L && XPref.getAutoScreenOffAfterHourEnabled()) {
                setScreenOff() // 一小时自动息屏
            }

            MainHook.logD("防烧屏: x offset:$horizontal y offset:$vertical")
        })

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

//        XposedHelpers.callMethod(dreamService, "setInteractive", true)

        if (AodState.sleepMode) {
            Handler().postDelayed({
                if (AodState.DreamState.STOP != AodState.dreamState.value) {
                    setScreenOff()
                }
            }, 10000L)
        }
    }


    fun setScreenDoze() {
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_DOZE)
    }

    fun setScreenOff() {
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_OFF)
    }


    override fun onDreamingStopped() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        AodState.dreamState.postValue(AodState.DreamState.STOP)
        MainHook.logD("DreamProxy -> onDreamingStopped")
        windowManager.removeViewImmediate(mainView)
    }

    override fun onWakingUp(reason: String) {
        MainHook.logD("DreamProxy -> onWakingUp reason-> $reason")
        if (reason == "android.server.wm:TURN_ON") {
            setScreenOff()
        }

    }

    override fun onSingleTap() {
        MainHook.logD("DreamProxy -> onSingleTap")

    }


    private fun getAodViewLayoutParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        params.type = 2303
        params.layoutInDisplayCutoutMode = 1
        params.flags = 1280
        params.format = -2
        params.width = -1
        params.height = -1
        params.gravity = 17
        params.screenOrientation = 1
        params.title = "OPAod"
        params.softInputMode = 3
        return params
    }

}