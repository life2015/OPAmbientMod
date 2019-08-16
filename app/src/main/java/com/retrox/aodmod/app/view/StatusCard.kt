package com.retrox.aodmod.app.view

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import com.retrox.aodmod.R
import com.retrox.aodmod.app.*
import com.retrox.aodmod.app.alipay.AlipayZeroSdk
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.app.util.Utils
import com.retrox.aodmod.extensions.LiveEvent
import com.retrox.aodmod.extensions.checkPermission
import com.retrox.aodmod.extensions.isOP7Pro
import com.retrox.aodmod.shared.SharedContentManager
import com.retrox.aodmod.weather.WeatherProvider
import org.jetbrains.anko.*
import kotlin.concurrent.thread


open class StatusCard(val context: Context, lifecycleOwner: LifecycleOwner) {
    val title = MutableLiveData<String>()
    val status = MutableLiveData<String>()
    val rootView: View = context.layoutInflater.inflate(R.layout.layout_status_card, null, false)
    val contentView = rootView.findViewById<FrameLayout>(R.id.fl_content)
    val titleBar = rootView.findViewById<ConstraintLayout>(R.id.cl_title)
    val titleView = rootView.findViewById<TextView>(R.id.tv_title)
    val statusView = rootView.findViewById<TextView>(R.id.tv_status)


    init {
        title.observe(lifecycleOwner, Observer {
            it?.let { titleView.text = it }
        })
        status.observe(lifecycleOwner, Observer {
            it?.let { statusView.text = it }
        })

        rootView.layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent).apply {
            bottomMargin = context.dip(8)
        }
    }

    fun attachView(view: View) {
        contentView.removeAllViews()
        contentView.addView(view)
    }

}

class ActiveStatusCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {

    val activeState = MutableLiveData<Pair<Boolean, Boolean>>()
    val aodAppName = if (isOP7Pro()) "com.android.systemui" else "com.oneplus.aod"

    init {
        val callback: (Boolean, Boolean) -> Unit = { isActive, appInList ->
            activeState.value = isActive to appInList
        }

        AppState.isActive.observe(lifecycleOwner, Observer {
            callback(it ?: false, AppState.expApps.value?.any { it.contains(aodAppName) } ?: false)
        })
        AppState.expApps.observe(lifecycleOwner, Observer {
            callback(AppState.isActive.value ?: false, it?.any { it.contains(aodAppName) } ?: false)
        })

        val view = context.verticalLayout {
            textView {
                textColor = Color.BLACK
                activeState.observe(lifecycleOwner, Observer {
                    it?.let { (isActive, _) ->
                        text = if (isActive) context.getString(R.string.active_status_already) else context.getString(R.string.active_status_need_active)
                    }
                })
                setOnClickListener {
                    val t = Intent("me.weishu.exp.ACTION_MODULE_MANAGE")
                    t.data = Uri.parse("package:" + "com.retrox.aodmod")
                    t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(t)
                    } catch (e: ActivityNotFoundException) {
                        // TaiChi not installed.
                        Toast.makeText(context, "太极尚未安装", Toast.LENGTH_SHORT).show()
                    }
                }
            }.lparams(matchParent, wrapContent) {
                verticalMargin = dip(8)
                horizontalMargin = dip(16)
            }

            textView {
                textColor = Color.BLACK
                activeState.observe(lifecycleOwner, Observer {
                    it?.let { (_, isAppInList) ->
                        text =
                            if (isAppInList) "主动显示/SystemUI(OP7Pro) 已添加" else context.getString(R.string.need_add_aod)
                    }
                })
                setOnClickListener {
                    val t = Intent("me.weishu.exp.ACTION_ADD_APP")
                    t.data = Uri.parse("package:" + aodAppName)
                    t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(t)
                    } catch (e: ActivityNotFoundException) {
                        // TaiChi not installed or version below 4.3.4.
                        Toast.makeText(context, "太极尚未安装或版本低于4.3.4", Toast.LENGTH_SHORT).show()
                    }
                }
            }.lparams(matchParent, wrapContent) {
                verticalMargin = dip(8)
                horizontalMargin = dip(16)
            }

