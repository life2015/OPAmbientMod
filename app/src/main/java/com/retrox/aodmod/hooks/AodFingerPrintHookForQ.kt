package com.retrox.aodmod.hooks

import android.os.Looper
import android.view.View
import android.widget.TextView
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.app.util.getObjectFieldOrNull
import com.retrox.aodmod.app.util.runOnLooper
import com.retrox.aodmod.app.util.runOnMainThread
import com.retrox.aodmod.state.AodState
import com.retrox.aodmod.util.ToggleableXC_MethodHook
import com.retrox.aodmod.util.XC_MethodHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AodFingerPrintHookForQ : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        MainHook.logD("Hook into System UI -> AodFingerPrintHookForQ")

        //New class
        val v5 = XposedHelpers.findClassIfExists("com.oneplus.systemui.biometrics.OpFodIconViewController", lpparam.classLoader);
        //Old class
        val v6 = XposedHelpers.findClassIfExists("com.oneplus.systemui.biometrics.OpFingerprintDialogView", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(v6, "updateIconVisibility", Boolean::class.java, ToggleableXC_MethodHook(NewMethodHook()));

        if(v5 != null && XposedHelpers.findMethodExactIfExists(v5, "handleUpdateIconVisibility", Boolean::class.java) != null){
            XposedHelpers.findAndHookMethod(v5, "handleUpdateIconVisibility", Boolean::class.java, ToggleableXC_MethodHook(OldMethodHook()))
        }
        if(v6 != null && XposedHelpers.findMethodExactIfExists(v6, "handleUpdateIconVisibility", Boolean::class.java) != null){
            XposedHelpers.findAndHookMethod(v6, "handleUpdateIconVisibility", Boolean::class.java, ToggleableXC_MethodHook(OldMethodHook()))
        }

        if(v5 != null && XposedHelpers.findMethodExactIfExists(v5, "animateErrorText", TextView::class.java) != null){
            XposedHelpers.findAndHookMethod(v5, "animateErrorText", TextView::class.java, ToggleableXC_MethodHook(ErrorMethodHook()))
        }

        if(v6 != null && XposedHelpers.findMethodExactIfExists(v6, "animateErrorText", TextView::class.java) != null){
            XposedHelpers.findAndHookMethod(v6, "animateErrorText", TextView::class.java, ToggleableXC_MethodHook(ErrorMethodHook()))
        }
    }

    class NewMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
//                MainHook.logD("handleUpdateIconVisibility Called ${param.args}")
            val view = getObjectFieldOrNull(param.thisObject, "mIconNormal") as? View
            val currentLooper = Looper.myLooper()!!
            if (view != null) {
                view.visibility = View.INVISIBLE
                view.alpha = 0f
                if (AodState.DreamState.ACTIVE == AodState.dreamState.value) {
                    view.visibility = View.INVISIBLE
                    view.alpha = 0f
                }
                runOnMainThread {
                    AodState.dreamState.observeForever {
                        runOnLooper(currentLooper) {
                            if (AodState.DreamState.STOP == AodState.dreamState.value) {
                                view.visibility = View.VISIBLE
                                view.alpha = 1f
                            }
                        }
                    }
                }
            }
        }
    }

    class OldMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            MainHook.logD("Update Icon Visbility ${param.args}")
            val view = getObjectFieldOrNull(param.thisObject, "mIconNormal") as? View
            val currentLooper = Looper.myLooper()!!
            if (view != null) {
                view.visibility = View.INVISIBLE
                view.alpha = 0f
                if (AodState.DreamState.ACTIVE == AodState.dreamState.value) {
                    view.visibility = View.INVISIBLE
                    view.alpha = 0f
                }
                runOnMainThread {
                    AodState.dreamState.observeForever {
                        runOnLooper(currentLooper) {
                            if (AodState.DreamState.STOP == AodState.dreamState.value) {
                                view.visibility = View.VISIBLE
                                view.alpha = 1f
                            }
                        }
                    }
                }
            }
        }
    }

    class ErrorMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val view = param.args[0] as? TextView
            if (AodState.DreamState.ACTIVE == AodState.dreamState.value) {
                view?.text = ""
            }
        }
    }
}