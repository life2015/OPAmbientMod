package com.retrox.aodmod.proxy.view.custom.oneplus.views

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.opimports.OpAnalogClock
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import kotlin.math.roundToInt

fun Context.opAodAnalogClockView(resources: ResourceUtils) : OpAnalogClock {
    val opAnalogClock = OpAnalogClock(this).apply {
        layoutParams = ViewGroup.LayoutParams(resources.getDimension(R.dimen.clock_analog_size).toInt(), resources.getDimension(R.dimen.clock_analog_size).toInt())
        id = R.id.analog_clock_view
        visibility = View.GONE
    }
    val analogBackground = ImageView(this).apply {
        id = R.id.analog_background
        adjustViewBounds = true
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
            gravity = Gravity.CENTER
        }
    }
    opAnalogClock.addView(analogBackground)
    val relativeLayout = relativeLayout {
        id = R.id.analog_date_container
        layoutDirection = View.LAYOUT_DIRECTION_LTR
        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        textView {
            id = R.id.analog_date_view
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                leftMargin = resources.getDimension(R.dimen.aod_clock_min2_date_left).roundToInt()
            }
            textColor = Color.parseColor("#fafafa")
            textSize = resources.getDimension(R.dimen.aod_clock_analog_min2_date_size)
        }
    }
    opAnalogClock.addView(relativeLayout)
    val ids = arrayOf(R.id.analog_hour, R.id.analog_min, R.id.analog_sec, R.id.analog_dot, R.id.analog_outer)
    Log.d("XAod", "adding view")
    for(viewId in ids){
        val view = View(this).apply {
            id = viewId
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        opAnalogClock.addView(view)
    }
    //We have to call this ourselves
    opAnalogClock.onFinishInflate()
    return opAnalogClock
}