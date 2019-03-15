package com.retrox.aodmod.app.view

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.widget.*
import com.retrox.aodmod.R
import com.retrox.aodmod.app.MainActivity
import com.retrox.aodmod.app.XposedUtils
import com.retrox.aodmod.app.state.AppState
import com.retrox.aodmod.app.util.Utils
import com.retrox.aodmod.shared.SharedContentManager
import org.jetbrains.anko.*


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

    val activeState = MutableLiveData<Pair<Boolean,Boolean>>()

    init {
        val callback: (Boolean, Boolean) -> Unit = { isActive, appInList ->
            activeState.value = isActive to appInList
        }

        AppState.isActive.observe(lifecycleOwner, Observer {
            callback(it ?: false, AppState.expApps.value?.any { it.contains("com.oneplus.aod") } ?: false)
        })
        AppState.expApps.observe(lifecycleOwner, Observer {
            callback(AppState.isActive.value ?: false, it?.any { it.contains("com.oneplus.aod") } ?: false)
        })

        val view = context.verticalLayout {
            textView {
                textColor = Color.BLACK
                activeState.observe(lifecycleOwner, Observer {
                    it?.let { (isActive, _) ->
                        text = if (isActive) "模块已激活" else "模块尚未激活"
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
                        text = if (isAppInList) "主动显示已添加" else "主动显示未添加(需要太极Magisk)"
                    }
                })
                setOnClickListener {
                    val t = Intent("me.weishu.exp.ACTION_ADD_APP")
                    t.data = Uri.parse("package:" + "com.oneplus.aod")
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

            layoutParams = FrameLayout.LayoutParams(matchParent, wrapContent)
        }

        attachView(view)
        title.value = "激活状态"

        activeState.observe(lifecycleOwner, Observer {
            it?.let {
                if (it.first && it.second) {
                    titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    status.value = "已激活"
                } else {
                    titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorOrange)
                    status.value = "尚未激活"
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


class RunStatusCard(context: Context, lifecycleOwner: LifecycleOwner) : StatusCard(context, lifecycleOwner) {

    val textView = TextView(context).apply {
        textColor = Color.BLACK
        layoutParams = FrameLayout.LayoutParams(matchParent, wrapContent)
    }

    val layout = context.verticalLayout {
        addView(textView.lparams(wrapContent, wrapContent){
            verticalMargin = dip(8)
            horizontalMargin = dip(16)
        })
        textView("留意息屏次数和上次息屏时间，如果息屏次数和时间没有随着息屏操作而变化，证明模块并没有正常工作。如果这是因为模块更新而造成，请尝试重新打钩模块。") {
            textColor = Color.parseColor("#9B9B9B")
            textSize = 14f
        }.lparams(matchParent, wrapContent) {
            horizontalMargin = dip(8)
            verticalMargin = dip(4)
        }
        linearLayout {
            orientation = LinearLayout.HORIZONTAL
            button {
                text = "点击刷新"
                setBorderlessStyle()
                textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                setOnClickListener {
                    refresh()
                }
            }.lparams(wrapContent, wrapContent)

            button {
                text = "重新打钩"
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
        }.lparams(matchParent, wrapContent)

    }

    init {
        attachView(layout)
        title.value = "工作状态"
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
            text = "强力重启息屏 需要Root"
            setBorderlessStyle()
            textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            setOnClickListener {
                Utils.findProcessAndKill(context)
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
        leftPadding = dip(8)
    }

    init {
        attachView(layout)
        title.value = "工具箱"
        titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
    }
}

fun Button.setBorderlessStyle() {
    val outValue = TypedValue()
    context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
    setBackgroundResource(outValue.resourceId)
}