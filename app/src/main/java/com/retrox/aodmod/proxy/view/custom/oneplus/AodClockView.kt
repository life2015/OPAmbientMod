package com.retrox.aodmod.proxy.view.custom.oneplus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Gravity
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.ViewFlipper
import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.R
import com.retrox.aodmod.SmaliImports
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.appendSpace
import com.retrox.aodmod.extensions.generateAlarmText
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.opimports.OPUtilsBridge
import com.retrox.aodmod.opimports.OpClockViewCtrl
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.view.custom.oneplus.views.opAodDateTimeView
import com.retrox.aodmod.receiver.PowerData
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.state.AodClockTick
import com.retrox.aodmod.state.AodState
import com.retrox.aodmod.weather.WeatherProvider
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import java.text.SimpleDateFormat
import java.util.*


fun Context.aodClockView(lifecycleOwner: LifecycleOwner, parent: ViewGroup): View {
    return linearLayout {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        OPUtilsBridge.init(context)
        //val layout = context.layoutInflater.inflate(R.layout.op_aod_date_time_view, parent, false)
        val layout = opAodDateTimeView()
        val clockControl = OpClockViewCtrl(context, layout as ViewGroup)
        clockControl.onTimeChanged()
        AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
            clockControl.onTimeChanged()
        })
        addView(layout)
        textView {
            id = Ids.tv_today
            textColor = Color.WHITE
            textSize = 17f
            setLineSpacing(dip(4).toFloat(), 1.0f)
            setGoogleSans()
            fun generateTodayText(): SpannableStringBuilder {
                return SpannableStringBuilder().apply {
                    append(
                        SimpleDateFormat(
                            SmaliImports.systemDateFormat,
                            Locale.getDefault()
                        ).format(Date())
                    )
                    if (XPref.getShowAlarm()) {
                        appendSpace()
                        append(generateAlarmText(context, false))
                        appendSpace()
                    }
                }
            }
//            text = SimpleDateFormat("E MM. dd", Locale.ENGLISH).format(Date())
            text = generateTodayText()
            AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
                text = generateTodayText()
            })
            gravity = Gravity.CENTER
        }.lparams(width = wrapContent, height = wrapContent) {
            topMargin = dip(16)
        }
        //Battery icon
        textView {
            id = Ids.tv_battery
            setGoogleSans()
            setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium)
            textColor = Color.WHITE
            AodState.powerState.observe(lifecycleOwner, Observer {
                it?.let {
                    val statusIcon = when{
                        it.fastCharge -> ResourceUtils.getInstance(context).getDrawable(R.drawable.aod_ic_battery_fast_charging)
                        it.plugged -> ResourceUtils.getInstance(context).getDrawable(R.drawable.aod_ic_battery_charging)
                        it.charged -> ResourceUtils.getInstance(context).getDrawable(R.drawable.aod_ic_battery_charged)
                        else -> getBatteryIcon(it)
                    }
                    if(!it.plugged && !it.fastCharge && !it.charged){
                        statusIcon.setBounds(0, 0, 40, 60)
                    }else{
                        statusIcon.setBounds(0, 0, 60, 60)
                    }
                    this.setCompoundDrawables(null, null, statusIcon, null)
                    text = "${it.level}% "
                }
            })
        }.lparams(width = wrapContent, height = wrapContent) {
            topMargin = dip(16)
            bottomMargin = dip(16)
        }
        linearLayout {
            id = Ids.ll_icons
            orientation = LinearLayout.HORIZONTAL

            val refreshBlock = {
                val icons = NotificationManager.notificationMap.values.asSequence()
                    .map { it.notification }
                    .filter {
                        it.extras.getInt(
                            NotificationManager.EXTRA_IMPORTANTCE,
                            2
                        ) > 1
                    } // 过滤掉不重要通知
//                    .filter { it. }
                    .map {
                        try {
                            it.smallIcon.loadDrawable(context)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            return@map null
                        }
                    }
                    .filterNot { it == null || it is VectorDrawable }
                    .toList()

//                MainHook.logD("icons: $icons")

                removeAllViews()
                icons.take(6).forEach {
                    imageView {
                        setImageDrawable(it)
//                        colorFilter = grayColorFilter
                        imageTintList = ColorStateList.valueOf(Color.WHITE)
                    }.lparams(width = dip(20), height = dip(24)) {
                        horizontalMargin = dip(4)
                        topMargin = dip(24)
                    }
                }
            }
            refreshBlock.invoke()
            NotificationManager.notificationStatusLiveData.observe(lifecycleOwner, Observer {
                refreshBlock.invoke()
            })

        }.lparams(width = wrapContent, height = wrapContent) {
        }
    }
}

fun getBatteryIcon(it: PowerData): Drawable {
    val batteryLevel = it.level
    val resources = ResourceUtils.getInstance()
    val baseDrawable = resources.getDrawable(R.drawable.op_battery_mask)
    var batteryDrawableRes = 0
    if(batteryLevel >= 0) batteryDrawableRes = R.drawable.op_battery_mask_0
    if(batteryLevel >= 5) batteryDrawableRes = R.drawable.op_battery_mask_5
    if(batteryLevel >= 10) batteryDrawableRes = R.drawable.op_battery_mask_10
    if(batteryLevel >= 15) batteryDrawableRes = R.drawable.op_battery_mask_15
    if(batteryLevel >= 20) batteryDrawableRes = R.drawable.op_battery_mask_20
    if(batteryLevel >= 25) batteryDrawableRes = R.drawable.op_battery_mask_25
    if(batteryLevel >= 30) batteryDrawableRes = R.drawable.op_battery_mask_30
    if(batteryLevel >= 35) batteryDrawableRes = R.drawable.op_battery_mask_35
    if(batteryLevel >= 40) batteryDrawableRes = R.drawable.op_battery_mask_40
    if(batteryLevel >= 45) batteryDrawableRes = R.drawable.op_battery_mask_45
    if(batteryLevel >= 50) batteryDrawableRes = R.drawable.op_battery_mask_50
    if(batteryLevel >= 55) batteryDrawableRes = R.drawable.op_battery_mask_55
    if(batteryLevel >= 60) batteryDrawableRes = R.drawable.op_battery_mask_60
    if(batteryLevel >= 65) batteryDrawableRes = R.drawable.op_battery_mask_65
    if(batteryLevel >= 70) batteryDrawableRes = R.drawable.op_battery_mask_70
    if(batteryLevel >= 75) batteryDrawableRes = R.drawable.op_battery_mask_75
    if(batteryLevel >= 80) batteryDrawableRes = R.drawable.op_battery_mask_80
    if(batteryLevel >= 85) batteryDrawableRes = R.drawable.op_battery_mask_85
    if(batteryLevel >= 90) batteryDrawableRes = R.drawable.op_battery_mask_90
    if(batteryLevel >= 95) batteryDrawableRes = R.drawable.op_battery_mask_95
    if(batteryLevel >= 100) batteryDrawableRes = R.drawable.op_battery_mask_100

    return LayerDrawable(arrayOf(baseDrawable, resources.getDrawable(batteryDrawableRes)))
}


