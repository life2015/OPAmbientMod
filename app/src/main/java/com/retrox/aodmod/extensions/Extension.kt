package com.retrox.aodmod.extensions

import android.graphics.Typeface
import android.widget.TextView
import com.retrox.aodmod.pref.XPref

fun TextView.setGoogleSans(style: String = "Regular"): Boolean {
    if (XPref.getFontWithSystem()) return false // 跟随系统字体
    typeface = Typeface.createFromAsset(ResourceUtils.getInstance(context).assets, "fonts/GoogleSans-$style.ttf")
    return true
}