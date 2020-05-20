package com.retrox.aodmod.app.settings.fragments

import android.os.Bundle
import androidx.preference.Preference
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.preference.RadioButtonPreference

class SettingsClockAlignmentFragment : GenericPreferenceFragment(),
    Preference.OnPreferenceChangeListener {

    private val alignmentOptions = arrayOf("SYSTEM", "AlarmManager-1min", "Alarm-TimeOutMode", "Chore")

    private val currentOption: String
        get() = AppPref.aodAlarmMode

    private val systemBroadcast by lazy {
        findPreference<RadioButtonPreference>("system_broadcast")
    }

    private val alarmManager by lazy {
        findPreference<RadioButtonPreference>("alarm_manager")
    }

    private val alarmManagerTimeout by lazy {
        findPreference<RadioButtonPreference>("alarm_manager_timeout")
    }

    private val choreographer by lazy {
        findPreference<RadioButtonPreference>("choreographer")
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_clock_alignment)
        systemBroadcast?.onPreferenceChangeListener = this
        alarmManager?.onPreferenceChangeListener = this
        alarmManagerTimeout?.onPreferenceChangeListener = this
        choreographer?.onPreferenceChangeListener = this
        when(currentOption){
            alignmentOptions[0] -> systemBroadcast?.isChecked = true
            alignmentOptions[1] -> alarmManager?.isChecked = true
            alignmentOptions[2] -> alarmManagerTimeout?.isChecked = true
            alignmentOptions[3] -> choreographer?.isChecked = true
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val preferences = arrayOf(systemBroadcast, alarmManager, alarmManagerTimeout, choreographer)
        preferences.forEach {
            if(it?.key != preference?.key) it?.isChecked = false
        }
        AppPref.aodAlarmMode = when(preference?.key){
            systemBroadcast?.key -> alignmentOptions[0]
            alarmManager?.key -> alignmentOptions[1]
            alarmManagerTimeout?.key -> alignmentOptions[2]
            choreographer?.key -> alignmentOptions[3]
            else -> alignmentOptions[0]
        }
        return true
    }

}