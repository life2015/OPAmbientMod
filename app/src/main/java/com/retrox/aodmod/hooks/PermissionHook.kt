package com.retrox.aodmod.hooks

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

object PermissionHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "android") return
        val classLoader = lpparam.classLoader
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            PermissionGranterQ.initAndroid(classLoader)
        }

    }
}