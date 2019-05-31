package com.retrox.aodmod.hooks

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File
import java.io.FileInputStream

object SystemServiceHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        XposedBridge.log("SystemServiceHook Start")

        val file = File("/proc/touchpanel/coordinate")
        val fin = FileInputStream(file)
        val line =  fin.bufferedReader().readLine()
        XposedBridge.log(line)

        val deviceKeyHandlerClass = XposedHelpers.findClass("com.android.server.policy.DeviceKeyHandler", lpparam.classLoader)

        XposedHelpers.findAndHookMethod(deviceKeyHandlerClass, "processKeyEvent", object : XC_MethodHook() {
            var backUpState = false

            override fun beforeHookedMethod(param: MethodHookParam) {
                val fileUtilsClass = XposedHelpers.findClass("com.android.server.policy.FileUtils", lpparam.classLoader)
                val response = XposedHelpers.callStaticMethod(
                    fileUtilsClass,
                    "readOneLine",
                    "/proc/touchpanel/coordinate"
                ) as String
                val code = response.split(",")[0]
                backUpState = XposedHelpers.getBooleanField(param.thisObject, "mSleeping")
                if (code == "15") {
                    XposedHelpers.setBooleanField(param.thisObject, "mSleeping", true)
                }
                XposedBridge.log("Line Content: $response ClassState $deviceKeyHandlerClass")
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                XposedHelpers.setBooleanField(param.thisObject, "mSleeping", backUpState)
            }
        })
    }

}