package com.retrox.aodmod.hooks

import android.view.View
import android.widget.TextView
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

        val fingerprintDialogViewClassOld = XposedHelpers.findClass("com.oneplus.systemui.biometrics.OpFingerprintDialogView", lpparam.classLoader)
        val fingerprintDialogViewClassNew = XposedHelpers.findClass("com.oneplus.systemui.biometrics.OpFodIconViewController", lpparam.classLoader)

        val methodCheck = try {
            XposedHelpers.findMethodExact(fingerprintDialogViewClassOld, "handleUpdateIconVisibility", Boolean::class.java)
        } catch (e: Throwable) {
            null
        }

        val fingerprintDialogViewClass = if (methodCheck != null) {
            fingerprintDialogViewClassOld
        } else {
            fingerprintDialogViewClassNew
        }

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

        if (methodCheck == null) return // 如果没有找到这个方法，那也不要做此Hook 避免Crash
        // 隐藏指纹的错误提示
        XposedHelpers.findAndHookMethod(fingerprintDialogViewClass, "animateErrorText", TextView::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val view = param.args[0] as? TextView
                if (AodState.DreamState.ACTIVE == AodState.dreamState.value) {
                    view?.text = ""
                }
            }
        })
    }
}