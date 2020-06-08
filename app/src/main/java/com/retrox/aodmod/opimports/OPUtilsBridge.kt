package com.retrox.aodmod.opimports

import android.content.Context
import android.graphics.Typeface
import com.retrox.aodmod.extensions.getSystemUiClassLoader
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*


object OPUtilsBridge {

    private var opUtils: Class<*>? = null

    fun init(context: Context){
        opUtils = Class.forName("com.oneplus.util.OpUtils", false, context.getSystemUiClassLoader())
        //We can't just call init as it relies on a permission that we don't have
        //opUtils!!.getMethod("init", Context::class.java).invoke(null, context)
        val densityDpi = context.resources.configuration.densityDpi
        opUtils!!.getDeclaredMethod("updateDensityDpi", Integer.TYPE).setAccessibleR(true).invoke(null, densityDpi)
        val checkIsSupportResolutionSwitch = opUtils!!.getDeclaredMethod("checkIsSupportResolutionSwitch", Context::class.java).setAccessibleR(true).invoke(null, context) as Boolean
        opUtils!!.getDeclaredField("mIsSupportResolutionSwitch").setAccessibleR(true).set(null, checkIsSupportResolutionSwitch)
        opUtils!!.getDeclaredMethod("loadMCLTypeface").setAccessibleR(true).invoke(null)
    }

    @JvmStatic
    fun isMCLVersionFont(): Boolean {
        return try{
            opUtils!!.getMethod("isMCLVersionFont").invoke(null) as Boolean
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    fun isMCLVersion(): Boolean {
        return try{
            opUtils!!.getMethod("isMCLVersion").invoke(null) as Boolean
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    fun getMclTypeface(arg1: Int): Typeface {
        return try{
            opUtils!!.getMethod("getMclTypeface", Integer.TYPE).invoke(null, arg1) as Typeface
        }catch (e: Exception){
            e.printStackTrace()
            Typeface.DEFAULT
        }
    }

    @JvmStatic
    fun convertDpToFixedPx(dp: Float): Int {
        return try{
            opUtils!!.getMethod("convertDpToFixedPx", Float::class.java).invoke(null, dp) as Int
        }catch (e: Exception){
            e.printStackTrace()
            0
        }
    }

    @JvmStatic
    fun getDeviceTag(): String? {
        return getSystemProperty("ro.boot.project_name")
    }

    @JvmStatic
    fun getSystemProperty(key: String): String? {
        val systemProperties = Class.forName("android.os.SystemProperties")
        return systemProperties.getMethod("get", String::class.java).invoke(null, key) as? String
    }

    @JvmStatic
    fun getSystemBoolean(key: String, default: Boolean): Boolean {
        val systemProperties = Class.forName("android.os.SystemProperties")
        return systemProperties.getMethod("getBoolean", String::class.java, Boolean::class.java).invoke(null, key, default) as Boolean
    }

    @JvmStatic
    fun getLocaleDatahm(context: Context): CharSequence {
        val clazz = Class.forName("libcore.icu.LocaleData", false, context.getSystemUiClassLoader())
        val instance = clazz.getMethod("get", Locale::class.java).invoke(null, context.resources.configuration.locale)
        return clazz.getField("timeFormat_hm").get(instance) as CharSequence
    }

    @JvmStatic
    fun getLocaleDataHm(context: Context): CharSequence {
        val clazz = Class.forName("libcore.icu.LocaleData", false, context.getSystemUiClassLoader())
        val instance = clazz.getMethod("get", Locale::class.java).invoke(null, context.resources.configuration.locale)
        return clazz.getField("timeFormat_Hm").get(instance) as CharSequence
    }

    fun Method.setAccessibleR(isAccessible: Boolean) : Method {
        this.isAccessible = isAccessible
        return this
    }

    fun Field.setAccessibleR(isAccessible: Boolean) : Field {
        this.isAccessible = isAccessible
        return this
    }

}