package com.retrox.aodmod.proxy

import android.app.AndroidAppHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Process
import android.service.dreams.DreamService
import android.view.View
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.sensor.DozeSensors
import com.retrox.aodmod.shared.SharedContentManager
import com.retrox.aodmod.state.AodState
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object OP7DreamProxyHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return

        MainHook.logD("Hook into System UI")
        val classLoader = lpparam.classLoader
        var dreamProxy: DreamProxy? = null
        val dozeServiceClass = XposedHelpers.findClass("com.android.systemui.doze.DozeService", classLoader)
        val dozeMachineClass = XposedHelpers.findClass("com.android.systemui.doze.DozeMachine", classLoader)


        // 让DozeMachine内容失效 避免不必要的资源消耗


        val killReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Process.killProcess(Process.myPid())
            }
        }

        XposedHelpers.findAndHookConstructor(dozeServiceClass, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                AndroidAppHelper.currentApplication().applicationContext.registerReceiver(
                    killReceiver,
                    IntentFilter("com.retrox.aod.killmyself")
                )
                SharedContentManager.addAodTimes() // 选择构造函数的的Hook点 作为判断 基本上Hook成功就可以上车
            }
        })

        MainHook.logD("DisplayMode: ${XPref.getDisplayMode()}")
        if (XPref.getDisplayMode() == "SYSTEM") {
            SharedContentManager.setWorkMode("系统增强 - 不支持7Pro")
            return
        }
        SharedContentManager.setWorkMode("常亮模式 - 7Pro")


        XposedHelpers.findAndHookConstructor(dozeServiceClass, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (dreamProxy == null) {
                    dreamProxy = DreamProxy(param.thisObject as DreamService)
                } else {
                    XposedHelpers.setObjectField(dreamProxy, "dreamService", param.thisObject)
                    // do the trick 避免重复初始化占内存 我真他妈是个聪明鬼
                }
            }
        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onCreate", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {

                val Dependency = XposedHelpers.findClass("com.android.systemui.Dependency", classLoader)
                val asyncSensorManager =
                    XposedHelpers.findClass("com.android.systemui.util.AsyncSensorManager", classLoader)
                val sensorManager =
                    XposedHelpers.callStaticMethod(Dependency, "get", asyncSensorManager) as SensorManager
                val testSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER, false)
                val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
                MainHook.logD("Sensor Test in SystemUI: $testSensor")
                MainHook.logD("Sensor List in SystemUI: $sensorList")

                dreamProxy?.onCreate()
                param.result = null
            }
        })

//        XposedHelpers.findAndHookMethod(dozeServiceClass, "onAttachedToWindow", object : XC_MethodHook() {
//            override fun beforeHookedMethod(param: MethodHookParam) {
//                dreamProxy?.onAttachedToWindow()
//                param.result = null
//            }
//        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onDreamingStarted", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                dreamProxy?.onDreamingStarted()
                param.result = null
            }
        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onDreamingStopped", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                dreamProxy?.onDreamingStopped()
                param.result = null
            }
        })

//        XposedHelpers.findAndHookMethod(dozeServiceClass, "onWakingUp", String::class.java, object : XC_MethodHook() {
//            override fun beforeHookedMethod(param: MethodHookParam) {
//                dreamProxy?.onWakingUp(param.args[0] as String)
//                param.result = null
//            }
//        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onSingleTap", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                dreamProxy?.onSingleTap()
                param.result = null
            }
        })
    }
}