package com.retrox.aodmod.hooks

import android.view.View
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.state.AodState
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AodFingerPrintHookForQ : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        MainHook.logD("Hook into System UI -> AodFingerPrintHookForQ")

        //  com.oneplus.systemui.biometrics.OpFingerprintDialogView
        // com.android.systemui.fingerprint.FingerprintDialogView
        val fingerprintDialogViewClass = XposedHelpers.findClass("com.oneplus.systemui.biometrics.OpFingerprintDialogView", lpparam.classLoader)

        // 屏蔽息屏界面上的指纹指纹亮光
        // 这个在Q上没啥卵用貌似
        XposedHelpers.findAndHookMethod(fingerprintDialogViewClass, "updateIconVisibility", Boolean::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD("Update Icon Visbility ${param.args}")
                val view = XposedHelpers.getObjectField(param.thisObject, "mIconNormal") as View
                view.visibility = View.INVISIBLE
                view.alpha = 0f
                if (AodState.DreamState.ACTIVE == AodState.dreamState.value) {
                    view.visibility = View.INVISIBLE
                    view.alpha = 0f
                }
                AodState.dreamState.observeForever {
                    if (AodState.DreamState.STOP == AodState.dreamState.value) {
                        view.visibility = View.VISIBLE
                        view.alpha = 1f
                    }
                }
            }
        })

        // todo 整理代码 适配Android Q
        // handleUpdateIconVisibility
        XposedHelpers.findAndHookMethod(fingerprintDialogViewClass, "handleUpdateIconVisibility", Boolean::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
//                MainHook.logD("handleUpdateIconVisibility Called ${param.args}")
                val view = XposedHelpers.getObjectField(param.thisObject, "mIconNormal") as View
                view.visibility = View.INVISIBLE
                view.alpha = 0f
                if (AodState.DreamState.ACTIVE == AodState.dreamState.value) {
                    view.visibility = View.INVISIBLE
                    view.alpha = 0f
                }
                AodState.dreamState.observeForever {
                    if (AodState.DreamState.STOP == AodState.dreamState.value) {
                        view.visibility = View.VISIBLE
                        view.alpha = 1f
                    }
                }
            }
        })
    }
}