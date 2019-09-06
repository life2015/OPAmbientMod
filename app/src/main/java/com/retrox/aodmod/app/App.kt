package com.retrox.aodmod.app

import android.app.Application
import android.content.SharedPreferences
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.shared.global.BaseFileManager
import com.retrox.aodmod.shared.global.GlobalCacheManager
import com.retrox.aodmod.shared.global.GlobalKV
import com.retrox.aodmod.shared.global.OwnFileManager
import org.jetbrains.anko.defaultSharedPreferences
import java.lang.ref.WeakReference
import kotlin.concurrent.thread

class App : Application() {
    companion object {
        var applicationReference: WeakReference<Application>? = null

        val application: Application
            get() = applicationReference?.get()
                ?: throw IllegalStateException("Application should be registered in CommonContext.")

        val applicationVersion: String by lazy {
            application.packageManager.getPackageInfo(application.packageName, 0).versionName
        }

        // context.getPackageName() + "_preferences"
        val defaultSharedPreferences: SharedPreferences by lazy { application.defaultSharedPreferences }
    }

    override fun onCreate() {
        super.onCreate()
        applicationReference = WeakReference(this)
        AppPref.setWorldReadable()
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            thread {
                // 强制等待一下apply写操作完成
                val queueWorkClazz = Class.forName("android.app.QueuedWork")
                val method = queueWorkClazz.getMethod("waitToFinish")
                method.invoke(null)
                AppPref.flushPrefChangeToSDcard()
            }
        }

        BaseFileManager.makeCacheFileDir()
        OwnFileManager.getOwnFileDir()
        GlobalCacheManager.getCacheFileDir()
        GlobalKV.getKVFileDir()

    }
}