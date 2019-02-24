package com.retrox.aodmod

import android.app.Application
import android.content.SharedPreferences
import org.jetbrains.anko.defaultSharedPreferences
import java.lang.ref.WeakReference

class App : Application() {
    companion object {
        private var applicationReference: WeakReference<Application>? = null

        val application: Application
            get() = applicationReference?.get()
                ?: throw IllegalStateException("Application should be registered in CommonContext.")

        val applicationVersion: String by lazy {
            application.packageManager.getPackageInfo(application.packageName, 0).versionName
        }

        val defaultSharedPreferences: SharedPreferences by lazy { application.defaultSharedPreferences }
    }

    override fun onCreate() {
        super.onCreate()
        applicationReference = WeakReference(this)
    }
}