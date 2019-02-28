package com.retrox.aodmod

import android.util.Log
import com.retrox.aodmod.BuildConfig.DEBUG
import com.retrox.aodmod.hooks.*
import com.retrox.aodmod.proxy.DreamProxyHook
import com.retrox.aodmod.proxy.ProxyInitHook
import com.retrox.aodmod.receiver.ReceiverManager
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

object MainHook : IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    const val PACKAGE_ANDROID = "android"
    const val PACKAGE_SYSTEMUI = "com.android.systemui"
    const val PACKAGE_SETTINGS = "com.android.settings"
    const val PACKAGE_AOD = "com.oneplus.aod"
    const val TAG = "AODMOD"
    const val PACKAGE_OWN = "com.retrox.aodmod"


    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
//        startupParam.modulePath.
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.d("AODMOD", "package: ${lpparam.packageName}")
        ProxyInitHook.handleLoadPackage(lpparam)
        AodLayoutSourceHook.handleLoadPackage(lpparam)
        AodDurationHook.handleLoadPackage(lpparam)
        DisplayStateHook.handleLoadPackage(lpparam)
        MediaControl.handleLoadPackage(lpparam)
        AodMainMediaHook.handleLoadPackage(lpparam)
//        AodAlwaysOnHook.handleLoadPackage(lpparam)
        DreamProxyHook.handleLoadPackage(lpparam)

    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
//        resparam.res.
//        AodLayoutHook.handleInitPackageResources(resparam)
    }


    private const val LOG_FORMAT = "[OnePlus AOD MOD] %1\$s %2\$s: %3\$s"

    fun logE(msg: String, tag: String = TAG, t: Throwable? = null) {
        XposedBridge.log(String.format(LOG_FORMAT, "[ERROR]", tag, msg))
        t?.let { XposedBridge.log(it) }
//        Log.e(tag, msg, t)
    }

    fun logW(msg: String, tag: String = TAG) {
        XposedBridge.log(String.format(LOG_FORMAT, "[WARNING]", tag, msg))
//        Log.w(tag, msg)

    }

    fun logI(msg: String, tag: String = TAG) {
        XposedBridge.log(String.format(LOG_FORMAT, "[INFO]", tag, msg))
//        Log.i(tag, msg)

    }

    fun logD(msg: String, tag: String = TAG) {
        if (DEBUG) XposedBridge.log(String.format(LOG_FORMAT, "[DEBUG]", tag, msg))
//        Log.d(tag, msg)
    }

}