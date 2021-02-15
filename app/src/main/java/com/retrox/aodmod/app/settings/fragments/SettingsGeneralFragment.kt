package com.retrox.aodmod.app.settings.fragments

import android.content.Intent
import android.os.Bundle
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.SettingsBatteryOptimisationActivity
import com.retrox.aodmod.app.settings.SettingsSensitiveAppPickerActivity
import com.retrox.aodmod.app.settings.fragments.bottomsheet.MemoSettingBottomSheetFragment
import com.retrox.aodmod.extensions.runAfter
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

class SettingsGeneralFragment : GenericPreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_general)
        setupPreferences()
    }

    private fun setupPreferences(){
        findSwitchPreference("general_raise_detection"){
            it.isChecked = AppPref.aodPickCheck
            it.listen { value ->
                AppPref.aodPickCheck = value
            }
        }
        findSwitchPreference("general_flip_mode"){
            it.isChecked = AppPref.filpOffScreen
            it.listen { value ->
                AppPref.filpOffScreen = value
            }
        }
        findSwitchPreference("general_night_mode"){
            it.isChecked = AppPref.autoCloseByNightMode
            it.listen { value ->
                AppPref.autoCloseByNightMode = value
            }
        }
        findSwitchPreference("general_auto_brightness"){
            it.isChecked = AppPref.autoBrightness
            it.listen { value ->
                AppPref.autoBrightness = value
            }
        }
        findPreference("general_battery_optimisation"){
            it.setOnPreferenceClickListener {
                startActivity(Intent(context, SettingsBatteryOptimisationActivity::class.java))
                true
            }
        }
        findSwitchPreference("general_sensitive_notifications"){
            it.isChecked = !AppPref.aodShowSensitiveContent
            it.listen { value ->
                AppPref.aodShowSensitiveContent = !value
            }
        }
        findPreference("general_sensitive_notifications_pick"){
            it.setOnPreferenceClickListener {
                startActivity(Intent(context, SettingsSensitiveAppPickerActivity::class.java))
                true
            }
        }
        findSwitchPreference("general_system_font"){
            it.isChecked = !AppPref.fontWithSystem
            it.listen { value ->
                AppPref.fontWithSystem = !value
            }
        }
        findSwitchPreference("general_ten_minutes"){
            it.isChecked = AppPref.autoCloseAfterHour
            it.listen { value ->
                AppPref.autoCloseAfterHour = value
            }
        }
        findSwitchPreference("general_turn_off_after_fifteen_seconds"){
            it.isChecked = AppPref.autoCloseBySeconds
            it.listen { value ->
                AppPref.autoCloseBySeconds = value
            }
        }
        findSwitchPreference("general_force_english"){
            it.isChecked = AppPref.forceEnglishWordClock
            it.listen { value ->
                AppPref.forceEnglishWordClock = value
            }
        }
        findSwitchPreference("general_hide_divider"){
            it.isVisible = AppPref.aodLayoutTheme == "Default"
            it.isChecked = AppPref.hideDivider
            it.listen { value ->
                AppPref.hideDivider = value
            }
        }
        findPreference("general_aod_memo"){
            it.setOnPreferenceClickListener {
                MemoSettingBottomSheetFragment().show(childFragmentManager, "bs_aod_memo")
                true
            }
        }
    }

    override fun onLayoutChanged(newLayout: String) {
        super.onLayoutChanged(newLayout)
        setupPreferences()
        runAfter(0.5){
            checkNestedScroll()
        }
    }

}