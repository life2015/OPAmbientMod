package com.retrox.aodmod.proxy

import android.animation.Animator
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
import android.graphics.Color
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.service.dreams.DreamService
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.extensions.children
import com.retrox.aodmod.extensions.isOP7Pro
import com.retrox.aodmod.pref.SystemPref
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.sensor.DozeSensors
import com.retrox.aodmod.proxy.sensor.FlipOffSensor
import com.retrox.aodmod.proxy.sensor.LightSensor
import com.retrox.aodmod.proxy.view.AodDefaultDream
import com.retrox.aodmod.proxy.view.custom.dvd.AodDVDDream
import com.retrox.aodmod.proxy.view.custom.flat.AodFlatDream
import com.retrox.aodmod.proxy.view.custom.music.ComplexMusicDream
import com.retrox.aodmod.proxy.view.custom.music.PureMusicDream
import com.retrox.aodmod.proxy.view.custom.word.WordDream
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.receiver.ReceiverManager
import com.retrox.aodmod.service.alarm.LocalAlarmManager
import com.retrox.aodmod.service.alarm.LocalAlarmTimeOutTicker
import com.retrox.aodmod.service.alarm.proxy.LocalAlarmProxy
import com.retrox.aodmod.service.notification.NotificationCollectorService
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.state.AodState
import de.robv.android.xposed.XposedHelpers
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import java.net.URL


class DreamProxy(override val dreamService: DreamService) : DreamProxyInterface, LifecycleOwner, DreamProxyController {

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
    val handler = Handler(Looper.getMainLooper())

    var mainView: View? = null

    var dreamView: DreamView = AodFlatDream(this)

