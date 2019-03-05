package com.retrox.aodmod.proxy

import com.retrox.aodmod.MainHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object DreamLifeCycleHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != MainHook.PACKAGE_AOD) return
        val classLoader = lpparam.classLoader
        var dreamProxy: DreamProxy? = null
        val dozeServiceClass = XposedHelpers.findClass("com.oneplus.doze.DozeService", classLoader)

        XposedHelpers.findAndHookConstructor(dozeServiceClass, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD("Dream Life Cycle: Constructor")
            }
        })


        XposedHelpers.findAndHookMethod(dozeServiceClass, "onCreate", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD("Dream Life Cycle: onCreate")

            }
        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onAttachedToWindow", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD("Dream Life Cycle: onAttachedToWindow")

            }
        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onDreamingStarted", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD("Dream Life Cycle: onDreamingStarted")

            }
        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onDreamingStopped", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD("Dream Life Cycle: onDreamingStopped")

            }
        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onWakingUp", String::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD("Dream Life Cycle: onWakingUp -> ${param.args[0]}")

            }
        })

        XposedHelpers.findAndHookMethod(dozeServiceClass, "onSingleTap", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD("Dream Life Cycle: onSingleTap")
            }
        })


    }

}