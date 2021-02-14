package com.retrox.aodmod.proxy

import android.app.AndroidAppHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.dreams.DreamService
import com.retrox.aodmod.MainHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import android.os.Process
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.shared.SharedContentManager
import com.retrox.aodmod.util.ToggleableXC_MethodHook
import com.retrox.aodmod.util.XC_MethodHook
import java.lang.ref.WeakReference

object DreamProxyHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != MainHook.PACKAGE_AOD) return
        val classLoader = lpparam.classLoader

        XposedHelpers.findAndHookMethod("com.android.systemui.SystemUIApplication", classLoader, "onCreate", object: XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                //Setup the context for XPref
                XPref.context = WeakReference(param.thisObject as Context)
            }
        })

        var dreamProxy: DreamProxy? = null
        val dozeServiceClass = XposedHelpers.findClass("com.oneplus.doze.DozeService", classLoader)

        val killReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Process.killProcess(Process.myPid())
            }
        }

        XposedHelpers.findAndHookConstructor(dozeServiceClass, ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                AndroidAppHelper.currentApplication().applicationContext.registerReceiver(killReceiver, IntentFilter("com.retrox.aod.killmyself"))
                SharedContentManager.addAodTimes() // 选择构造函数的的Hook点 作为判断 基本上Hook成功就可以上车
            }
        }))

        MainHook.logD("DisplayMode: ${XPref.getDisplayMode()}")
        if (XPref.getDisplayMode() == "SYSTEM") {
            SharedContentManager.setWorkMode("系统增强")
            return
        }
        SharedContentManager.setWorkMode("常亮模式")


        XposedHelpers.findAndHookConstructor(dozeServiceClass, ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (dreamProxy == null) {
                    dreamProxy = DreamProxy(param.thisObject as DreamService)
                } else {
                    XposedHelpers.setObjectField(dreamProxy, "dreamService", param.thisObject)
                    // do the trick 避免重复初始化占内存 我真他妈是个聪明鬼
                }
            }
        }))

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onCreate", ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                //XPref.context = WeakReference(param.thisObject as Context)
                dreamProxy?.onCreate()
                param.result = null
            }
        }))

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onAttachedToWindow", ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                dreamProxy?.onAttachedToWindow()
                param.result = null
            }
        }))

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onDreamingStarted", ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                dreamProxy?.onDreamingStarted()
                param.result = null
            }
        }))

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onDreamingStopped", ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                dreamProxy?.onDreamingStopped()
                param.result = null
            }
        }))

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onWakingUp", String::class.java, ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                dreamProxy?.onWakingUp(param.args[0] as String)
                param.result = null
            }
        }))

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onSingleTap", ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                dreamProxy?.onSingleTap()
                param.result = null
            }
        }))
    }
}