package com.retrox.aodmod.extensions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.os.PowerManager
import android.support.v4.app.ActivityCompat
import android.widget.TextView
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.view.theme.ThemeClockPack
import com.retrox.aodmod.proxy.view.theme.ThemeManager

fun TextView.setGoogleSans(style: String = "Regular"): Boolean {
    if (XPref.getFontWithSystem()) return false // 跟随系统字体
    typeface = Typeface.createFromAsset(ResourceUtils.getInstance(context).assets, "fonts/GoogleSans-$style.ttf")
    return true
}

fun Context.checkPermission(callback: () -> Unit): Boolean {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        callback()
        return false
    } else return true
}

/**
 * 使用WakeLock来保证一些操作的正确执行
 */
fun Context.wakeLockWrap(tag: String, block: () -> Unit) {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag)
    wakeLock.acquire(2000L)
    block()
    wakeLock.release()
}

fun TextView.setGradientTest(colorPack: ThemeClockPack = ThemeManager.getCurrentColorPack()) {
    val shader = LinearGradient(
        0f, 0f, width.toFloat() * 1.2f, height.toFloat() * 1.2f, Color.parseColor(colorPack.gradientStart),Color.parseColor(colorPack.gradientEnd),Shader.TileMode.CLAMP)
    paint.shader = shader
//    MainHook.logD("Debug Gradient -> text: $text width: $width height: $height")
}