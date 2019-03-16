package com.retrox.aodmod.app

import android.arch.lifecycle.Observer
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.app.state.AppState.expApps
import com.retrox.aodmod.app.state.AppState.isActive
import com.retrox.aodmod.app.view.joinQQGroup
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 不要在这里使用Xposed api相关的类
 * 会导致 NoClassDef
 */
class MainActivity : AppCompatActivity() {
    var pid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        MainHook.logD(service.getActiveSessions(null).toString())
        isActive.value = XposedUtils.isExpModuleActive(this@MainActivity)
        expApps.value = XposedUtils.getExpApps(this@MainActivity)

        scrollView {
            verticalLayout {
                cardView {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPixelBlue))
                    radius = dip(12).toFloat()

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        text = "##上车提示 -> 太极·Magisk运行环境需求：\n严格要求 太极App -> 5.0.2 及以上  太极Magisk插件版本 4.7.5(可加群下载) 及以上\nEdXposed也可以上车\n上车前确保自己系统开着抬手显示 "
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                        verticalMargin = dip(16)
                    }

                    setOnClickListener {
                        joinQQGroup("GeuMQLnKNWcIQ-Hmy9H98m3HO62dogGp")

                    }

                }.lparams(matchParent, wrapContent) {
                    horizontalMargin = dip(4)
                    verticalMargin = dip(4)
                }

                cardView {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorGray))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 17f
                        isActive.observe(this@MainActivity, Observer {
                            it?.let { active ->
                                if (active) {
                                    text = "模块已激活\n 重装或者升级APP建议重新打钩并且点击下方重启息屏显示"
                                    this@cardView.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.colorPixelBlue
                                        )
                                    )
                                    alpha = 0.85f
                                } else {
                                    text = "模块尚未激活 请在太极·Magisk中激活模块"
                                    this@cardView.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.colorGray
                                        )
                                    )
                                    alpha = 1f
                                }
                            }

                        })
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        val t = Intent("me.weishu.exp.ACTION_MODULE_MANAGE")
                        t.data = Uri.parse("package:" + "com.retrox.aodmod")
                        t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try {
                            startActivity(t)
                        } catch (e: ActivityNotFoundException) {
                            // TaiChi not installed.
                        }
                    }
                }.lparams(matchParent, dip(80)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }

                cardView {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorGray))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        expApps.observe(this@MainActivity, Observer {
                            it?.let { list ->
                                if (list.contains("com.oneplus.aod")) {
                                    text = "主动显示APP已添加 \n 首次添加需要点击下方强制重启"
                                    this@cardView.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.colorPixelBlue
                                        )
                                    )
                                    alpha = 0.85f
                                } else {
                                    text = "主动显示APP未添加 点击添加主动显示APP \n 需要太极·Magisk"
                                    this@cardView.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.colorGray
                                        )
                                    )
                                    alpha = 1f
                                }
                            }

                        })
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        val t = Intent("me.weishu.exp.ACTION_ADD_APP")
                        t.data = Uri.parse("package:" + "com.oneplus.aod")
                        t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try {
                            startActivity(t)
                        } catch (e: ActivityNotFoundException) {
                            // TaiChi not installed or version below 4.3.4.
                        }
                    }


                }.lparams(matchParent, dip(60)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }


                cardView {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorOrange))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 17f
                        text = "设置息屏音乐提醒"
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        startActivity<MusicSettingsActivity>()
                    }

                }.lparams(matchParent, dip(60)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }


                cardView {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorOrange))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        text = "设置息屏模式: 系统模式/常亮模式 \n当前模式: ${AppPref.aodMode}"
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        startActivity<AodModeActivity>()
                    }

                }.lparams(matchParent, dip(60)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }

                cardView {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPixelBlue))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        text = "Always ON 常亮模式自定义设置\n 翻转亮屏，敏感消息隐藏等设置"
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        startActivity<AlwaysOnSettings>()
                    }

                }.lparams(matchParent, dip(60)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }



            }
        }
    }




    override fun onResume() {
        super.onResume()
        AppState.refreshActiveState(this)
        AppState.refreshExpApps(this)
    }
}
