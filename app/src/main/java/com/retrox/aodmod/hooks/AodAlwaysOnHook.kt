package com.retrox.aodmod.hooks

import android.content.Context
import com.retrox.aodmod.MainHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AodAlwaysOnHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != MainHook.PACKAGE_AOD) return

        val classLoader = lpparam.classLoader

        val dozeUtilClass = XposedHelpers.findClass("com.oneplus.aod.Utils", classLoader)
        val systemPropertiesClass = XposedHelpers.findClass("android.os.SystemProperties", classLoader)
        MainHook.logD("check SystemPropClass: ${systemPropertiesClass.toGenericString()}")

//        XposedHelpers.findAndHookMethod(dozeUtilClass, "isAlwaysOnEnabled", object : XC_MethodHook() {
//            override fun beforeHookedMethod(param: MethodHookParam) {
//                param.result = true
//                MainHook.logD("Try Hook Method: isAlwaysOnEnabled")
//            }
//        })
//
//        XposedHelpers.findAndHookMethod(dozeUtilClass, "updateAlwaysOnState", Context::class.java, Int::class.java, object : XC_MethodHook() {
//            override fun beforeHookedMethod(param: MethodHookParam) {
//
//                XposedHelpers.setStaticBooleanField(dozeUtilClass, "mIsAlwaysOnModeEnabled", true)
//                XposedHelpers.callStaticMethod(systemPropertiesClass, "set","sys.aod.disable", "0")
//                param.result = null
//                MainHook.logD("Try Hook Method: updateAlwaysOnState")
//
//            }
//        })

        val aodUpdateMonitorClass = XposedHelpers.findClass("com.oneplus.aod.AodUpdateMonitor", classLoader)
//        val unHook = XposedHelpers.findAndHookMethod(aodUpdateMonitorClass, "isAlwaysOnFired", object : XC_MethodHook() {
//            override fun beforeHookedMethod(param: MethodHookParam) {
//                param.result = true
//                MainHook.logD("Hook Method: AodUpdateMonitor.isAlwaysOnFired")
//            }
//        })

        val aodDisplayManagerClass = XposedHelpers.findClass("com.oneplus.aod.DisplayViewManager", classLoader)
        XposedHelpers.findAndHookMethod(aodDisplayManagerClass, "resetViewState", object : XC_MethodHook() {
            private var backupValue = false
            private var aodUpdateMonitor: Any? = null
            override fun beforeHookedMethod(param: MethodHookParam) {
                val context = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                aodUpdateMonitor = XposedHelpers.callStaticMethod(aodUpdateMonitorClass, "getInstance", context)

                backupValue = XposedHelpers.getBooleanField(aodUpdateMonitor, "mAlwaysOnFired")
                XposedHelpers.setBooleanField(aodUpdateMonitor, "mAlwaysOnFired", true)

                MainHook.logD("Hook Method AOP: before -> ${aodUpdateMonitor.toString()} backupValue: $backupValue")
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                aodUpdateMonitor?.let {
                    XposedHelpers.setBooleanField(it, "mAlwaysOnFired", backupValue)
                    MainHook.logD("Hook Method AOP: after -> ${aodUpdateMonitor.toString()} backupValue: $backupValue")
                }
            }
        })

        val dozeServiceClass = XposedHelpers.findClass("com.oneplus.doze.DozeService", classLoader)
        XposedHelpers.findAndHookMethod(dozeServiceClass, "turnDisplayOff", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.result = null
            }
        })
    }
}