            textView("注意：EdXposed自己弄好的话，请忽略以上提示!! 使用太极添加App时，OP7Pro之前是主动显示，OP7Pro是系统界面SystemUI") {
                activeState.observe(lifecycleOwner, Observer {
                    it?.let {
                        if (it.first && it.second) {
                            textColor = Color.parseColor("#9B9B9B")
                            textSize = 16f
                        } else {
                            textColor = Color.BLACK
                            textSize = 18f
                        }
                    }
                })


            }.lparams(matchParent, wrapContent) {
                horizontalMargin = dip(8)
                verticalMargin = dip(4)
            }

            layoutParams = FrameLayout.LayoutParams(matchParent, wrapContent)
        }

        attachView(view)
        title.value = context.getString(R.string.active_state)

        activeState.observe(lifecycleOwner, Observer {
            it?.let {
                if (it.first && it.second) {
                    titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    status.value = context.getString(R.string.active_status_already)
                } else {
                    titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorOrange)
                    status.value = context.getString(R.string.active_state_noalready)
                }
            }
        })

        refresh()
    }

    fun refresh() {
        AppState.isActive.value = XposedUtils.isExpModuleActive(context)
        AppState.expApps.value = XposedUtils.getExpApps(context)
    }
}

class MotionAwakeStatCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {
    val layout = context.verticalLayout {
        horizontalPadding = dip(16)
        topPadding = dip(8)
        textView {
            textColor = Color.BLACK
            AppState.isMotionAwakeEnabled.observe(lifecycleOwner, Observer {
                it?.let {
                    text = if (it) {
                        titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                        status.value = context.getString(R.string.motion_awake_opened)
                        context.getString(R.string.pickup_enabled)
                    } else {
                        titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorOrange)
                        status.value = context.getString(R.string.please_enable_pick)
                        context.getString(R.string.status_pick_up_not_enabled)
                    }
                }
            })
        }.lparams {
            bottomMargin = dip(8)
        }

        textView {
            textColor = Color.parseColor("#9B9B9B")
            textSize = 14f
            text = context.getString(R.string.top_open_pick_up)
        }.lparams {
            bottomMargin = dip(4)
        }

        textView {
            textColor = Color.BLACK
            textSize = 14f
            text = context.getString(R.string.top_touch_to_see)
        }

        button {
            setBorderlessStyle()
            text = context.getString(R.string.open_pick_up_display)
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                val intent = Intent()
                intent.component = ComponentName("com.oneplus.aod", "com.oneplus.settings.SettingsActivity")
                context.startActivity(intent)
            }
        }.lparams(wrapContent, wrapContent)


    }

    init {
        attachView(layout)
        title.value = context.getString(R.string.tip_status_pick_up)
    }
}


class RunStatusCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {

    val textView = TextView(context).apply {
        textColor = Color.BLACK
        layoutParams = FrameLayout.LayoutParams(matchParent, wrapContent)
    }

    val layout = context.verticalLayout {
        addView(textView.lparams(wrapContent, wrapContent) {
            verticalMargin = dip(8)
            horizontalMargin = dip(16)
        })
        textView(context.getString(R.string.attention_work_status)) {
            textColor = Color.parseColor("#9B9B9B")
            textSize = 14f
        }.lparams(matchParent, wrapContent) {
            horizontalMargin = dip(8)
            verticalMargin = dip(4)
        }
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            button {
                text = context.getString(R.string.touch_refresh)
                setBorderlessStyle()
                textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                setOnClickListener {
                    refresh()
                }
            }.lparams(wrapContent, wrapContent)

            button {
                text = context.getString(R.string.rehook)
                setBorderlessStyle()
                textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                setOnClickListener {
                    val t = Intent("me.weishu.exp.ACTION_MODULE_MANAGE")
                    t.data = Uri.parse("package:" + "com.retrox.aodmod")
                    t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(t)
                    } catch (e: ActivityNotFoundException) {
                        // TaiChi not installed.
                        Toast.makeText(context, "太极尚未安装", Toast.LENGTH_SHORT).show()
                    }
                }
            }.lparams(wrapContent, wrapContent)

