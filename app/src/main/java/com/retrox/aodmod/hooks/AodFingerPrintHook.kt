package com.retrox.aodmod.hooks

import android.view.View
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.state.AodState
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AodFingerPrintHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        MainHook.logD("Hook into System UI -> AodFingerPrintHook")

        val fingerprintDialogViewClass = XposedHelpers.findClass("com.android.systemui.fingerprint.FingerprintDialogView", lpparam.classLoader)

        // 屏蔽息屏界面上的指纹指纹亮光
        XposedHelpers.findAndHookMethod(fingerprintDialogViewClass, "updateIconVisibility", Boolean::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val view = XposedHelpers.getObjectField(param.thisObject, "mIconNormal") as View
                if (AodState.DreamState.ACTIVE == AodState.dreamState.value) {
                    view.visibility = View.INVISIBLE
                }
                AodState.dreamState.observeForever {
                    if (AodState.DreamState.STOP == AodState.dreamState.value) {
                        view.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}