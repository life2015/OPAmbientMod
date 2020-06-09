package com.retrox.aodmod.app.settings

import androidx.appcompat.app.AppCompatActivity
import com.retrox.aodmod.app.settings.fragments.GenericPreferenceFragment

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

}