            button {
                text = context.getString(R.string.reboot_ambient)
                setBorderlessStyle()
                textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                setOnClickListener {
                    Utils.findProcessAndKill(context, if (isOP7Pro()) "com.android.systemui" else "com.oneplus.aod")
                }
            }.lparams(wrapContent, wrapContent)

        }.lparams(matchParent, wrapContent)

    }

    init {
        attachView(layout)
        title.value = context.getString(R.string.work_status)
        AppState.needRefreshStatus.observe(lifecycleOwner, Observer {
            refresh()
        })
        refresh()
    }

    fun refresh() {
        // todo IO性能优化
        val sharedState = SharedContentManager.getSharedState()

        val times = sharedState.aodTimes.toInt()
        val text = if (times > 0) {
            status.value = "正常 ${sharedState.workMode}"
            titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            "本次插件介入息屏的次数为 $times \n上次息屏时间：${sharedState.lastTime}"
        } else {
            status.value = "异常 ${sharedState.workMode}"
            titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorOrange)
            "本次插件介入息屏的次数为0 \n上次息屏时间：${sharedState.lastTime} \n如果这不是刚刚重启了息屏 那也许出了问题"
        }
        textView.text = text
    }
}

class ToolCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {
    val layout = context.verticalLayout {
        button {
            text = "学习一点必要的英语"
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                context.startActivity<EnglishLearnActivity>()
            }
        }.lparams(wrapContent, wrapContent)
        button {
            text = context.getString(R.string.force_restart_not_7p)
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                Utils.findProcessAndKill(context)
            }
        }.lparams(wrapContent, wrapContent)
        button {
            text = context.getString(R.string.force_restart_7p)
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                Utils.findProcessAndKill(context, "com.android.systemui")
            }
        }.lparams(wrapContent, wrapContent)
        button {
            text = "加群交流一下？"
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                val key = "8bW_c8foZfXB1NFZILBsupRDWblY3Lhl"
                context.joinQQGroup(key)
            }
        }.lparams(wrapContent, wrapContent)
        button {
            text = "群满可加入中转备用群"
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                val key = "20ARgc7Mzn0TNIKYJAiCXmfWg2FkPEog"
                context.joinQQGroup(key)
            }
        }.lparams(wrapContent, wrapContent)

        button {
            text = "Join Telegram Group EU/NA/IN"
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                val telegram = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/joinchat/FE-DFRPY5fduM2wTsl1Spg"));
                context.startActivity(telegram);
            }
        }.lparams(wrapContent, wrapContent)

        button {
            text = "回到老设置界面"
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                context.startActivity<MainActivity>()
            }
        }.lparams(wrapContent, wrapContent)
        button {
            text = "觉得好用？捐赠开发者"
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                if (AlipayZeroSdk.hasInstalledAlipayClient(context) && context is Activity) {
                    AlipayZeroSdk.startAlipayClient(context, "fkx08744aqofnhxpvkgd6d0")
                } else {
                    Toast.makeText(context, "支付宝未安装！", Toast.LENGTH_SHORT).show()
                }
            }
        }.lparams(wrapContent, wrapContent)
        leftPadding = dip(16)
    }

    init {
        attachView(layout)
        title.value = "工具箱"
        titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
    }


}

class SettingsCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {
    val layout = context.verticalLayout {
        button {
            text = context.getString(R.string.aod_music_settings)
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                context.startActivity<MusicSettingsActivity>()
            }
        }.lparams(wrapContent, wrapContent)
        button {
            text = context.getString(R.string.aod_custom_settings)
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                context.startActivity<AlwaysOnSettings>()
            }
        }.lparams(wrapContent, wrapContent)
        button {
            text = context.getString(R.string.clock_align_settings)
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                context.startActivity<AlarmSettingsActivity>()
            }
        }.lparams(wrapContent, wrapContent)
        leftPadding = dip(16)
    }

    init {
        attachView(layout)
        title.value = context.getString(R.string.settings)
        titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
    }


}

class WeatherCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {

