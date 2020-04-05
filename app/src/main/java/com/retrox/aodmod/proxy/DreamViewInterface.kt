package com.retrox.aodmod.proxy

import android.app.AndroidAppHelper
import androidx.lifecycle.LifecycleOwner
import android.content.Context
import android.os.Vibrator
import android.view.Display
import android.view.View
import com.retrox.aodmod.extensions.isOP7Pro
import com.retrox.aodmod.extensions.simpleTap
import com.retrox.aodmod.pref.SystemPref

interface DreamView {
    val layoutTheme: String
    val context: Context
    fun onCreate() {}
    fun onCreateView(): View
    fun onDestroyView() {}
    fun onSingleTap() {}
    fun onScreenTurnOn(reason: String) {}
    fun onScreenTurnOff(reason: String) {}
    fun onScreenActive(reason: String) {}
    fun onAvoidScreenBurnt(mainView: View, lastTime: Long)
}

interface DreamProxyController {
    fun setScreenDoze(reason: String = "")
    fun setScreenActive(reason: String = "")
    fun setScreenOff(reason: String = "")
    fun getScreenState(): Int

    /**
     * 用于一些临时唤醒屏幕的操作
     * 比如说通知来了之类 如果屏幕状态本身是亮的就不会做自动关屏逻辑 或者time == -1L
     */
    fun screenPulse(time: Long = 15 * 1000L, block: () -> Unit)
}

abstract class AbsDreamView(private val dreamProxy: DreamProxy) : DreamProxyController by dreamProxy, LifecycleOwner by dreamProxy,
    DreamView {
    override val context: Context
        get() = dreamProxy.dreamService

    var lastTapTime = 0L
    override fun onSingleTap() {
        if (System.currentTimeMillis() - lastTapTime < 300L) { // 防抖动
            return
        }
        lastTapTime = System.currentTimeMillis()
        if (isOP7Pro() && !SystemPref.getNightAutoOffX()) {
            val vibrator = AndroidAppHelper.currentApplication().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.simpleTap()
        }
        if (getScreenState() == Display.STATE_OFF) {
            setScreenDoze("SingleTap")
        } else {
            setScreenOff("SingleTap")
        }
    }
}

