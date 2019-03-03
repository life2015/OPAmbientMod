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
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.app.state.AppState.expApps
import com.retrox.aodmod.app.state.AppState.isActive
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
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    radius = dip(12).toFloat()

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        text = "##上车提示 -> 太极·Magisk运行环境需求：\n严格要求 太极App -> 5.0.2 及以上  太极Magisk插件版本 4.7.5(可加群下载) 及以上 "
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
                                    text = "主动显示APP已添加 \n 首次添加需要重启系统"
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
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorOrange))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        text = "设置睡眠模式\n仅Always On模式需要"
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        startActivity<SleepModeActivity>()
                    }

                }.lparams(matchParent, dip(60)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }

                cardView {
                    setCardBackgroundColor(Color.parseColor("#8B572A"))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        text = "重启系统息屏显示功能（强力重启 需要Root 如果卡死 请确保Magisk打开）"
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        try {
                            var line: String
                            val process = Runtime.getRuntime().exec("su")
                            val stdin = process.outputStream
                            val stderr = process.errorStream
                            val stdout = process.inputStream

                            stdin.write("ps -e \n".toByteArray())
                            stdin.flush()
                            stdin.write("exit\n".toByteArray())
                            stdin.close()

                            var br = BufferedReader(InputStreamReader(stdout))
                            br.lineSequence().forEach {
                                if (it.contains("com.oneplus.aod")) {
                                    val strings = it.split(" ".toRegex()).filterNot { it == "" || it == " " }
                                    Log.d("[PidTEST]", strings.toString())

                                    if (strings.isNotEmpty()) {
                                        runOnUiThread {
                                            Toast.makeText(
                                                context,
                                                "查询成功：Pid:${strings[1]} 已重启息屏程序 点击按钮可以再次重启",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        pid = strings[1]
                                    }
                                }
                            }

                            br.close()
                            br = BufferedReader(InputStreamReader(stderr))
                            br.lineSequence().forEach {
                                Log.e("[Error]", it)
                            }

                            br.close()

                            process.waitFor()
                            process.destroy()

                            kill() // kill aod

                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }


                }.lparams(matchParent, dip(60)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }


                cardView {
                    setCardBackgroundColor(Color.parseColor("#8B572A"))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        text = "重启系统息屏显示功能（非Root）"
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        val intent = Intent("com.retrox.aod.killmyself")
                        sendBroadcast(intent)
                        Toast.makeText(context, "已发送重启请求", Toast.LENGTH_SHORT).show()

                    }

                }.lparams(matchParent, dip(60)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }

                cardView {
                    setCardBackgroundColor(Color.parseColor("#8B572A"))
                    radius = dip(12).toFloat()
                    verticalPadding = dip(12)

                    textView {
                        textColor = Color.WHITE
                        textSize = 16f
                        text = "加群交流♂反馈"
                        horizontalPadding = dip(16)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }.lparams {
                        gravity = Gravity.CENTER
                    }

                    setOnClickListener {
                        joinQQGroup("GeuMQLnKNWcIQ-Hmy9H98m3HO62dogGp")

                    }

                }.lparams(matchParent, dip(60)) {
                    horizontalMargin = dip(8)
                    verticalMargin = dip(8)
                }
            }
        }
    }

    private fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(this, "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show()
            false
        }

    }

    fun kill() {
        if (pid == "") {
            Toast.makeText(this, "未查询到pid，您的手机可能不是一加，如果是，请联系开发者", Toast.LENGTH_SHORT).show()
        }
        try {
            val process = Runtime.getRuntime().exec("su")
            val stdin = process.outputStream
            val stderr = process.errorStream
            val stdout = process.inputStream

            stdin.write("kill $pid \n".toByteArray())
            stdin.flush()
            stdin.write("exit\n".toByteArray())
            stdin.close()

            var br = BufferedReader(InputStreamReader(stdout))
            br.lineSequence().forEach {

            }

            br.close()
            br = BufferedReader(InputStreamReader(stderr))
            br.lineSequence().forEach {
                Log.e("[Error]", it)
            }

            br.close()

            process.waitFor()
            process.destroy()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        AppState.refreshActiveState(this)
        AppState.refreshExpApps(this)
    }
}
