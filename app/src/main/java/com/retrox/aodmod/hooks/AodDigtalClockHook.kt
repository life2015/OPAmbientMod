package com.retrox.aodmod.hooks

import com.retrox.aodmod.MainHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AodDigtalClockHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != MainHook.PACKAGE_AOD) return

        val classLoader = lpparam.classLoader
        val typefaceClass = XposedHelpers.findClass("android.graphics.Typeface", classLoader)
        XposedHelpers.findAndHookMethod(typefaceClass, "")
    }
}