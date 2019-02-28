package com.retrox.aodmod.hooks

import android.app.AndroidAppHelper
import android.content.IntentFilter
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.receiver.MediaMessageReceiver
import com.retrox.aodmod.state.AodMedia
import com.retrox.aodmod.state.AodState
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.jetbrains.anko.*

object AodMainMediaHook : IXposedHookLoadPackage {
    private val bottomMediaViewTag = "BOTTOMMEDIAVIEWTAG"


    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != MainHook.PACKAGE_AOD) return
        val classLoader = lpparam.classLoader

        val aodUpdateMonitorClass = XposedHelpers.findClass("com.oneplus.aod.AodUpdateMonitor", classLoader)
        XposedHelpers.findAndHookMethod(aodUpdateMonitorClass, "init", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val intentFilter = IntentFilter()
                intentFilter.addAction("com.retrox.aodmod.NEW_MEDIA_META")
                val receiver = MediaMessageReceiver()
                AndroidAppHelper.currentApplication().registerReceiver(receiver, intentFilter)
                MainHook.logD("mediaMessageReceiver registered")
            }
        })

        val displayViewManagerClass = XposedHelpers.findClass("com.oneplus.aod.DisplayViewManager", classLoader)
        XposedHelpers.findAndHookMethod(displayViewManagerClass, "updateView", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val state = XposedHelpers.getIntField(param.thisObject, "mState") // 1 -> 抬手息屏
                MainHook.logD("Media Hook display state: $state")

                val displayViewManager = param.thisObject
                val aodMainView = XposedHelpers.getObjectField(displayViewManager, "mMainView") as LinearLayout
                val parent = aodMainView.parent as RelativeLayout

                val oldView: View? = parent.findViewWithTag(bottomMediaViewTag)
                oldView?.let { parent.removeView(it) }

                if (state != 1) return


                MainHook.logD("Media hook data real: ${MediaControl.metadata}")
                val mediaMetadata = AodState.mediaMetadata ?: return

                val artist = mediaMetadata.artist
                val musicName = mediaMetadata.name

                val linearLayout = parent.context.verticalLayout {
                    imageView {
                        setImageDrawable(ResourceUtils.getInstance(this).getDrawable(R.drawable.ic_music))
                    }.lparams(dip(24), dip(24)) {
                        gravity = Gravity.CENTER_HORIZONTAL
                        verticalMargin = dip(16)
                    }

                    textView {
                        textSize = 16f
                        textColor = Color.WHITE
                        setGoogleSans()
                        text = "$musicName - $artist"
                        AodMedia.aodMediaLiveData.observeForever {
                            it?.let {
                                text = "${it.name} - ${it.artist}"
                            }
                        }
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams(wrapContent, wrapContent) {
                        horizontalMargin = dip(24)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }

                    layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent).apply {
                        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                        gravity = Gravity.CENTER_HORIZONTAL
                        bottomMargin = dip(24)
                    }

                    alpha = 0.7f
                    tag = bottomMediaViewTag
                }



                parent.addView(linearLayout)
                MainHook.logD("music view hook added")
            }
        })

    }
}