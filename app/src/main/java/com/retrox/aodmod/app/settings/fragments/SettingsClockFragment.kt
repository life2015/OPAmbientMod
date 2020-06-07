package com.retrox.aodmod.app.settings.fragments

import android.content.Intent
import android.os.Bundle
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.SettingsClockAlignmentActivity
import com.retrox.aodmod.app.settings.fragments.bottomsheet.DateFormatPickerBottomSheet
import com.retrox.aodmod.app.settings.preference.PreferenceCategory
import com.retrox.aodmod.extensions.getDateFormatted
import com.retrox.aodmod.extensions.runAfter
import com.retrox.aodmod.proxy.view.theme.ThemeManager

class SettingsClockFragment : GenericPreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_clock)
        setupPreferences()
    }

    private fun setupPreferences(){
        val currentTheme = AppPref.aodLayoutTheme
        val isClockSupported = ThemeManager.doesThemeSupportClock(currentTheme)
        val isDateSupported = ThemeManager.doesThemeSupportDate(currentTheme)
        val isTodaySupported = ThemeManager.doesThemeSupportToday(currentTheme)
        findPreferenceCategory("category_clock_style"){
            it.isVisible = AppPref.aodLayoutTheme == "OnePlus"
        }
        findPreferenceCategory("category_clock_general"){
            it.isVisible = isClockSupported
        }
        if(AppPref.aodLayoutTheme == "OnePlus") {
            findDropdownPreference("oneplus_clock_style"){
                val entries = arrayOf("0", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
                it.entryValues = entries
                it.summary = it.entries[entries.indexOf(AppPref.onePlusClockStyle.toString())]
                it.listen { style ->
                    AppPref.onePlusClockStyle = style.toInt()
                    it.summary = it.entries[it.entryValues.indexOf(style)]
                }
            }
        }
        findSwitchPreference("clock_use_24h"){
            it.isChecked = AppPref.use24h
            it.isVisible = isClockSupported
            it.listen { value ->
                AppPref.use24h = value
                findSwitchPreference("clock_show_am_pm"){ amPmSwitch ->
                    amPmSwitch.isEnabled = !value
                }
            }
        }
        findSwitchPreference("clock_show_am_pm"){
            it.isChecked = AppPref.useAmPm
            it.isVisible = isClockSupported
            it.listen { value ->
                AppPref.useAmPm = value
            }
            it.isEnabled = !AppPref.use24h
        }
        findSwitchPreference("clock_word_clock_flat"){
            it.isChecked = AppPref.forceShowWordClockOnFlat
            it.isVisible = AppPref.aodLayoutTheme == "Flat"
            it.listen { value ->
                AppPref.forceShowWordClockOnFlat = value
            }
        }
        findSwitchPreference("clock_show_bullets"){
            it.isChecked = AppPref.showBullets
            it.isVisible = isTodaySupported
            it.listen { value ->
                AppPref.showBullets = value
            }
        }
        findPreferenceCategory("category_clock_alarm"){
            it.isVisible = isTodaySupported
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
            it.isVisible = isDateSupported
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
        findPreferenceCategory("category_clock_advanced"){
            it.isVisible = isClockSupported
        }
        findPreference("clock_advanced_sync"){
            it.setOnPreferenceClickListener {
                startActivity(Intent(context, SettingsClockAlignmentActivity::class.java))
                true
            }
        }
        findPreference("generic_theme_unsupported"){
            it.isVisible = !isClockSupported && !isDateSupported && !isTodaySupported
        }
    }

    override fun onResume() {
        super.onResume()
        listView.post {
            listView.scrollToPosition(0)
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