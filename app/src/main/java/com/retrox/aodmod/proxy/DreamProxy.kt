package com.retrox.aodmod.proxy

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.service.dreams.DreamService
import android.view.Display
import android.view.WindowManager
import com.retrox.aodmod.MainHook
import de.robv.android.xposed.XposedHelpers
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import android.view.View

class DreamProxy(override val dreamService: DreamService) : DreamProxyInterface {

    val context: Context = dreamService
    val windowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager;
    }
    var mainView: View? = null
    override fun onCreate() {
        XposedHelpers.callMethod(dreamService, "setWindowless", true)
        MainHook.logD("DreamProxy -> OnCreate")
    }

    override fun onAttachedToWindow() {
        MainHook.logD("DreamProxy -> onAttachedToWindow")

    }

    override fun onDreamingStarted() {
        MainHook.logD("DreamProxy -> onDreamingStarted")
        val view = context.verticalLayout {
            textView {
                text = "Hello Hook"
                textSize = 30f
                textColor = Color.WHITE
            }
            backgroundColor = Color.BLACK
        }
        mainView = view

        windowManager.addView(view, getAodViewLayoutParams())
        XposedHelpers.callMethod(dreamService, "startDozing")
        XposedHelpers.callMethod(dreamService, "setDozeScreenState", Display.STATE_DOZE)
    }

    override fun onDreamingStopped() {
        MainHook.logD("DreamProxy -> onDreamingStopped")
        windowManager.removeViewImmediate(mainView)
    }

    override fun onWakingUp() {
        MainHook.logD("DreamProxy -> onWakingUp")

    }

    override fun onSingleTap() {
        MainHook.logD("DreamProxy -> onSingleTap")

    }


    private fun getAodViewLayoutParams(): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        params.type = 2303
        params.layoutInDisplayCutoutMode = 1
        params.flags = 1280
        params.format = -2
        params.width = -1
        params.height = -1
        params.gravity = 17
        params.screenOrientation = 1
        params.title = "OPAod"
        params.softInputMode = 3
        return params
    }

}