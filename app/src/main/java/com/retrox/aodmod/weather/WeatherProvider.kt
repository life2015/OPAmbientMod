package com.retrox.aodmod.weather

import android.app.AlarmManager
import android.app.AndroidAppHelper
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.retrox.aodmod.SmaliImports
import com.retrox.aodmod.app.App
import com.retrox.aodmod.extensions.appendSpace
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.weather.icons.BaseWeatherIconProvider
import com.retrox.aodmod.weather.icons.EmojiWeatherIconProvider
import com.retrox.aodmod.weather.icons.PixelWeatherIconProvider
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/**
 * 里面有一部分代码是反编译转化的
 * 凑合着用吧 懒得写
 */
object WeatherProvider {
    private val WEATHER_CONTENT_URI = Uri.parse("content://com.oneplus.weather.ContentProvider/data")
    private val TEMP_UNIT_CELSIUS = "˚C"
    private val WEATHER_NAME_NONE = "N/A"
    private val TAG = "OPAodWeather"
    private val context: Context by lazy {
        val contextRef = App.applicationReference?.get()?.applicationContext
        if (contextRef != null) {
            contextRef
        } else AndroidAppHelper.currentApplication().applicationContext

    }

    val weatherLiveEvent = object : MutableLiveData<WeatherData>() {
        override fun onActive() {
            super.onActive()
            queryWeatherInformation(context, forceRefresh = true)
            registerContentObserver(context)
        }

        override fun onInactive() {
            super.onInactive()
            unregisterContentObserver(context)
        }
    }

