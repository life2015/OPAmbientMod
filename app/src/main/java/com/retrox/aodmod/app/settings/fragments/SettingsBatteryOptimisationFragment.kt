package com.retrox.aodmod.app.settings.fragments

import android.os.Bundle
import androidx.preference.Preference
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.preference.RadioButtonPreference

class SettingsBatteryOptimisationFragment : GenericPreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_battery_optimisation)
        findSwitchPreference("settings_battery_force_doze"){
            it.isChecked = AppPref.batteryEnableForceDoze
            it.listen { enabled ->
                AppPref.batteryEnableForceDoze = enabled
            }
        }
        findSwitchPreference("settings_battery_enable_saver"){
            it.isChecked = AppPref.batteryEnableBatterySaver
            it.listen { enabled ->
                AppPref.batteryEnableBatterySaver = enabled
            }
        }
        findSwitchPreference("settings_lower_refresh_rate"){
            it.isChecked = AppPref.batteryLowerRefreshRate
            it.listen { enabled ->
                AppPref.batteryLowerRefreshRate = enabled
            }
        }
        findSwitchPreference("settings_lower_brightness"){
            it.isChecked = AppPref.batteryLowestBrightness
            it.listen { enabled ->
                AppPref.batteryLowestBrightness = enabled
            }
        }
    }

}