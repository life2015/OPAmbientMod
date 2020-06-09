package com.retrox.aodmod.proxy

import android.app.AndroidAppHelper
import android.content.Context
import android.content.Context.MEDIA_SESSION_SERVICE
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.os.Vibrator
import android.util.Log
import android.view.Display
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.retrox.aodmod.extensions.isOP7Pro
import com.retrox.aodmod.extensions.simpleTap
import com.retrox.aodmod.music.MediaCallback
import com.retrox.aodmod.pref.SystemPref
import com.retrox.aodmod.pref.XPref


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
    DreamView, MediaSessionManager.OnActiveSessionsChangedListener {
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

    override fun onCreate() {
        super.onCreate()
        if(XPref.getUseSystemMusic()) {
            setupMediaListener()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(XPref.getUseSystemMusic()) {
            removeMediaListener()
        }
    }

    private fun setupMediaListener(){
        val mediaSessionManager: MediaSessionManager = context.getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSessionManager.addOnActiveSessionsChangedListener(this, null)
        onActiveSessionsChanged(mediaSessionManager.getActiveSessions(null))
    }

    private fun removeMediaListener(){
        val mediaSessionManager: MediaSessionManager = context.getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSessionManager.removeOnActiveSessionsChangedListener(this)
    }

    private var controllers: List<MediaController>? = null
    private val callbacks: HashMap<String, MediaCallback> = HashMap()

    override fun onActiveSessionsChanged(list: MutableList<MediaController>?) {
        if(list == null) return
        for (controller in list) {
            if (controller.packageName != null && callbacks.containsKey(controller.packageName)) {
                callbacks[controller.packageName]?.let {
                    controller.unregisterCallback(it)
                    callbacks.remove(controller.packageName)
                }
            }
        }
        controllers = list
        for (controller in list) {
            if (controller.packageName != null) {
                val mediaCallback = MediaCallback(controller, context)
                controller.registerCallback(mediaCallback)
                callbacks[controller.packageName] = mediaCallback
            }
        }
    }
}

