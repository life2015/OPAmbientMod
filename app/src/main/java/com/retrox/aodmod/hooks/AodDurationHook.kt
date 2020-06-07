package com.retrox.aodmod.hooks

import com.retrox.aodmod.MainHook
import com.retrox.aodmod.state.AodState
import com.retrox.aodmod.util.ToggleableXC_MethodHook
import com.retrox.aodmod.util.XC_MethodHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AodDurationHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != MainHook.PACKAGE_AOD) return

        val classLoader = lpparam.classLoader

        val dozeParametersClass = XposedHelpers.findClass("com.oneplus.doze.DozeParameters", classLoader)
        XposedHelpers.findAndHookMethod(dozeParametersClass, "getPulseVisibleDuration", Int::class.java, ToggleableXC_MethodHook(object : XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)

                val arg = param.args[0] as Int
                val result = when(arg) {
                    3 -> 10000
                    1 -> if (AodState.isImportantMessage) 20000 else 10000
                    else -> 5000
                }

                AodState.isImportantMessage = false
                param.result = result

                MainHook.logD("AOD Duration mod: type:$arg dur:$result")
            }
        }))
    }
}