package com.retrox.aodmod.app.settings.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.preference.SwitchPreference
import androidx.preference.forEach
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.settings.SettingsActivity
import com.retrox.aodmod.app.settings.preference.Preference
import com.retrox.aodmod.extensions.resetPrefPermissions
import com.retrox.aodmod.extensions.runAfter
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.weather.WeatherProvider

class SettingsWeatherFragment : GenericPreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_weather)
        setupPreferences()
    }

    private fun setupPreferences(){
        val currentTheme = AppPref.aodLayoutTheme
        val isWeatherSupported = ThemeManager.doesThemeSupportToday(currentTheme)
        val isWeatherCitySupported = ThemeManager.doesThemeSupportWeatherCity(currentTheme)
        findSwitchPreference("settings_weather_condition"){
            it.isChecked = AppPref.weatherShowSymbol
            it.isVisible = isWeatherSupported
            it.listen { value ->
                AppPref.weatherShowSymbol = value
            }
        }
        findSwitchPreference("settings_weather_condition_text"){
            it.isChecked = AppPref.weatherShowCondition
            it.isVisible = isWeatherSupported
            it.listen { value ->
                AppPref.weatherShowCondition = value
            }
        }
        findSwitchPreference("settings_weather_tempeature"){
            it.isChecked = AppPref.weatherShowTemperature
            it.isVisible = isWeatherSupported
            it.listen { value ->
                AppPref.weatherShowTemperature = value
            }
        }
        findSwitchPreference("settings_weather_city"){
            it.isChecked = AppPref.weatherShowCity
            it.isVisible = isWeatherSupported && isWeatherCitySupported
            it.listen { value ->
                AppPref.weatherShowCity = value
            }
        }
        findPreference("settings_weather_current_condition"){
            it.isVisible = isWeatherSupported
            loadWeather(it)
        }
        findDropdownPreference("settings_weather_icon_style"){
            it.isVisible = isWeatherSupported
            it.summary = it.entry
            it.listen { newValue ->
                AppPref.weatherIconStyle = newValue
                view?.post {
                    it.summary = it.entry
                }
            }
        }
        setAllEnabled(AppPref.aodShowWeather)
        findPreference("generic_theme_unsupported"){
            it.isVisible = !isWeatherSupported
        }
        setMasterSwitchEnabled(isWeatherSupported)
    }

    private fun loadWeather(preference: Preference?){
        val weatherLiveData = WeatherProvider.weatherLiveEvent
        weatherLiveData.observe(this, Observer { weatherData ->
            if (weatherData != null) {
                with(weatherData) {
                    preference?.summary = context?.getString(R.string.settings_weather_current_condition_desc, cityName, weatherName, temperature.toString() + temperatureUnit.trim(), temperatureLow.toString().trim() + " to " + temperatureHigh.toString().trim() + temperatureUnit.trim())
                }
            } else {
                //preference?.summary = getString(R.string.settings_weather_current_condition_desc_off)
            }
        })
        preference?.setOnPreferenceClickListener {
            context?.let { context ->
                if(context.packageManager.isPackageAvailable("net.oneplus.weather")) {
                    startActivity(context.packageManager.getLaunchIntentForPackage("net.oneplus.weather"))
                }
            }
            true
        }
    }

    fun setAllEnabled(enabled: Boolean){
        preferenceScreen?.forEach {
            it.isEnabled = enabled
        }
    }

    override fun getMasterSwitchTitle(): Int? {
        return R.string.settings_weather_master_switch
    }

    override fun onMasterSwitchCheckedChange(checked: Boolean) {
        super.onMasterSwitchCheckedChange(checked)
        setAllEnabled(checked)
        AppPref.aodShowWeather = checked
        resetPrefPermissions(context)
    }

    override fun isMasterSwitchChecked(): Boolean {
        return AppPref.aodShowWeather
    }

    override fun onLayoutChanged(newLayout: String) {
        super.onLayoutChanged(newLayout)
        setupPreferences()
        runAfter(0.5){
            checkNestedScroll()
        }
    }

}