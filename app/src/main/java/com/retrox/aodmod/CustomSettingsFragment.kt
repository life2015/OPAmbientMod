package com.retrox.aodmod

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import org.jetbrains.anko.defaultSharedPreferences
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CustomSettingsFragment : PreferenceFragmentCompat() {
    
    companion object {
        var KEY_DATE_FORMAT = "DATE_FORMAT"
        var DEFAULT_DATE_FORMAT = "EEE, d MMM"
        var KEY_24_HOUR = "USE_24_HOUR"
        var KEY_USE_AM_PM = "USE_AM_PM_MARKER"
        var KEY_WEATHER_SHOW_SYMBOL = "WEATHER_SHOW_SYMBOL"
        var KEY_WEATHER_SHOW_CONDITION = "WEATHER_SHOW_CONDITION"
        var KEY_WEATHER_SHOW_TEMPERATURE = "WEATHER_SHOW_TEMPERATURE"
        var KEY_WEATHER_SHOW_CITY = "KEY_WEATHER_SHOW_CITY"
        var KEY_SHOW_ALARM = "KEY_SHOW_ALARM"
        var KEY_SHOW_ALARM_EMOJI = "KEY_SHOW_ALARM_EMOJI"
        var KEY_SHOW_BULLETS = "KEY_SHOW_BULLETS"
        var KEY_USE_SYSTEM_MUSIC = "USE_SYSTEM_MUSIC"
        var KEY_USE_PIXEL_MUSIC_ICON = "USE_PIXEL_MUSIC_ICON"
    }


    private var sharedPreferences: SharedPreferences? = null
    private val dateFormats =
        arrayOf("EEE, d MMM", "EEE, MMM d", "dd/MM/y", "MM/dd/y", "d/M/y", "M/d/y")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val activity = this.activity as? AppCompatActivity ?: return
        sharedPreferences = activity.defaultSharedPreferences
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = getString(R.string.custom_settings_title)
        val screen = preferenceManager.createPreferenceScreen(activity)
        val infoPreference = Preference(activity)
        infoPreference.title = getString(R.string.custom_settings_info_title)
        infoPreference.summary =
            getString(R.string.custom_settings_info_desc)
        screen.addPreference(infoPreference)
        val generalCategory = PreferenceCategory(activity)
        val addPreference = screen.addPreference(generalCategory)
        generalCategory.title = getString(R.string.custom_settings_general)
        val dateFormat = ListPreference(activity)
        dateFormat.entries = arrayOf<CharSequence>(
            getDateFormatted(dateFormats[0]),
            getDateFormatted(dateFormats[1]),
            getDateFormatted(dateFormats[2]),
            getDateFormatted(dateFormats[3]),
            getString(R.string.custom_settings_date_format_no_leading, getDateFormatted(dateFormats[4])),
            getString(R.string.custom_settings_date_format_no_leading, getDateFormatted(dateFormats[5]))
        )
        dateFormat.entryValues = dateFormats
        dateFormat.value = getStringPreference(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT)
        dateFormat.summary = getDateFormatted(
            getStringPreference(
                KEY_DATE_FORMAT,
                DEFAULT_DATE_FORMAT
            )
        )
        dateFormat.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as String?
                sharedPreferences?.edit()?.putString(KEY_DATE_FORMAT, newValue)?.commit()
                dateFormat.summary = getDateFormatted(
                    getStringPreference(
                        KEY_DATE_FORMAT,
                        DEFAULT_DATE_FORMAT
                    )
                )
                true
            }
        dateFormat.key = KEY_DATE_FORMAT
        dateFormat.dialogTitle = getString(R.string.custom_settings_date_format)
        dateFormat.title = getString(R.string.custom_settings_date_format)
        generalCategory.addPreference(dateFormat)
        val useTwentyFourHour = SwitchPreference(activity)
        val showAmPm = SwitchPreference(activity)
        useTwentyFourHour.title = getString(R.string.custom_settings_24_h_clock)
        useTwentyFourHour.summary = getString(R.string.custom_settings_24_h_clock_desc)
        useTwentyFourHour.isChecked = getBooleanPreference(KEY_24_HOUR, true)
        useTwentyFourHour.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                showAmPm.isEnabled = !newValue!!
                sharedPreferences?.edit()?.putBoolean(KEY_24_HOUR, newValue)?.commit()
                true
            }
        generalCategory.addPreference(useTwentyFourHour)
        showAmPm.title = getString(R.string.custom_settings_show_am_pm)
        showAmPm.summary = getString(R.string.custom_settings_show_am_pm_desc)
        showAmPm.isChecked = getBooleanPreference(KEY_USE_AM_PM, false)
        showAmPm.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()?.putBoolean(KEY_USE_AM_PM, newValue!!)?.commit()
                true
            }
        showAmPm.isEnabled = !getBooleanPreference(KEY_24_HOUR, true)
        generalCategory.addPreference(showAmPm)
        val showBullets = SwitchPreference(activity)
        showBullets.title = getString(R.string.custom_settings_show_bullets)
        showBullets.summary =
            getString(R.string.custom_settings_show_bullets_desc)
        showBullets.isChecked = getBooleanPreference(KEY_SHOW_BULLETS, true)
        showBullets.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()?.putBoolean(KEY_SHOW_BULLETS, newValue!!)?.commit()
                true
            }
        generalCategory.addPreference(showBullets)
        val weatherCategory = PreferenceCategory(activity)
        screen.addPreference(weatherCategory)
        weatherCategory.title = getString(R.string.custom_settings_weather)
        val showIcon = SwitchPreference(activity)
        showIcon.title = getString(R.string.custom_settings_weather_condition)
        showIcon.summary =
            getString(R.string.custom_settings_weather_condition_desc)
        showIcon.isChecked = getBooleanPreference(KEY_WEATHER_SHOW_SYMBOL, true)
        showIcon.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()?.putBoolean(KEY_WEATHER_SHOW_SYMBOL, newValue!!)
                    ?.commit()
                true
            }
        weatherCategory.addPreference(showIcon)
        val showCondition = SwitchPreference(activity)
        showCondition.title = getString(R.string.custom_settings_weather_condition_text)
        showCondition.summary = getString(R.string.custom_settings_weather_condition_text_desc)
        showCondition.isChecked = getBooleanPreference(KEY_WEATHER_SHOW_CONDITION, true)
        showCondition.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()?.putBoolean(KEY_WEATHER_SHOW_CONDITION, newValue!!)
                    ?.commit()
                true
            }
        weatherCategory.addPreference(showCondition)
        val showTemperature = SwitchPreference(activity)
        showTemperature.title = getString(R.string.custom_settings_temperature)
        showTemperature.summary =
            getString(R.string.custom_settings_temperature_desc)
        showTemperature.isChecked = getBooleanPreference(KEY_WEATHER_SHOW_TEMPERATURE, true)
        showTemperature.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()
                    ?.putBoolean(KEY_WEATHER_SHOW_TEMPERATURE, newValue!!)?.commit()
                true
            }
        weatherCategory.addPreference(showTemperature)
        val showCity = SwitchPreference(activity)
        showCity.title = getString(R.string.custom_settings_city)
        showCity.summary =
            getString(R.string.custom_settings_city_desc)
        showCity.isChecked = getBooleanPreference(KEY_WEATHER_SHOW_CITY, false)
        showCity.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()?.putBoolean(KEY_WEATHER_SHOW_CITY, newValue!!)
                    ?.commit()
                true
            }
        weatherCategory.addPreference(showCity)
        val alarmCategory = PreferenceCategory(activity)
        screen.addPreference(alarmCategory)
        alarmCategory.title = getString(R.string.custom_settings_alarm)
        val showAlarm = SwitchPreference(activity)
        val showAlarmEmoji = SwitchPreference(activity)
        showAlarm.title = getString(R.string.custom_settings_next_alarm)
        showAlarm.summary =
            getString(R.string.custom_settings_next_alarm_desc)
        showAlarm.isChecked = getBooleanPreference(KEY_SHOW_ALARM, false)
        showAlarm.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()?.putBoolean(KEY_SHOW_ALARM, newValue!!)?.commit()
                showAlarmEmoji.isEnabled = newValue ?: false
                true
            }
        alarmCategory.addPreference(showAlarm)
        showAlarmEmoji.title = getString(R.string.custom_settings_alarm_emoji)
        showAlarmEmoji.summary = getString(R.string.custom_settings_alarm_emoji_desc)
        showAlarmEmoji.isChecked = getBooleanPreference(KEY_SHOW_ALARM_EMOJI, true)
        showAlarmEmoji.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()?.putBoolean(KEY_SHOW_ALARM_EMOJI, newValue!!)
                    ?.commit()
                true
            }
        showAlarmEmoji.isEnabled =
            sharedPreferences?.getBoolean(KEY_SHOW_ALARM, false) ?: false
        alarmCategory.addPreference(showAlarmEmoji)
        val musicCategory = PreferenceCategory(context)
        screen.addPreference(musicCategory)
        musicCategory.title = getString(R.string.custom_settings_music)
        val musicUseSystem = SwitchPreference(context)
        musicUseSystem.title = getString(R.string.custom_settings_system_music)
        musicUseSystem.summary = getString(R.string.custom_settings_system_music_desc)
        musicUseSystem.setOnPreferenceChangeListener { preference, o ->
            val newValue = o as Boolean?
            sharedPreferences?.edit()?.putBoolean(KEY_USE_SYSTEM_MUSIC, newValue!!)
                ?.commit()
            true
        }
        musicUseSystem.isChecked = getBooleanPreference(KEY_USE_SYSTEM_MUSIC, false)
        musicCategory.addPreference(musicUseSystem)
        val musicPixelIcon = SwitchPreference(context)
        musicPixelIcon.title = getString(R.string.custom_settings_pixel_music_icon)
        musicPixelIcon.summary = getString(R.string.custom_settings_pixel_music_icon_desc)
        musicPixelIcon.setOnPreferenceChangeListener { preference, o ->
            val newValue = o as Boolean?
            sharedPreferences?.edit()?.putBoolean(KEY_USE_PIXEL_MUSIC_ICON, newValue!!)
                ?.commit()
            true
        }
        musicPixelIcon.isChecked = getBooleanPreference(KEY_USE_PIXEL_MUSIC_ICON, false)
        musicCategory.addPreference(musicPixelIcon)
        preferenceScreen = screen
    }

    public override fun onPause() {
        super.onPause()
        //Make prefs world readable
        val file = File(
            activity?.filesDir?.parent + "/shared_prefs/",
            context?.packageName + "_preferences.xml"
        )
        try {
            Runtime.getRuntime().exec("chmod 777 " + file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getDateFormatted(dateFormat: String): String {
        return SimpleDateFormat(dateFormat, Locale.ENGLISH)
            .format(Date())
    }

    private fun getStringPreference(
        preferenceKey: String,
        defaultValue: String
    ): String {
        return sharedPreferences!!.getString(preferenceKey, defaultValue)!!
    }

    private fun getBooleanPreference(
        preferenceKey: String,
        defaultValue: Boolean
    ): Boolean {
        return sharedPreferences!!.getBoolean(preferenceKey, defaultValue)
    }

}