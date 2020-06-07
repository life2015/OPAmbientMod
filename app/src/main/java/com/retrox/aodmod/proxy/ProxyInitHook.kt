package com.retrox.aodmod.proxy

import android.os.Vibrator
import android.util.Log
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.util.ToggleableXC_MethodHook
import com.retrox.aodmod.util.XC_MethodHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.InvocationTargetException

object ProxyInitHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.tencent.tim") return

        val vibratorClass = XposedHelpers.findClass("android.os.Vibrator", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(
            vibratorClass,
            "vibrate",
            "android.os.VibrationEffect",
            "android.media.AudioAttributes",
            ToggleableXC_MethodHook(object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val vibrator = param.thisObject as Vibrator
                    vibrator.setVibratorEffect(10205)
                    MainHook.logD("Tim trigger Vibrate $param")
                }
            })
        )

    }

    fun Vibrator.setVibratorEffect(senceId: Int): Long {
        val TAG = "VibrationHelper2"
        val setVibrationEffect = try {
            Vibrator::class.java.getMethod("setVibratorEffect", Int::class.java)
        } catch (e: NoSuchMethodError) {
            Log.e(TAG, "failed to get method of name 'setVibratorEffect', error= $e")
            return 0L
        }
        try {
            val num = setVibrationEffect.invoke(this, *arrayOf<Any>(senceId)) as? Int
            return num?.toLong() ?: 1L
        } catch (e: IllegalAccessException) {
            Log.e(TAG, "setVibratorEffect# failed to set vibration effect: error= $e")
            return 1L
        } catch (e2: InvocationTargetException) {
            Log.e(TAG, "setVibratorEffect# failed to set vibration effect: error= $e2")
            return 1L
        }
    }
}