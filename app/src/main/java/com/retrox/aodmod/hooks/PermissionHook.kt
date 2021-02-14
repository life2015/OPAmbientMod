package com.retrox.aodmod.hooks

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

object PermissionHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        val classLoader = lpparam.classLoader
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> PermissionGranterR.initAndroid(classLoader)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> PermissionGranterQ.initAndroid(classLoader)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> PermissionGranterP.initAndroid(classLoader)
        }
    }
}