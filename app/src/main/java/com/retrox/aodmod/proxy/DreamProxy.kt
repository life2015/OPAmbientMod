package com.retrox.aodmod.proxy

import android.animation.ObjectAnimator
import android.app.AndroidAppHelper
import android.app.Service
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.service.dreams.DreamService
import android.support.constraint.ConstraintLayout
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
import com.retrox.aodmod.proxy.view.custom.dvd.dvdAodMainView
import com.retrox.aodmod.proxy.view.custom.flat.sumSungAodMainView
import com.retrox.aodmod.receiver.ReceiverManager
import com.retrox.aodmod.service.alarm.LocalAlarmManager
import com.retrox.aodmod.service.notification.NotificationCollectorService
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.state.AodState
import de.robv.android.xposed.XposedHelpers
import java.util.*

class DreamProxy(override val dreamService: DreamService) : DreamProxyInterface, LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    var lastScreenOnTime = 0L

    val context: Context = dreamService
    val windowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager;

    val lazyInitBlock by lazy {
        ReceiverManager.register()
        NotificationCollectorService(AndroidAppHelper.currentApplication().applicationContext)
        "OK"
    }
    var mainView: View? = null

    val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            MainHook.logD("Service Disconnected ${name.toString()}")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            MainHook.logD("Service Connected ${name.toString()}")
        }

    }

    override fun onCreate() {
        XposedHelpers.callMethod(dreamService, "setWindowless", true)
        MainHook.logD("DreamProxy -> OnCreate")
        lazyInitBlock.length
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        LocalAlarmManager.initService(context)
    }

    override fun onAttachedToWindow() {
        MainHook.logD("DreamProxy -> onAttachedToWindow")
    }

    override fun onDreamingStarted() {
        AodState.dreamState.postValue(AodState.DreamState.ACTIVE)
        MainHook.logD("DreamProxy -> onDreamingStarted")
        lastScreenOnTime = System.currentTimeMillis()

//        val layout = context.dvdAodMainView(this) as ViewGroup
//        if (XPref.getAodLayoutTheme() == "Flat")

        val layout = when (XPref.getAodLayoutTheme()) {
            "Flat" -> context.sumSungAodMainView(this) as ViewGroup
            "Default" -> context.aodMainView(this) as ViewGroup
            else -> context.aodMainView(this) as ViewGroup
        }

        val aodMainLayout = layout.findViewById<View>(Ids.ly_main)
        mainView = layout
        windowManager.addView(mainView, LayoutParamHelper.getAodViewLayoutParams())

        aodMainLayout.visibility = View.INVISIBLE
//        setScreenDoze()
        setScreenOff()

        val intent = Intent().apply {
            action = "com.retrox.aodplugin.plugin.service"
            setPackage("com.retrox.aodmod.plugin")
        }
        try {
            context.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

/*          音乐架构重构尝试
        val mediaSessionManager = context.getSystemService(MediaSessionManager::class.java)
        mediaSessionManager.getActiveSessions(null).forEach {
            MainHook.logD(it.packageName + "2222")
            it.registerCallback()
        }
        mediaSessionManager.addOnActiveSessionsChangedListener(object : MediaSessionManager.OnActiveSessionsChangedListener {
            override fun onActiveSessionsChanged(controllers: MutableList<MediaController>?) {
                MainHook.logD("MediaSession Changed")
            }
        }, null, object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                MainHook.logD("MediaSession handle Message : $msg")
            }
        })*/

        /**
         * setScreenOff -> startDozing -> setScreenDoze(delayed)
         * do the magic
         * 修复电话时候无法息屏的问题 @SCREEN_ON_FLAG
         */
        val dozeWakeLock = context.getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AODMOD:ScreenDoze")
        dozeWakeLock.acquire(10000L)
        Handler(Looper.getMainLooper()).post {
            setScreenOff()
            XposedHelpers.callMethod(dreamService, "startDozing")
            Handler(Looper.getMainLooper()).postDelayed({
                setScreenDoze()
                aodMainLayout.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(aodMainLayout, View.ALPHA, 0f, 1f).apply {
                    duration = 800L
                }.start()
                dozeWakeLock.release()
            }, 400L)
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
//                    AodClockTick.tickLiveData.postValue("Tick from Light Sensor")
                    aodMainLayout.alpha = suggestAlpha

//                    val brightness = (suggestAlpha * 255).roundToInt()
//                    MainHook.logD("Light Sensor Brightness : $brightness")
//                    XposedHelpers.callMethod(dreamService, "setDozeScreenBrightness", brightness)
                }
            })
        }

        var lastScreenBurnUpdate = System.currentTimeMillis()
        // 防烧屏
        AodClockTick.tickLiveData.observe(this, Observer {

            if (System.currentTimeMillis() - lastScreenBurnUpdate < 1000L * 30L) return@Observer // 避免太能挪动了...

            lastScreenBurnUpdate = System.currentTimeMillis()
            var vertical = Random().nextInt(50)
            var horizontal = Random().nextInt(20) - 10

            if (XPref.getAodLayoutTheme() == "Flat") { // Flat Mode
                vertical = Random().nextInt(20) - 250
                horizontal = Random().nextInt(100) - 20
            }

//            TransitionManager.beginDelayedTransition(layout)
//            aodMainLayout.apply {
//                translationX = horizontal.toFloat()
//                translationY = -vertical.toFloat()
//            }
            aodMainLayout.animate()
                .translationX(horizontal.toFloat())
                .translationY(-vertical.toFloat())
                .setDuration(800L)
                .start()

            if ((System.currentTimeMillis() - lastScreenOnTime) > 30 * 60 * 1000L && XPref.getAutoScreenOffAfterHourEnabled()) {
                setScreenOff() // 半小时自动息屏
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


    // Screen ON
    fun setScreenDoze() {
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_DOZE)
        LocalAlarmManager.setUpAlarm()
    }

    // Screen OFF
    fun setScreenOff() {
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_OFF)
        LocalAlarmManager.cancelAlarm()
    }


    override fun onDreamingStopped() {
        try {
            context.unbindService(serviceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        LocalAlarmManager.cancelAlarm()
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