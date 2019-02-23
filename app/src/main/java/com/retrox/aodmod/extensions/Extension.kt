package com.retrox.aodmod.extensions

import android.content.res.Resources
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.widget.TextView

fun TextView.setGoogleSans(style: String = "Regular"): Boolean {
    typeface = Typeface.createFromAsset(ResourceUtils.getInstance(context).assets, "fonts/GoogleSans-$style.ttf")
    return true
}