    val weatherLiveData = WeatherProvider.weatherLiveEvent
    val layout = context.verticalLayout {

        textView {
            textColor = Color.BLACK

            weatherLiveData.observe(lifecycleOwner, Observer { weatherData ->
                val content = if (weatherData != null) {
                    with(weatherData) {
                        titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                        status.value = context.getString(R.string.weather_service_ok)
                        val result =
                            "当前天气数据：$cityName $weatherName $temperature$temperatureUnit \n今日温度范围：$temperatureLow/$temperatureHigh$temperatureUnit"
                        if (AppPref.aodShowWeather) {
                            result
                        } else {
                            "$result\n您的息屏未开启天气显示，可以在常亮自定义设置中打开"
                        }
                    }
                } else {
                    status.value = context.getString(R.string.weather_service_error)
                    titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorOrange)
                    AppPref.aodShowWeather = false
                    "获取天气数据失败 检查系统天气APP是否可用\n已自动关闭天气显示"
                }

                text = content
            })
        }.lparams(wrapContent, wrapContent) {
            verticalMargin = dip(8)
            horizontalMargin = dip(16)
        }
    }

    init {
        title.value = context.getString(R.string.weather_data)
        attachView(layout)

    }
}

class PermissionCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {
    val checkStat = MutableLiveData<Boolean>().apply {
        value = context.checkPermission {}
    }
    val activity = context as? Activity

    val layout = context.verticalLayout {
        textView {
            textColor = Color.BLACK
            checkStat.observe(lifecycleOwner, Observer {
                it?.let {
                    if (it) {
                        text = context.getString(R.string.storage_permission_granted)
                    } else {
                        text = context.getString(R.string.storage_permission_not_granted)
                    }
                }
            })
        }.lparams(wrapContent, wrapContent) {
            topMargin = dip(8)
            horizontalMargin = dip(16)
        }

        button {
            text = context.getString(R.string.check_permission_status)
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                activity?.let {
                    val result = it.checkPermission {
                        ActivityCompat.requestPermissions(
                            it,
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), 66
                        );
                    }
                    checkStat.value = result
                }
            }
        }.lparams(wrapContent, wrapContent) {
            leftMargin = dip(16)
        }

    }

    init {
        title.value = context.getString(R.string.permisssion_status)
        checkStat.observe(lifecycleOwner, Observer {
            it?.let {
                if (it) {
                    titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    status.value = "权限授予正常"
                } else {
                    titleBar.backgroundColor = Color.RED
                    status.value = "请授予储存权限"
                }
            }
        })
        attachView(layout)
    }
}

class ThemeCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {
    val themeLayoutList = listOf("Default", "Flat", "DVD", "PureMusic", "FlatMusic")
    val layout = context.linearLayout {
        orientation = LinearLayout.VERTICAL
        frameLayout {
            textView(context.getString(R.string.display_style)) {
                textColor = Color.BLACK
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
            spinner {
                background.setColorFilter(Color.parseColor("#568FFF"), PorterDuff.Mode.SRC_ATOP)
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, themeLayoutList)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val style = themeLayoutList[position]
                        AppPref.aodLayoutTheme = style
                        Toast.makeText(context, "主题已设置 $style", Toast.LENGTH_SHORT).show()
                    }
                }
                setSelection(themeLayoutList.indexOf(AppPref.aodLayoutTheme), true)
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.END
            }
        }.lparams(matchParent, wrapContent)

        textView(context.getString(R.string.music_lrc_support)) {
            textColor = Color.parseColor("#9B9B9B")
            textSize = 14f
        }.lparams(matchParent, wrapContent) {
            verticalMargin = dip(4)
        }

        button {
            text = context.getString(R.string.other_custom_style_settings)
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                context.startActivity<CustomActivity>()
            }
        }.lparams(wrapContent, wrapContent)
        leftPadding = dip(16)
    }

    init {
        title.value = context.getString(R.string.display_style_custom)
        titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
        attachView(layout)
    }
}

fun Button.setBorderlessStyle() {
    val outValue = TypedValue()
    context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
    setBackgroundResource(outValue.resourceId)
}

fun Context.joinQQGroup(key: String): Boolean {
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