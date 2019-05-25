package com.retrox.aodmod.proxy

import android.animation.ObjectAnimator
import android.app.AndroidAppHelper
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.service.dreams.DreamService
import android.view.*
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.sensor.FlipOffSensor
import com.retrox.aodmod.proxy.sensor.LightSensor
import com.retrox.aodmod.proxy.view.Ids
import com.retrox.aodmod.proxy.view.aodMainView
import com.retrox.aodmod.proxy.view.custom.dvd.dvdAodMainView
import com.retrox.aodmod.proxy.view.custom.flat.sumSungAodMainView
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.receiver.ReceiverManager
import com.retrox.aodmod.service.alarm.LocalAlarmManager
import com.retrox.aodmod.service.alarm.LocalChoreManager
import com.retrox.aodmod.service.alarm.proxy.LocalAlarmProxy
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
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        LocalAlarmManager.initService(context)
    }

    override fun onAttachedToWindow() {
        MainHook.logD("DreamProxy -> onAttachedToWindow")
    }

    override fun onDreamingStarted() {
        AodState.dreamState.postValue(AodState.DreamState.ACTIVE)
        MainHook.logD("DreamProxy -> onDreamingStarted")
        lastScreenOnTime = System.currentTimeMillis()

//        val layout = context.sumSungAodMainViewActive(this) as ViewGroup
//        if (XPref.getAodLayoutTheme() == "Flat")

        ThemeManager.loadThemePackFromDisk()

        val layout = when (XPref.getAodLayoutTheme()) {
            "Flat" -> context.sumSungAodMainView(this) as ViewGroup
            "Default" -> context.aodMainView(this) as ViewGroup
            "DVD" -> context.dvdAodMainView(this) as ViewGroup
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
//            context.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        MediaServiceLocal.getActiveSessions()


        /**
         * setScreenOff -> startDozing -> setScreenDoze(delayed)
         * do the magic
         * 修复电话时候无法息屏的问题 @SCREEN_ON_FLAG
         */
        val dozeWakeLock = context.getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AODMOD:ScreenDoze")
        dozeWakeLock.acquire(10000L)
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            setScreenOff()
            XposedHelpers.callMethod(dreamService, "startDozing")
            Handler(Looper.getMainLooper()).postDelayed({
                if (FlipOffSensor.Flip_ON == FlipOffSensor.flipSensorLiveData.value?.suggestState) { // 修复距离传感太近的时候息屏依然亮着的bug
                    setScreenDoze()
                } else {
                    setScreenOff()
                }
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
                vertical = Random().nextInt(350) - 400 // 更大的移动范围
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

            // 检测唤醒状态 关屏不继续唤醒
            val state = XposedHelpers.callMethod(dreamService, "getDozeScreenState")
            if (state == Display.STATE_OFF) { // 如果关屏了
                LocalAlarmProxy.stopTick()
                MainHook.logD("屏幕关闭状态，取消唤醒")
            }
        })

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
        AodState.screenState.value = Display.STATE_DOZE
//        LocalAlarmManager.setUpAlarm()
        LocalAlarmProxy.startTick()
        AodClockTick.tickLiveData.postValue("Tick from Screen ON")

        Handler().postDelayed({
            if (AodState.DreamState.STOP != AodState.dreamState.value) {
                setScreenOff()
            }
        }, 10000L)
    }

    // Screen OFF
    fun setScreenOff() {
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_OFF)
        AodState.screenState.value = Display.STATE_OFF
//        LocalAlarmManager.cancelAlarm()
        LocalAlarmProxy.stopTick()
    }


    override fun onDreamingStopped() {
        try {
//            context.unbindService(serviceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        LocalAlarmManager.cancelAlarm()
        LocalAlarmProxy.stopTick()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        AodState.dreamState.postValue(AodState.DreamState.STOP)
        MainHook.logD("DreamProxy -> onDreamingStopped")
        windowManager.removeViewImmediate(mainView)
    }

    override fun onWakingUp(reason: String) {
        MainHook.logD("DreamProxy -> onWakingUp reason-> $reason")

    }

    override fun onSingleTap() {
        MainHook.logD("DreamProxy -> onSingleTap")
        setScreenDoze()
    }


}