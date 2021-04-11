package com.retrox.aodmod

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.retrox.aodmod.BuildConfig.DEBUG
import com.retrox.aodmod.app.settings.models.NowPlayingAmbientTrack
import com.retrox.aodmod.apple.AppleMusicHook
import com.retrox.aodmod.apple.NetEaseQHook
import com.retrox.aodmod.extensions.isOP7Pro
import com.retrox.aodmod.hooks.*
import com.retrox.aodmod.music.AmbientMusicPlugin
import com.retrox.aodmod.proxy.DreamProxyHook
import com.retrox.aodmod.proxy.OP7DreamProxyHook
import com.retrox.aodmod.service.notification.BubbleController
import com.retrox.aodmod.shared.SharedLogger
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.Exception

object MainHook : IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    const val PACKAGE_ANDROID = "android"
    const val PACKAGE_SYSTEMUI = "com.android.systemui"
    const val PACKAGE_SETTINGS = "com.android.settings"
    const val PACKAGE_AOD = "com.oneplus.aod"
    const val TAG = "AODMOD"
    const val PACKAGE_OWN = "com.retrox.aodmod"


    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
//        SystemServiceHook.initZygote(startupParam)
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.d("AODMOD", "package: ${lpparam.packageName}")
//        ProxyInitHook.handleLoadPackage(lpparam)

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            AodLayoutSourceHook.handleLoadPackage(lpparam)
            AodDurationHook.handleLoadPackage(lpparam)
            DisplayStateHook.handleLoadPackage(lpparam)
            AodMainMediaHook.handleLoadPackage(lpparam)
            AodFingerPrintHook.handleLoadPackage(lpparam) // 理论上支持6T
        } else {
            AodFingerPrintHookForQ.handleLoadPackage(lpparam)
            PermissionHook.handleLoadPackage(lpparam) // Q
        }

        MediaControl.handleLoadPackage(lpparam)
//        AppleMusicHook.handleLoadPackage(lpparam)
        if (BuildConfig.DEBUG) {
//            NetEaseQHook.handleLoadPackage(lpparam)
        }

        if (isOP7Pro()) {
            OP7DreamProxyHook.handleLoadPackage(lpparam)
//            NotiLrcHook.handleLoadPackage(lpparam)
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                SystemServiceHook.handleLoadPackage(lpparam)
            }
        } else {
            DreamProxyHook.handleLoadPackage(lpparam)
        }


//        DreamLifeCycleHook.handleLoadPackage(lpparam)

        if(lpparam.packageName == BuildConfig.APPLICATION_ID){
            //Self hooking for EdXposed check
            XposedHelpers.findAndHookMethod("com.retrox.aodmod.app.XposedUtils", lpparam.classLoader,"isEdXposedModuleActive", object: XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    //Just return true
                    param?.result = true
                    return true
                }
            })
        }

        if(lpparam.packageName == PACKAGE_SYSTEMUI){
            XposedHelpers.findAndHookMethod(Application::class.java, "onCreate", object: XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    registerNowPlayingReceiver(param.thisObject as Context)
                }
            })

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                try {
                    val bubbleControllerClass = XposedHelpers.findClass("com.android.systemui.bubbles.BubbleController", lpparam.classLoader)
                    val notificationEntryClass = XposedHelpers.findClass("com.android.systemui.statusbar.notification.collection.NotificationEntry", lpparam.classLoader)
                    BubbleController.notificationEntryClass = notificationEntryClass
                    val constructor = bubbleControllerClass.constructors.firstOrNull() ?: return
                    XposedHelpers.findAndHookConstructor(bubbleControllerClass, *constructor.parameterTypes, object: XC_MethodHook(){
                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                            Log.d("OPAodModB", "BubbleController INIT")
                            BubbleController.bubbleController = param.thisObject
                        }
                    })
                }catch (e: Exception){
                    //Bubble suppressing won't work
                }
            }
        }

    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {}


    private const val LOG_FORMAT = "[OnePlus AOD MOD] %1\$s %2\$s: %3\$s"

    fun logE(msg: String, tag: String = TAG, t: Throwable? = null) {
        XposedBridge.log(String.format(LOG_FORMAT, "[ERROR]", tag, msg))
        t?.let { XposedBridge.log(it) }
        SharedLogger.writeLog(String.format(LOG_FORMAT, "[ERROR]", tag, msg) + t?.message)
//        Log.e(tag, msg, t)
    }

    fun logW(msg: String, tag: String = TAG) {
        XposedBridge.log(String.format(LOG_FORMAT, "[WARNING]", tag, msg))
//        Log.w(tag, msg)

    }

    fun logI(msg: String, tag: String = TAG) {
        XposedBridge.log(String.format(LOG_FORMAT, "[INFO]", tag, msg))
//        Log.i(tag, msg)

    }

    fun logD(msg: String, tag: String = TAG) {
        val message = String.format(LOG_FORMAT, "[DEBUG]", tag, msg)
        SharedLogger.writeLog(message)
        XposedBridge.log(message)
        Log.d(tag, msg)
    }

    private fun registerNowPlayingReceiver(context: Context){
        Log.d("OPAodModNP", "Registering Now Playing receivers")
        AmbientMusicPlugin().setupReceivers(context)
    }

}