    val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            MainHook.logD("Service Disconnected ${name.toString()}")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            MainHook.logD("Service Connected ${name.toString()}")
        }

    }

    val screenOffRunnable = Runnable {
        setScreenOff("AutoOffTimeOffset")
    }


    override fun screenPulse(time: Long, block: () -> Unit) {
        val currentStateOFF = getScreenState() == Display.STATE_OFF
        handler.removeCallbacks(screenOffRunnable)
        setScreenDoze("screenPulse")
        if (currentStateOFF && time != -1L) {
            handler.postDelayed(screenOffRunnable, time)
        }
        block()
    }

    private fun screenAutoOff() {
        handler.removeCallbacks(screenOffRunnable)
        handler.postDelayed(screenOffRunnable, 15 * 1000L)
    }


    override fun onCreate() {
        XposedHelpers.callMethod(dreamService, "setWindowless", true)
        MainHook.logD("DreamProxy -> OnCreate")
        lazyInitBlock.length
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        LocalAlarmManager.initService(context)
        LocalAlarmTimeOutTicker.initService(context)
        dreamView.onCreate()
    }

    override fun onAttachedToWindow() {
        MainHook.logD("DreamProxy -> onAttachedToWindow")
    }

    override fun onDreamingStarted() {
        AodState.dreamState.postValue(AodState.DreamState.ACTIVE)
        MainHook.logD("DreamProxy -> onDreamingStarted")

        ThemeManager.loadThemePackFromDisk()

        val dream = when (XPref.getAodLayoutTheme()) {
            "Flat" -> AodFlatDream(this)
            "Default" -> AodDefaultDream(this)
            "DVD" -> AodDVDDream(this)
            "PureMusic" -> PureMusicDream(this)
            "FlatMusic" -> ComplexMusicDream(this)
            "Word" -> WordDream(this)
            else -> AodDefaultDream(this)
        }

        dreamView = dream

        val viewInternal = dreamView.onCreateView() // 真正显示的View
        val realLayout = context.frameLayout {
            backgroundColor = Color.BLACK
            val v = viewInternal.lparams(matchParent, matchParent)
            addView(v)
        } // 加入黑色背景的 总View
        mainView = realLayout

        val aodMainLayout = viewInternal
        windowManager.addView(realLayout, LayoutParamHelper.getAodViewLayoutParams())

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

//        doAsync {
//            val text = URL("https://www.baidu.com").readText()
//            MainHook.logD("网络请求成功了: $text")
//        }

        /**
         * setScreenOff -> startDozing -> setScreenDoze(delayed)
         * do the magic
         * 修复电话时候无法息屏的问题 @SCREEN_ON_FLAG
         */
        val dozeWakeLock = context.getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AODMOD:ScreenDoze")
        dozeWakeLock.acquire(10000L)
        handler.post {
            setScreenOff()
            XposedHelpers.callMethod(dreamService, "startDozing")
            handler.postDelayed({
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

        if (XPref.getAutoBrightnessEnabled() || isOP7Pro()) {  // 一加7Pro的距离传感器依赖于光感的响应，所以开着就可以用口袋了
            LightSensor.lightSensorLiveData.observe(this, Observer {
                it?.let { (suggestAlpha, _) ->
                    MainHook.logD("Light Sensor Alpha: $suggestAlpha")
                    if (XPref.getAutoBrightnessEnabled()) {
                        aodMainLayout.alpha = suggestAlpha
                    }

//                    val brightness = (suggestAlpha * 255).roundToInt()
//                    MainHook.logD("Light Sensor Brightness : $brightness")
//                    XposedHelpers.callMethod(dreamService, "setDozeScreenBrightness", brightness)
                }
            })
        }

        var lastScreenBurnUpdate = System.currentTimeMillis()
        MainHook.logD("防烧屏: 初始偏移")
        dreamView.onAvoidScreenBurnt(aodMainLayout, 0L)

        // 防烧屏
        AodClockTick.tickLiveData.observe(this, Observer {

            if (System.currentTimeMillis() - lastScreenBurnUpdate < 1000L * 30L) return@Observer // 避免太能挪动了...

            MainHook.logD("防烧屏: 触发")

            dreamView.onAvoidScreenBurnt(aodMainLayout, lastScreenBurnUpdate)

            lastScreenBurnUpdate = System.currentTimeMillis()

            // todo 抽离自动息屏逻辑
            if ((System.currentTimeMillis() - lastScreenOnTime) > 10 * 60 * 1000L && XPref.getAutoScreenOffAfterHourEnabled() && true != AodState.powerState.value?.plugged) {
                setScreenOff() // 半小时自动息屏
            }


            // 检测唤醒状态 关屏不继续唤醒
            val state = getScreenState()
            if (state == Display.STATE_OFF) { // 如果关屏了
                LocalAlarmProxy.stopTick()
                MainHook.logD("屏幕关闭状态，取消唤醒")
            }
        })

//        XposedHelpers.callMethod(dreamService, "setInteractive", true)


        if (XPref.getAodPickCheckEnabled()) {
            DozeSensors.getSensorWakeLiveData().observe(this, Observer {
                if (it == null) return@Observer
                when (it) {
                    DozeSensors.DozeSensorMessage.PICK_UP -> setScreenDoze()
                    DozeSensors.DozeSensorMessage.MOTION_UP -> setScreenDoze()
                    DozeSensors.DozeSensorMessage.PICK_DROP -> {
                        if (true != AodState.powerState.value?.charging) {
                            setScreenOff()
                        } else {
                            MainHook.logD("充电状态 禁用抬手灭屏")
                        }
                    }
                }
            })
        }

        NotificationManager.notificationStatusLiveData.observeNewOnly(this, Observer {
            it?.let { (_, status) ->
                if (status == "Removed") return@let
                if (it.first.notification.getNotificationData().isOnGoing) return@let

                if (XPref.getAodAutoCloseBySeconds()) {
                    screenPulse {}
                } else {
                    screenPulse(-1L) {}
                }
            }
        })

    }

    override fun getScreenState() =
        XposedHelpers.callMethod(dreamService, "getDozeScreenState") as Int

    override fun setScreenDoze(reason: String) {
        if (SystemPref.getNightModeStat()) {
            MainHook.logD("NightMode，不要亮屏")
            return
        }
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_DOZE)
        lastScreenOnTime = System.currentTimeMillis()

        AodState.screenState.value = Display.STATE_DOZE
//        LocalAlarmManager.setUpAlarm()
        LocalAlarmProxy.startTick()
        AodClockTick.tickLiveData.postValue("Tick from Screen ON")

        if (XPref.getAodAutoCloseBySeconds()) {
            screenAutoOff()
        }

        dreamView.onScreenTurnOn(reason)
    }

    override fun setScreenOff(reason: String) {
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_OFF)
        AodState.screenState.value = Display.STATE_OFF
//        LocalAlarmManager.cancelAlarm()
        LocalAlarmProxy.stopTick()

        dreamView.onScreenTurnOff(reason)
    }


    // 目前还是不好用 会出现很多问题
    override fun setScreenActive(reason: String) {
        if (getScreenState() != Display.STATE_DOZE) {
            setScreenDoze()
        }
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_ON)
        AodState.screenState.value = Display.STATE_ON
        dreamView.onScreenActive(reason)
    }

    fun setInteractive(interative: Boolean) {
        XposedHelpers.callMethod(dreamService, "setInteractive", interative)
    }

    override fun onDreamingStopped() {
        try {
//            context.unbindService(serviceConnection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        LocalAlarmManager.cancelAlarm()
        MainHook.logD("DreamProxy -> onDreamingStopped")

        val contentview = mainView
        contentview?.apply {
            val viewChild = (this as? ViewGroup)?.getChildAt(0)
//            不同阶动画，白色文字快速透明，背景慢速透明
//            这部分注释掉的代码是因为这种渐变动画在直接指纹解锁的时候会出现奇怪的视觉效果 所以先去掉了
/*             viewChild?.apply {
                visibility = View.INVISIBLE
            }
           animate().alpha(0f).setDuration(400L)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {

                    }
                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}
                }).start()*/

            LocalAlarmProxy.stopTick()
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            AodState.dreamState.postValue(AodState.DreamState.STOP)
            dreamView.onDestroyView()
            windowManager.removeViewImmediate(mainView)
        }

    }

    override fun onWakingUp(reason: String) {
        MainHook.logD("DreamProxy -> onWakingUp reason-> $reason")

    }

    override fun onSingleTap() {
        MainHook.logD("DreamProxy -> onSingleTap")
        AodState.singleTapLiveEvent.postValue("onSingleTap")
        dreamView.onSingleTap()
    }


}