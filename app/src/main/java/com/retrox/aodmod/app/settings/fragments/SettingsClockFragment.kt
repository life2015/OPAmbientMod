package com.retrox.aodmod.app.settings.fragments

import android.content.Intent
import android.os.Bundle
import com.retrox.aodmod.R
import com.retrox.aodmod.app.settings.SettingsClockAlignmentActivity
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.fragments.bottomsheet.DateFormatPickerBottomSheet
import com.retrox.aodmod.extensions.getDateFormatted

class SettingsClockFragment : GenericPreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_clock)
        findSwitchPreference("clock_use_24h"){
            it.isChecked = AppPref.use24h
            it.listen { value ->
                AppPref.use24h = value
                findSwitchPreference("clock_show_am_pm"){ amPmSwitch ->
                    amPmSwitch.isEnabled = !value
                }
            }
        }
        findSwitchPreference("clock_show_am_pm"){
            it.isChecked = AppPref.useAmPm
            it.listen { value ->
                AppPref.useAmPm = value
            }
            it.isEnabled = !AppPref.use24h
        }
        findSwitchPreference("clock_word_clock_flat"){
            it.isChecked = AppPref.forceShowWordClockOnFlat
            it.listen { value ->
                AppPref.forceShowWordClockOnFlat = value
            }
        }
        findSwitchPreference("clock_show_alarm"){
            it.isChecked = AppPref.showAlarm
            it.listen { value ->
                AppPref.showAlarm = value
                findSwitchPreference("clock_show_alarm_emoji"){ emojiSwitch ->
                    emojiSwitch.isEnabled = value
                }
            }
        }
        findSwitchPreference("clock_show_alarm_emoji"){
            it.isChecked = AppPref.showAlarmEmoji
            it.listen { value ->
                AppPref.showAlarmEmoji = value
            }
            it.isEnabled = AppPref.showAlarm
        }
        findPreference("clock_date_format_string"){
            it.summary = AppPref.dateFormat
            it.summary = getDateFormatted(AppPref.dateFormat)
            it.setOnPreferenceClickListener {
                DateFormatPickerBottomSheet().apply {
                    dismissListener = {
                        it.summary = getDateFormatted(AppPref.dateFormat)
                    }
                }.show(childFragmentManager, "bs_date_format")
                true
            }
        }
        findPreference("clock_advanced_sync"){
            it.setOnPreferenceClickListener {
                startActivity(Intent(context, SettingsClockAlignmentActivity::class.java))
                true
            }
        }
    }

}