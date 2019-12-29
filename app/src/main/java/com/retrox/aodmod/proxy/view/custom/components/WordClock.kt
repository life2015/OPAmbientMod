package com.retrox.aodmod.proxy.view.custom.components

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.view.ViewManager
import android.widget.TextView
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.extensions.toCNString
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.state.AodClockTick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import java.util.*

fun ViewManager.wordClock(lifecycleOwner: LifecycleOwner, init: TextView.() -> Unit = {}): TextView {
    return textView {
        text = ""
        textSize = 30f
        setLineSpacing(6f, 1f)
        textColor = Color.WHITE
        setGoogleSans()
        init()

        AodClockTick.tickLiveData.observe(lifecycleOwner, Observer {
            val cal = Calendar.getInstance()
            val hour = if (cal.get(Calendar.HOUR) == 0 ) 12 else cal.get(Calendar.HOUR) % 12
            val minute = cal.get(Calendar.MINUTE) % 60
            val month = cal.get(Calendar.MONTH) + 1
            val day = cal.get(Calendar.DAY_OF_MONTH)
            val weekDay = cal.get(Calendar.DAY_OF_WEEK)

            val hoursArray = ResourceUtils.getInstance(context)
                .resources.getStringArray(R.array.type_clock_hours)
            val minutesArray = ResourceUtils.getInstance(context)
                .resources.getStringArray(R.array.type_clock_minutes)

            // 系统语言是中文 并且关闭了强制英文时钟时候才使用中文时钟
            if (Locale.getDefault().language == Locale.CHINESE.language && !XPref.getForceEnglishWordClock()) {
                text = "${hour.toCNString()}時\n${minute.toCNString()}分" + "  "
            } else {
                val engStr = "It's\n${hoursArray[hour]}\n${minutesArray[minute]}" + " "
                text = engStr
            }

        })

    }
}