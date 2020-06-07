package com.retrox.aodmod.proxy.view.custom.oneplus.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.opimports.OpCustomTextClock
import com.retrox.aodmod.opimports.OpDateTimeView
import com.retrox.aodmod.opimports.OpOneRedStyleClock
import com.retrox.aodmod.opimports.OpTextClock
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor
import java.util.*

fun Context.opAodDateTimeView() : OpDateTimeView {
    val root = OpDateTimeView(this)
    root.id = R.id.date_time_view
    val context = this
    val resources = ResourceUtils.getInstance(context)
    val linearLayout = linearLayout {
        id = R.id.keyguard_clock_container
        layoutParams = ViewGroup.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
        addView(OpTextClock(context, resources.getString(R.string.keyguard_widget_12_hours_format), resources.getString(R.string.keyguard_widget_24_hours_format), TimeZone.getDefault().id).apply {
            id = R.id.clock_view
        })
        addView(opAodAnalogClockView(resources))
        addView(OpCustomTextClock(context, 1, resources.getColor(R.color.op_aod_digitalclock_gradient_start), resources.getColor(R.color.op_aod_digitalclock_gradient_end), resources.getColor(R.color.op_aod_textclock_top), resources.getColor(R.color.oneplus_contorl_text_color_primary_dark), R.string.textclock_template).apply {
            typeface = resources.getFont(R.font.oneplus_aod_font)
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            visibility = View.GONE
            id = R.id.custom_clock_view
        })
            addView(OpOneRedStyleClock(context, Color.parseColor("#eb0028"), resources.getString(R.string.keyguard_widget_12_hours_format), resources.getString(R.string.keyguard_widget_24_hours_format)).apply {
            isElegantTextHeight = false
            gravity = Gravity.CENTER_HORIZONTAL
            id = R.id.red_clock_view
            letterSpacing = 0.02f
            setPadding(0, 0, 0, resources.getDimension(R.dimen.title_clock_padding).toInt())
            singleLine = true
            textColor = resources.getColor(R.color.clock_ten_digit_white)
            visibility = View.GONE
            textSize = resources.getDimension(R.dimen.widget_big_font_size)
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        })
    }
    root.addView(linearLayout)
    return root
}