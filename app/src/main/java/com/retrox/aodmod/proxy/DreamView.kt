package com.retrox.aodmod.proxy

import android.app.AndroidAppHelper
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.os.Vibrator
import android.view.Display
import android.view.View
import com.retrox.aodmod.extensions.isOP7Pro
import com.retrox.aodmod.extensions.simpleTap

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
}

abstract class AbsDreamView(private val dreamProxy: DreamProxy) : DreamProxyController by dreamProxy, LifecycleOwner by dreamProxy,
    DreamView {
    override val context: Context
        get() = dreamProxy.dreamService

    override fun onSingleTap() {
        if (isOP7Pro()) {
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

class DemoDreamView(dreamProxy: DreamProxy) : AbsDreamView(dreamProxy) {
    override val layoutTheme: String
        get() = "TEST"

    override fun onCreateView(): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAvoidScreenBurnt(mainView: View, lastTime: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}