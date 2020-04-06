package com.retrox.aodmod.app

import androidx.lifecycle.Observer
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.app.state.AppState.expApps
import com.retrox.aodmod.app.state.AppState.isActive
import com.retrox.aodmod.app.view.joinQQGroup
import com.retrox.aodmod.shared.global.GlobalCacheManager
import com.retrox.aodmod.shared.global.GlobalKV
import com.retrox.aodmod.shared.global.OwnFileManager
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import kotlin.random.Random

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
                        text = context.getString(R.string.main_activity_instructions)
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
                                    text = context.getString(R.string.main_module_active)
                                    this@cardView.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.colorPixelBlue
                                        )
                                    )
                                    alpha = 0.85f
                                } else {
                                    text = context.getString(R.string.main_module_inactive)
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
                                    text = context.getString(R.string.main_aod_added)
                                    this@cardView.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.colorPixelBlue
                                        )
                                    )
                                    alpha = 0.85f
                                } else {
                                    text = context.getString(R.string.main_aod_not_added)
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
                        text = context.getString(R.string.main_music_settings)
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
                        text = context.getString(R.string.main_aod_mode, AppPref.aodMode)
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
                        text = context.getString(R.string.main_aod_settings)
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
        OwnFileManager.writeFileWithContent("hello.file", "Hello Boy! ${Random.nextInt()}")

    }
}
