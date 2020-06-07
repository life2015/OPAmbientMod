package com.retrox.aodmod.app.settings

import android.content.res.ApkAssets
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.retrox.aodmod.app.settings.fragments.GenericPreferenceFragment
import dalvik.system.PathClassLoader

abstract class BaseSettingsActivity : AppCompatActivity() {

    abstract fun loadPreview(
        overriddenTheme: String? = null,
        updateHeight: Boolean = false,
        currentFragment: GenericPreferenceFragment? = null,
        disableAnimation: Boolean = false
    )

    abstract fun setToolbarElevationEnabled(enabled: Boolean)

    open fun getBottomNavigationTop() : Int {
        return 0
    }

    private var isSystemUiAssets = false
    private var cachedAppAssets : Array<ApkAssets>? = null

}