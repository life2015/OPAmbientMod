package com.retrox.aodmod.hooks

import com.retrox.aodmod.MainHook
import com.retrox.aodmod.state.AodState
import com.retrox.aodmod.util.ToggleableXC_MethodHook
import com.retrox.aodmod.util.XC_MethodHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Hook DisplayViewManager State
 */
object DisplayStateHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != MainHook.PACKAGE_AOD) return

        val classLoader = lpparam.classLoader
        val displayViewManagerClass = XposedHelpers.findClass("com.oneplus.aod.DisplayViewManager", classLoader)

        val aodUpdateMonitorClass = XposedHelpers.findClass("com.oneplus.aod.AodUpdateMonitor", classLoader)
//        XposedHelpers.findAndHookMethod(aodUpdateMonitorClass, "fireNewNotifications", object : XC_MethodHook() {
//            override fun beforeHookedMethod(param: MethodHookParam) {
//                val displayViewManager = XposedHelpers.getObjectField(param.thisObject, "mDisplayViewManager")
//                val oldState = XposedHelpers.getIntField(displayViewManager, "mState")
//                AodState.setDisplayState(oldState)
//                AodState.setDisplayState(2)
//
//            }
//        })

        XposedHelpers.findAndHookMethod(displayViewManagerClass, "updateView", ToggleableXC_MethodHook(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val state = XposedHelpers.getIntField(param.thisObject, "mState")
//                AodState.setDisplayState(oldState)
//                val newState = param.args[0] as Int
                AodState.setDisplayState(state)

                MainHook.logD("DisplayStateHook: old State to new: ${AodState.getDisplayState()}")
                // 检查息屏提示的状态 息屏提示动画用 避免重复动画
            }
        }))

    }

}