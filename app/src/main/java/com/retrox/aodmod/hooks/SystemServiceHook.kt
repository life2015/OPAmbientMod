package com.retrox.aodmod.hooks

import com.retrox.aodmod.util.ToggleableXC_MethodHook
import com.retrox.aodmod.util.XC_MethodHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

object SystemServiceHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        XposedBridge.log("SystemServiceHook Start")

        readLine()

        val deviceKeyHandlerClass = XposedHelpers.findClass("com.android.server.policy.DeviceKeyHandler", lpparam.classLoader)

        XposedHelpers.findAndHookMethod(deviceKeyHandlerClass, "processKeyEvent", ToggleableXC_MethodHook(object : XC_MethodHook() {
            var backUpState = false

            override fun beforeHookedMethod(param: MethodHookParam) {
                val response = readLine()
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
        }))
    }

    // return 0,0:0,0:0,0:0,0:0,0:0,0:0,0 or ""
    private fun readLine() : String {
        var strFallback = ""
        return try {
            val file = File("/proc/touchpanel/coordinate")
            val fin = FileInputStream(file)
            val line =  fin.bufferedReader().readLine()
            strFallback = line
            fin.close()
            XposedBridge.log(line)
            line
        } catch (e: Exception) {
            XposedBridge.log(e)
            strFallback
        }
    }

}