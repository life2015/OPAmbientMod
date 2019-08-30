package com.retrox.aodmod.hooks

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

object PermissionHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        val classLoader = lpparam.classLoader
        PermissionGranter.initAndroid(classLoader)

    }
}