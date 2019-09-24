package com.retrox.aodmod.apple

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.retrox.aodmod.MainHook

/**
 * Created by rickygao on 2017/11/14.
 */
@SuppressLint("PrivateApi")
fun Activity.enableLightStatusBarMode(enable: Boolean): String {

    // for MIUI
    try {
        val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
        val darkModeFlag =
            layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(layoutParams)
        val extraFlagField =
            window::class.java.getMethod("setExtraFlags", Int::class.java, Int::class.java)
        extraFlagField.invoke(window, if (enable) darkModeFlag else 0, darkModeFlag)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                if (enable) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else View.SYSTEM_UI_FLAG_VISIBLE
        }
        return "MIUI"
    } catch (e: Exception) {

    }

    // for Flyme
    try {
        val layoutParams = window.attributes
        val darkFlag =
            WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
        val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
        darkFlag.isAccessible = true
        meizuFlags.isAccessible = true
        val bit = darkFlag.getInt(null)
        var value = meizuFlags.getInt(layoutParams)
        value = if (enable) value or bit else value and bit.inv()
        meizuFlags.setInt(layoutParams, value)
        window.attributes = layoutParams
        return "Flyme"
    } catch (e: Exception) {

    }

    // for Android M
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility =
            if (enable) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else View.SYSTEM_UI_FLAG_VISIBLE
        return "Android M"
    }

    return "None"
}

fun Activity.setAndroidQImmersive() {
    window.navigationBarColor = Color.TRANSPARENT
    // 这里要提取出应用本身对于状态栏图标底色的适配，同时把它保留，顺便应用到底栏上
    val flag = if (window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0) {
        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    } else 0
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or flag
}

fun View.getId(name: String) =
    this.context.resources.getIdentifier(name, "id", context.applicationContext.packageName)

fun View.XFindViewById(id: String): View? {
    val realId = getId(id)
    return findViewById<View>(realId)
}


inline val Activity.statusBarHeight
    get() = resources.getIdentifier("status_bar_height", "dimen", "android")
        .takeIf { it > 0 }?.let {
            resources.getDimensionPixelSize(it)
        } ?: 0

inline val Activity.navigationBarHeight
    get() = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        .takeIf { it > 0 }?.let {
            resources.getDimensionPixelSize(it)
        } ?: 0

fun Activity.fitSystemWindowWithStatusBar(view: View) = with(view) {
    setPadding(paddingLeft, paddingTop + statusBarHeight, paddingRight, paddingBottom)
    if (layoutParams.height > 0)
        layoutParams.height += statusBarHeight
}

fun Activity.fitSystemWindowWithNavigationBar(view: View) = with(view) {
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + navigationBarHeight)
    if (layoutParams.height > 0)
        layoutParams.height += navigationBarHeight
}
