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
    }


    private var sharedPreferences: SharedPreferences? = null
    private val dateFormats =
        arrayOf("EEE, d MMM", "EEE, MMM d", "dd/MM/y", "MM/dd/y")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val activity = this.activity as? AppCompatActivity ?: return
        sharedPreferences = activity.defaultSharedPreferences
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "Custom Settings"
        val screen = preferenceManager.createPreferenceScreen(activity)
        val infoPreference = Preference(activity)
        infoPreference.title = "What's this?"
        infoPreference.summary =
            "Custom Settings, fixes, mod and translation by Kieron Quinn / Quinny899 @ XDA. All options not guaranteed to work on all devices and configurations."
        screen.addPreference(infoPreference)
        val generalCategory = PreferenceCategory(activity)
        screen.addPreference(generalCategory)
        generalCategory.title = "General"
        val dateFormat = ListPreference(activity)
        dateFormat.entries = arrayOf<CharSequence>(
            getDateFormatted(dateFormats[0]),
            getDateFormatted(dateFormats[1]),
            getDateFormatted(dateFormats[2]),
            getDateFormatted(dateFormats[3])
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
        dateFormat.dialogTitle = "Date Format"
        dateFormat.title = "Date Format"
        generalCategory.addPreference(dateFormat)
        val useTwentyFourHour = SwitchPreference(activity)
        val showAmPm = SwitchPreference(activity)
        useTwentyFourHour.title = "24 hour clock"
        useTwentyFourHour.summary = "Use a 24 hour clock on the AoD"
        useTwentyFourHour.isChecked = getBooleanPreference(KEY_24_HOUR, true)
        useTwentyFourHour.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                showAmPm.isEnabled = !newValue!!
                sharedPreferences?.edit()?.putBoolean(KEY_24_HOUR, newValue)?.commit()
                true
            }
        generalCategory.addPreference(useTwentyFourHour)
        showAmPm.title = "Show AM/PM"
        showAmPm.summary = "Show AM/PM text when using 12 hour (looks pretty broken)"
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
        showBullets.title = "Show Bullet Symbols"
        showBullets.summary =
            "Show bullet point symbols • between the date and weather and weather city (if enabled) and alarm"
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
        weatherCategory.title = "Weather"
        val showIcon = SwitchPreference(activity)
        showIcon.title = "Show weather condition icon"
        showIcon.summary =
            "Show an icon before the weather condition text in the form of an emoji (eg. ☁️)"
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
        showCondition.title = "Show weather condition text"
        showCondition.summary = "Show the weather condition as text (eg. Cloudy)"
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
        showTemperature.title = "Show temperature"
        showTemperature.summary =
            "Show the current temperature (unit can be changed in the OnePlus weather app)"
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
        showCity.title = "Show city"
        showCity.summary =
            "Show the current weather city from the auto location (shows on a second line)"
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
        alarmCategory.title = "Alarm"
        val showAlarm = SwitchPreference(activity)
        val showAlarmEmoji = SwitchPreference(activity)
        showAlarm.title = "Show next alarm"
        showAlarm.summary =
            "Show the next alarm after the weather. Only shows alarms within the next 24h (shows on a second line)"
        showAlarm.isChecked = getBooleanPreference(KEY_SHOW_ALARM, false)
        showAlarm.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference: Preference?, o: Any? ->
                val newValue = o as Boolean?
                sharedPreferences?.edit()?.putBoolean(KEY_SHOW_ALARM, newValue!!)?.commit()
                showAlarmEmoji.isEnabled = newValue ?: false
                true
            }
        alarmCategory.addPreference(showAlarm)
        showAlarmEmoji.title = "Show alarm emoji"
        showAlarmEmoji.summary = "Show alarm emoji before the next alarm time"
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
        return sharedPreferences!!.getString(preferenceKey, defaultValue)
    }

    private fun getBooleanPreference(
        preferenceKey: String,
        defaultValue: Boolean
    ): Boolean {
        return sharedPreferences!!.getBoolean(preferenceKey, defaultValue)
    }

}