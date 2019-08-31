package com.retrox.aodmod

import android.os.Build
import android.util.Log
import com.retrox.aodmod.BuildConfig.DEBUG
import com.retrox.aodmod.apple.AppleMusicHook
import com.retrox.aodmod.extensions.isOP7Pro
import com.retrox.aodmod.hooks.*
import com.retrox.aodmod.proxy.DreamProxyHook
import com.retrox.aodmod.proxy.OP7DreamProxyHook
import com.retrox.aodmod.proxy.ProxyInitHook
import com.retrox.aodmod.shared.SharedLogger
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
//        SystemServiceHook.initZygote(startupParam)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.d("AODMOD", "package: ${lpparam.packageName}")
//        ProxyInitHook.handleLoadPackage(lpparam)

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            AodLayoutSourceHook.handleLoadPackage(lpparam)
            AodDurationHook.handleLoadPackage(lpparam)
            DisplayStateHook.handleLoadPackage(lpparam)
            AodMainMediaHook.handleLoadPackage(lpparam)
        }

        MediaControl.handleLoadPackage(lpparam)
        AodFingerPrintHook.handleLoadPackage(lpparam) // 理论上支持6T todo 适配 P Q

//        AodAlwaysOnHook.handleLoadPackage(lpparam)
        if (isOP7Pro()) {
            OP7DreamProxyHook.handleLoadPackage(lpparam)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                PermissionHook.handleLoadPackage(lpparam) // Q
            } else {
                SystemServiceHook.handleLoadPackage(lpparam)
            }
        } else {
            DreamProxyHook.handleLoadPackage(lpparam)
        }

        AppleMusicHook.handleLoadPackage(lpparam)

//        DreamLifeCycleHook.handleLoadPackage(lpparam)

    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
//        resparam.res.
//        AodLayoutHook.handleInitPackageResources(resparam)
    }


    private const val LOG_FORMAT = "[OnePlus AOD MOD] %1\$s %2\$s: %3\$s"

    fun logE(msg: String, tag: String = TAG, t: Throwable? = null) {
        XposedBridge.log(String.format(LOG_FORMAT, "[ERROR]", tag, msg))
        t?.let { XposedBridge.log(it) }
        SharedLogger.writeLog(String.format(LOG_FORMAT, "[ERROR]", tag, msg) + t?.message)
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
        val message = String.format(LOG_FORMAT, "[DEBUG]", tag, msg)
        SharedLogger.writeLog(message)
        if (DEBUG) XposedBridge.log(message)
//        Log.d(tag, msg)
    }

}