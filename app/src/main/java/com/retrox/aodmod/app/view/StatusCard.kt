package com.retrox.aodmod.app.view

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.retrox.aodmod.R
import com.retrox.aodmod.app.XposedUtils
import com.retrox.aodmod.app.pref.AppStatusPref
import com.retrox.aodmod.app.state.AppState
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
            }.lparams(matchParent, wrapContent) {
                verticalMargin = dip(8)
                horizontalMargin = dip(16)
            }

            textView {
                textColor = Color.BLACK
                activeState.observe(lifecycleOwner, Observer {
                    it?.let { (_, isAppInList) ->
                        text = if (isAppInList) "主动显示已添加" else "主动显示未添加"
                    }
                })
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
        button {
            text = "刷新"
            setOnClickListener {
                refresh()
            }
        }
    }

    init {
        attachView(layout)
        title.value = "工作状态"
        refresh()
    }

    fun refresh() {
        val times = AppStatusPref.alwaysOnHookTimes
        val text = if (times > 0) {
            status.value = "正常"
            titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
            "本次插件介入息屏的次数为 $times"
        } else {
            status.value = "异常"
            titleBar.backgroundColor = ContextCompat.getColor(context, R.color.colorOrange)
            "本次插件介入息屏的次数为0 \n如果这不是刚刚重启了息屏 那也许出了问题"
        }
        textView.text = text
    }
}