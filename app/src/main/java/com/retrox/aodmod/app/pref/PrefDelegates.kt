package com.retrox.aodmod.app.pref

import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.app.App
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.extensions.chmod777
import java.io.File
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun shared(key: String, default: String) = object : ReadWriteProperty<Any?, String> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): String =
        App.defaultSharedPreferences.getString(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        App.defaultSharedPreferences.edit().putString(key, value).apply()
    }

}

fun shared(key: String, default: Set<String>) = object : ReadWriteProperty<Any?, Set<String>> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Set<String> =
        App.defaultSharedPreferences.getStringSet(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Set<String>) {
        App.defaultSharedPreferences.edit().putStringSet(key, value).apply()
    }

}

fun shared(key: String, default: Int) = object : ReadWriteProperty<Any?, Int> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Int =
        App.defaultSharedPreferences.getInt(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        App.defaultSharedPreferences.edit().putInt(key, value).apply()
    }

}

fun shared(key: String, default: Long) = object : ReadWriteProperty<Any?, Long> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Long =
        App.defaultSharedPreferences.getLong(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        App.defaultSharedPreferences.edit().putLong(key, value).apply()
    }

}

fun shared(key: String, default: Float) = object : ReadWriteProperty<Any?, Float> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Float =
        App.defaultSharedPreferences.getFloat(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        App.defaultSharedPreferences.edit().putFloat(key, value).apply()
    }

}

fun shared(key: String, default: Boolean) = object : ReadWriteProperty<Any?, Boolean> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
        App.defaultSharedPreferences.getBoolean(key, default)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        App.defaultSharedPreferences.edit().putBoolean(key, value).apply()
    }

}