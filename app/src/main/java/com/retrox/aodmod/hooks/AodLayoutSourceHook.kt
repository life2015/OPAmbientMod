package com.retrox.aodmod.hooks

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.state.AodState
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

object AodLayoutSourceHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != MainHook.PACKAGE_AOD) return

        val classLoader = lpparam.classLoader
        val singleNotificationView = XposedHelpers.findClass("com.oneplus.aod.SingleNotificationView", classLoader)

        val paramClass = XposedHelpers.findClass("com.oneplus.aod.NotificationData\$Entry", classLoader)
        MainHook.logD(paramClass.toGenericString()) // 检测内部类Hack

        XposedHelpers.findAndHookMethod(singleNotificationView, "updateViewInternal",paramClass, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)

                val layout = param.thisObject as LinearLayout // 息屏通知的Layout

                val notificationDefaultContainerId = layout.getId("notification_default")
                val notificationDefaultContainer: LinearLayout = layout.findViewById(notificationDefaultContainerId)

                val headerContainerId = layout.getId("header_container") // 内含Icon 和 应用名字
                val headerContainer: LinearLayout = layout.findViewById(headerContainerId)

                val notificationHeader: TextView = layout.findViewById(layout.getId("single_notification_header"))

                val textView = TextView(headerContainer.context).apply {
                    textSize = 20f
                    textColor = Color.WHITE
                    text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date())
                    gravity = Gravity.CENTER_HORIZONTAL
                    setGoogleSans()
                    layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent).apply {
                        verticalMargin = dip(20)
                    }
                }

                headerContainer.apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL // 或者遍历子部件 设置layout_gravity
                    notificationHeader.apply {
                        textSize = 22f
                        setGoogleSans()
                        layoutParams = LinearLayout.LayoutParams(layoutParams).apply {
                            verticalMargin = dip(16)
                        }

                    }

                    if (getChildAt(0) is TextView) {
                        removeViewAt(0)
                    }
                    addView(textView, 0)
                }

                MainHook.logD("SingleLayout Hook Finish")

                // Animate! 检查状态避免重复动画
                val state = AodState.getDisplayState()
                MainHook.logD("Check Layout Hook State: Old -> new: ${AodState.getDisplayState()}")
                if (state == 0) {
                    layout.apply {
                        translationY = -500f
                        val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, -300f, 0f).apply {
                            duration = 1000L
                        }
                        animator.start()
                    }
                } else {
                    layout.apply {
                        val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f, 100f).apply {
                            duration = 200L
                        }
                        val animator2 = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 100f, 0f).apply {
                            duration = 200L
                        }
                        val animatorSet = AnimatorSet().apply {
                            playSequentially(animator, animator2)
                        }
                        animatorSet.start()
                    }
                }

            }
        })

        XposedHelpers.findAndHookMethod(
            singleNotificationView,
            "setNewPosted",
            "android.service.notification.StatusBarNotification",
            Boolean::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val thisObj = param.thisObject
                    val newPostNoti = XposedHelpers.getObjectField(thisObj, "mNewPostedNotification")
                    val isUpdate = XposedHelpers.getObjectField(thisObj, "mIsUpdate")
                    val mIsTheFirstNotification = XposedHelpers.getBooleanField(thisObj, "mIsTheFirstNotification")

                    MainHook.logD("newPostNoti: $newPostNoti isUpdate: $isUpdate isTheFirst: $mIsTheFirstNotification")
                }
            }
        )

        XposedHelpers.findAndHookMethod(singleNotificationView, "onAttachedToWindow", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                val layout = param.thisObject as LinearLayout

                layout.apply {
                    gravity = Gravity.TOP
                    topPadding = dip(192)
                }

                val notificationDefaultContainerId = layout.getId("notification_default")
                val notificationDefaultContainer: LinearLayout = layout.findViewById(notificationDefaultContainerId)

                val singleNotificationTitle : TextView = layout.findViewById(layout.getId("single_notification_title"))

                singleNotificationTitle.apply {
                    layoutParams = LinearLayout.LayoutParams(layoutParams).apply {
                        bottomMargin = dip(12)
                        topMargin = dip(16)
                    }

                    val titleContent = text.toString()
                    if (titleContent.contains("西西")) {
                        AodState.isImportantMessage = true
                    }
                }
                repeat(notificationDefaultContainer.childCount) { index ->
                    val view = notificationDefaultContainer.getChildAt(index)
                    if (view is TextView) { // Hack 息屏Text样式
                        view.apply {
                            textSize = 18f
                            gravity = Gravity.CENTER
                            setGoogleSans()
                        }
                    }
                }

                MainHook.logD("SingleLayout Hook AttachWindow")
            }
        })
    }

    fun View.getId(name: String) = this.context.resources.getIdentifier(name, "id", MainHook.PACKAGE_AOD)
}