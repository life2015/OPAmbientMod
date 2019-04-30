package com.retrox.aodmod.apple

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import android.view.WindowManager
import com.retrox.aodmod.MainHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.jetbrains.anko.applyRecursively
import org.jetbrains.anko.backgroundColor

object AppleMusicHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.apple.android.music") return

        val backup = "com.apple.android.music.library.activities.LibraryActivity"
        val clazz = XposedHelpers.findClass("android.app.Activity", lpparam.classLoader)
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val activity = param.thisObject as Activity

//                activity.window.statusBarColor = Color.WHITE
                activity.enableLightStatusBarMode(true)
                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.statusBarColor = Color.WHITE

                MainHook.logD("Apple Music onCreate : ")

                val coor = activity.window.decorView


//                val statusBarBack = activity.window.findViewById<View>(android.R.id.statusBarBackground)
//                statusBarBack.backgroundColor = Color.WHITE

            }
        })

    }

}