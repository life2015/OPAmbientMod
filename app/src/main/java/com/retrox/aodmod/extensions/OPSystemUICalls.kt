package com.retrox.aodmod.extensions

import android.content.Context
import android.content.res.ApkAssets
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.graphics.Paint
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.graphics.TypefaceCompat
import com.retrox.aodmod.opimports.OpTextClock
import com.retrox.aodmod.util.CustomTypefaceSpan
import com.retrox.aodmod.util.LineHeightSpanStandard
import dalvik.system.PathClassLoader
import org.jetbrains.anko.dip
import org.jetbrains.anko.textColor
import java.lang.reflect.Field
import java.util.*

fun Context.getTextClock(): View {
    val opTextClockClass = Class.forName("com.oneplus.aod.OpCustomTextClock", false, getSystemUiClassLoader())
    val opTextClockConstructor = opTextClockClass.getConstructor(Context::class.java)
    val opSystemUiContext = createPackageContext("com.android.systemui", Context.CONTEXT_IGNORE_SECURITY)
    val applicationInfo = packageManager.getApplicationInfo("com.android.systemui", 0)
    opSystemUiContext.assets.setApkAssets(arrayOf(ApkAssets.loadFromPath(applicationInfo.sourceDir)), true)
    val view = opTextClockConstructor.newInstance(opSystemUiContext) as TextView
    val mTextClockStringTemplate = opTextClockClass.getDeclaredField("mTextClockStringTemplate")
    mTextClockStringTemplate.isAccessible = true
    mTextClockStringTemplate.setInt(view, opSystemUiContext.resources.getIdentifier("textclock_template", "string", "com.android.systemui"))
    val mGradientStyle = opTextClockClass.getDeclaredField("mGradientStyle")
    mGradientStyle.isAccessible = true
    mGradientStyle.setInt(view, 1)
    val mGradientStartColor = opTextClockClass.getDeclaredField("mGradientStartColor")
    mGradientStartColor.isAccessible = true
    mGradientStartColor.setInt(view, opSystemUiContext.getColorByName("op_aod_digitalclock_gradient_start"))
    val mGradientStartEnd = opTextClockClass.getDeclaredField("mGradientEndColor")
    mGradientStartEnd.isAccessible = true
    mGradientStartEnd.setInt(view, opSystemUiContext.getColorByName("op_aod_digitalclock_gradient_end"))
    val mColorTop = opTextClockClass.getDeclaredField("mColorTop")
    mColorTop.isAccessible = true
    mColorTop.setInt(view, opSystemUiContext.getColorByName("op_aod_textclock_top"))
    val mColorBottom = opTextClockClass.getDeclaredField("mColorBottom")
    mColorBottom.isAccessible = true
    mColorBottom.setInt(view, opSystemUiContext.getColorByName("oneplus_contorl_text_color_primary_dark"))
    view.textColor = Color.WHITE
    //view.onTimeChanged()
    opTextClockClass.getMethod("setClockStyle", Integer.TYPE).invoke(view, 4)
    return view
}

fun Context.getRedTextClock(): View {
    val view = OpTextClock(this)
    return view
}

fun Context.getSystemUiClassLoader(): PathClassLoader {
    val applicationInfo = packageManager.getApplicationInfo("com.android.systemui", 0)
    return PathClassLoader(applicationInfo.sourceDir, ClassLoader.getSystemClassLoader())
}

fun Context.getColorByName(name: String): Int {
    return getColor(resources.getIdentifier(name, "color", packageName))
}

fun Context.getLayoutByName(name: String): XmlResourceParser {
    return resources.getLayout(resources.getIdentifier(name, "layout", packageName))
}

private fun TextView.onTimeChanged() {
    val mTime = this.javaClass.getDeclaredField("mTime").setAccessibleR(true).get(this) as Calendar
    val mTextClockStringTemplate = this.javaClass.getDeclaredField("mTextClockStringTemplate").setAccessibleR(true).getInt(this)
    val mColorTop = this.javaClass.getDeclaredField("mColorTop").setAccessibleR(true).getInt(this)
    val mColorBottom = this.javaClass.getDeclaredField("mColorBottom").setAccessibleR(true).getInt(this)
    val mHours = this.javaClass.getDeclaredField("mHours").setAccessibleR(true).get(this) as Array<String>
    val mMinutes = this.javaClass.getDeclaredField("mMinutes").setAccessibleR(true).get(this) as Array<String>
    mTime.setTimeInMillis(System.currentTimeMillis())
    val v0_1: Int = mTime.get(Calendar.HOUR) % 12
    val v2: Int = mTime.get(Calendar.MINUTE) % 60
    val v3 = SpannableString(this.context.getText(context.resources.getIdentifier("textclock_template", "string", "com.android.systemui")))
    val v4 = v3.getSpans(0, v3.length, Annotation::class.java)
    val v5 = v4.size
    var v7: Int
    v7 = 0
    while (v7 < v5) {
        val v8 = v4[v7] as android.text.Annotation
        val v9: String = v8.getValue()
        if ("color" == v9) {
            v3.setSpan(ForegroundColorSpan(mColorTop), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33)
        } else if ("bold" == v9) {
            v3.setSpan(CustomTypefaceSpan(TypefaceCompat.create(this.context, this.paint.typeface, 400)), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33)
            v3.setSpan(ForegroundColorSpan(mColorBottom), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33)
        } else if ("line-height1" == v9) {
            val v9_1: Paint.FontMetrics = this.getPaint().getFontMetrics()
            v3.setSpan(LineHeightSpanStandard((v9_1.descent - v9_1.ascent + dip(9) as Float - (v9_1.ascent - v9_1.top)) as Int), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33)
        } else if ("line-height2" == v9) {
            val v9_2: Paint.FontMetrics = this.getPaint().getFontMetrics()
            v3.setSpan(LineHeightSpanStandard((v9_2.descent - v9_2.ascent + dip(5) as Float - (v9_2.ascent - v9_2.top)) as Int), v3.getSpanStart(v8), v3.getSpanEnd(v8), 33
            )
        }
        ++v7
    }
    this.setText(TextUtils.expandTemplate(v3, mHours.get(v0_1), mMinutes.get(v2)))
    return
}

private fun Field.setAccessibleR(isAccessible: Boolean): Field {
    this.isAccessible = isAccessible
    return this
}