    private val weatherObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            queryWeatherInformation(context)
        }
    }

    private var lastQueryTime = 0L
    fun queryWeatherInformation(context: Context, forceRefresh: Boolean = false): WeatherData? {
        val needQueryNew = ((System.currentTimeMillis() - lastQueryTime) < 1000L * 60 * 20) || forceRefresh
        if (!needQueryNew) {
            return weatherLiveEvent.value
        }
        lastQueryTime = System.currentTimeMillis()
        if (isPackageInstalled(context, "net.oneplus.weather")) {
            thread {
                // 妈的 同步调用给我搞出个ANR来
                val data = processWeatherInformation(
                    context.contentResolver.query(
                        this.WEATHER_CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )
                )
                weatherLiveEvent.postValue(data) // null 也要post
//                data?.let { weatherData -> weatherLiveEvent.postValue(weatherData) }
            }
        } else {
            Log.w(TAG, "the weather application is not installed, stop querying...")
            weatherLiveEvent.postValue(null)
            return null // 没这个软件 FUCK
        }
        return weatherLiveEvent.value // 先要给她点数据 不能空了 反正后续会有新的数据通过LiveData
    }

    fun queryWeatherInformationSync(context: Context): WeatherData? {
        return if (isPackageInstalled(context, "net.oneplus.weather")) {
            val data = processWeatherInformation(
                context.contentResolver.query(
                    this.WEATHER_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
            )
            data
        } else {
            Log.w(TAG, "the weather application is not installed, stop querying...")
            null // 没这个软件 FUCK
        }
    }

    fun registerContentObserver(context: Context) {
        val contentResolver = context.contentResolver
        if (isPackageInstalled(context, "net.oneplus.weather")) {
            try {
                contentResolver.registerContentObserver(this.WEATHER_CONTENT_URI, true, weatherObserver)
            } catch (e: SecurityException) {
                val str2 = TAG
                val stringBuilder2 = StringBuilder()
                stringBuilder2.append("register with Weather provider failed: ")
                stringBuilder2.append(e)
                Log.e(str2, stringBuilder2.toString())
            }

            return
        }
        Log.w(TAG, "the weather application is not installed")
    }

    fun unregisterContentObserver(context: Context) {
        context.contentResolver.unregisterContentObserver(weatherObserver)

    }


    private enum class WEATHER_COLUMNS constructor(val index: Int) {
        TIMESTAMP(0),
        CITY_NAME(1),
        WEATHER_CODE(2),
        WEATHER_NAME(6),
        TEMP(3),
        TEMP_HIGH(4),
        TEMP_LOW(5),
        TEMP_UNIT(7)
    }

    private fun processWeatherInformation(cursor: Cursor?): WeatherData? {
        val stringBuilder: StringBuilder
        if (cursor == null) {
            Log.e(TAG, "cannot get weather information by querying content resolver")
        } else if (cursor.moveToFirst()) {
            if (cursor.columnCount < WEATHER_COLUMNS.values().size) {
                Log.e(TAG, "the column count is not met the spec, contact OPWeather owner.")
                val str = TAG
                val stringBuilder2 = StringBuilder()
                stringBuilder2.append("expected columns: ")
                stringBuilder2.append(WEATHER_COLUMNS.values().size)
                stringBuilder2.append(", actual columns: ")
                stringBuilder2.append(cursor.columnCount)
                Log.e(str, stringBuilder2.toString())
            }
            val weatherData = WeatherData()
            var string: String
            try {
                val string2 = cursor.getString(WEATHER_COLUMNS.TIMESTAMP.index)
                string = cursor.getString(WEATHER_COLUMNS.CITY_NAME.index)
                val string3 = cursor.getString(WEATHER_COLUMNS.WEATHER_CODE.index)
                val string4 = cursor.getString(WEATHER_COLUMNS.WEATHER_NAME.index)
                val string5 = cursor.getString(WEATHER_COLUMNS.TEMP.index)
                val string6 = cursor.getString(WEATHER_COLUMNS.TEMP_HIGH.index)
                val string7 = cursor.getString(WEATHER_COLUMNS.TEMP_LOW.index)
                val string8 = cursor.getString(WEATHER_COLUMNS.TEMP_UNIT.index)
                val str2 = TAG
                val stringBuilder3 = StringBuilder()
                stringBuilder3.append("[Raw Weather Data] timestamp: ")
                stringBuilder3.append(string2)
                stringBuilder3.append(", city: ")
                stringBuilder3.append(string)
                stringBuilder3.append(", code: ")
                stringBuilder3.append(string3)
                stringBuilder3.append(", name: ")
                stringBuilder3.append(string4)
                stringBuilder3.append(", temp: ")
                stringBuilder3.append(string5)
                stringBuilder3.append(", high: ")
                stringBuilder3.append(string6)
                stringBuilder3.append(", low: ")
                stringBuilder3.append(string7)
                stringBuilder3.append(", unit: ")
                stringBuilder3.append(string8)
                Log.d(str2, stringBuilder3.toString())
                weatherData.timestamp = SimpleDateFormat("yyyyMMddkkmm", Locale.getDefault()).parse(string2).time / 1000
                weatherData.cityName = string
                weatherData.weatherCode = string3.toInt()
                weatherData.weatherName = string4
                weatherData.temperature = Integer.parseInt(string5)
                weatherData.temperatureHigh = Integer.parseInt(string6)
                weatherData.temperatureLow = Integer.parseInt(string7)
                weatherData.temperatureUnit = string8

                cursor.close()
                return weatherData
            } catch (e: IllegalStateException) {
                string = TAG
                stringBuilder = StringBuilder()
                stringBuilder.append("invalid Cursor data: ")
                stringBuilder.append(e)
                Log.e(string, stringBuilder.toString())
            } catch (e2: NullPointerException) {
                string = TAG
                stringBuilder = StringBuilder()
                stringBuilder.append("got unexpected weather data: ")
                stringBuilder.append(e2)
                Log.e(string, stringBuilder.toString())
            } catch (e2: NumberFormatException) {
                string = TAG
                stringBuilder = StringBuilder()
                stringBuilder.append("got unexpected weather data: ")
                stringBuilder.append(e2)
                Log.e(string, stringBuilder.toString())
            } catch (e2: ParseException) {
                string = TAG
                stringBuilder = StringBuilder()
                stringBuilder.append("got unexpected weather data: ")
                stringBuilder.append(e2)
                Log.e(string, stringBuilder.toString())
            } catch (th: Throwable) {
                cursor.close()
                Log.d(TAG, weatherData.toString())
            }

            cursor.close()
            Log.d(TAG, weatherData.toString())
            //            updateWeatherCallbacks(weatherData, null);
            //            writePreferences(weatherData);
        } else {
            Log.e(TAG, "cannot move the cursor point to the first row, is the cursor empty?")
            cursor.close()
        }

        return null
    }

    class WeatherData {
        var cityName = ""
        var temperature = -99
        var temperatureHigh = -99
        var temperatureLow = -99
        var temperatureUnit = WeatherProvider.TEMP_UNIT_CELSIUS
        var timestamp: Long = 0
        var weatherCode = 9999
        var weatherName = WeatherProvider.WEATHER_NAME_NONE

        override fun toString(): String {
            val stringBuilder = StringBuilder()
            stringBuilder.append("[timestamp] ")
            stringBuilder.append(this.timestamp)
            stringBuilder.append("; ")
            stringBuilder.append("[cityName] ")
            stringBuilder.append(this.cityName)
            stringBuilder.append("; ")
            stringBuilder.append("[weatherCode] ")
            stringBuilder.append(this.weatherCode)
            stringBuilder.append("; ")
            stringBuilder.append("[weatherName] ")
            stringBuilder.append(this.weatherName)
            stringBuilder.append("; ")
            stringBuilder.append("[temperature] ")
            stringBuilder.append(this.temperature)
            stringBuilder.append("; ")
            stringBuilder.append("[temperatureHigh] ")
            stringBuilder.append(this.temperatureHigh)
            stringBuilder.append("; ")
            stringBuilder.append("[temperatureLow] ")
            stringBuilder.append(this.temperatureLow)
            stringBuilder.append("; ")
            stringBuilder.append("[temperatureUnit] ")
            stringBuilder.append(this.temperatureUnit)
            stringBuilder.append("; ")
            return stringBuilder.toString()
        }

        fun toBriefString(addNewLine: Boolean = false): CharSequence {
            val iconProvider = BaseWeatherIconProvider.getWeatherIconProvider()
            val weatherText = if(iconProvider is EmojiWeatherIconProvider) iconProvider.getEmojiForCode(weatherCode) else "X"
            val stringBuilder = SpannableStringBuilder()
            if (XPref.getWeatherShowSymbol()) {
                stringBuilder.appendSpace()
                stringBuilder.appendSpace()
                stringBuilder.appendSpace()
                stringBuilder.append(weatherText, iconProvider.getWeatherIcon(context, weatherCode), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                stringBuilder.appendSpace()
                stringBuilder.appendSpace()
            }
            if (XPref.getWeatherShowCondition()) {
                stringBuilder.append(weatherName)
                stringBuilder.append(" ")
            }
            if (XPref.getWeatherShowTemperature()) {
                stringBuilder.append(temperature.toString())
                stringBuilder.append(temperatureUnit)
            }
            val isCityNameEnabled = XPref.getWeatherShowCity()
            if (isCityNameEnabled) {
                if(addNewLine) stringBuilder.append("\n")
                else stringBuilder.append("  ")
                stringBuilder.append(cityName)
            }
            return stringBuilder.trim { it <= ' ' }
        }
    }

    private fun isPackageInstalled(context: Context, str: String): Boolean {
        try {
            context.packageManager.getApplicationInfo(str, 0)
            return true
        } catch (unused: PackageManager.NameNotFoundException) {
            return false
        } catch (unused: RuntimeException) {
            return false
        }

    }

}
