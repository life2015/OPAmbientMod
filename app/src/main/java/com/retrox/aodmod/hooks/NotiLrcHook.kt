package com.retrox.aodmod.hooks

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.retrox.aodmod.remote.lyric.LrcSync
import com.retrox.aodmod.util.ToggleableXC_MethodHook
import com.retrox.aodmod.util.XC_MethodHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

object NotiLrcHook : IXposedHookLoadPackage {
    val layoutId = View.generateViewId()
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return

        val classLoader = lpparam.classLoader

        val mediaNotiViewClazz = XposedHelpers.findClass("com.android.internal.widget.MediaNotificationView", classLoader)
        XposedHelpers.findAndHookMethod(mediaNotiViewClazz, "onFinishInflate", ToggleableXC_MethodHook(object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                val mediaColumn = XposedHelpers.getObjectField(param.thisObject, "mMainColumn") as LinearLayout


                val context = mediaColumn.context
                val layout = context.linearLayout {
                    id = layoutId
                    textView {
                        textColor = Color.WHITE

//                        Looper.prepare()
                        LrcSync.currentLrcRowLive.observeForever {
                            if (it == null) {
                                visibility = View.GONE
                            } else {
                                visibility = View.VISIBLE

                                val originalTitleLayout = mediaColumn.getChildAt(0) as? LinearLayout
                                val colors = (originalTitleLayout?.getChildAt(0) as? TextView)?.textColors
                                colors?.let {
                                    setTextColor(it)
                                }
                                text = it.content
                            }
                        }
                    }
                }
                val view: View? = mediaColumn.findViewById<View>(layoutId)
                if (view == null) {
                    mediaColumn.addView(layout)
                }

            }
        }))

    }
}