package com.retrox.aodmod.extensions

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AndroidAppHelper
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.os.Handler
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.reflect.TypeToken
import com.retrox.aodmod.BuildConfig
import com.retrox.aodmod.SmaliImports
import com.retrox.aodmod.app.App
import com.retrox.aodmod.app.XposedUtils
import com.retrox.aodmod.app.util.getSystemContext
import com.retrox.aodmod.app.util.logD
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.pref.XPref.isSettings
import com.retrox.aodmod.proxy.view.theme.ThemeClockPack
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.service.notification.NotificationManager
import com.retrox.aodmod.service.notification.getNotificationData
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.text.SimpleDateFormat
import java.util.*


fun TextView.setGoogleSans(style: String = "Regular"): Boolean {
    if (XPref.getFontWithSystem()) return false // 跟随系统字体
    typeface = Typeface.createFromAsset(ResourceUtils.getInstance(context).assets, "fonts/GoogleSans-$style.ttf")
    return true
}

fun Context.checkPermission(callback: () -> Unit): Boolean {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        callback()
        return false
    } else return true
}

/**
 * 使用WakeLock来保证一些操作的正确执行
 */
fun Context.wakeLockWrap(tag: String, block: () -> Unit) {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag)
    wakeLock.acquire(2000L)
    block()
    wakeLock.release()
}

fun TextView.setGradientTest(colorPack: ThemeClockPack = ThemeManager.getCurrentColorPack()) {
    val shader = LinearGradient(
        0f, 0f, width.toFloat() * 1.2f, height.toFloat() * 1.2f, Color.parseColor(colorPack.gradientStart),Color.parseColor(colorPack.gradientEnd),Shader.TileMode.CLAMP)
    paint.shader = shader
//    Log.d("OPAodMod", "Debug Gradient -> text: $text width: $width height: $height")
}

// 1900/1905:OP7 China, 1910 OP7Pro China, 1911: India, 1913: EU, 1915: Tmobile, 1917: global/US unlocked, 1920: EU 5G
val OP7DeviceModels = listOf<String>("GM1900", "GM1905", "GM1910", "GM1911", "GM1913", "GM1915", "GM1917", "GM1920")

fun isOP7Pro() = OP7DeviceModels.contains(android.os.Build.MODEL) || XPref.isAndroidQ() // 强制Q机器使用7pro适配模式

/**
 * Only for OP7Pro
 */
@SuppressLint("MissingPermission")
fun Vibrator.simpleTap() {
    setVibratorEffect(1007)
    val effect = VibrationEffect.createWaveform(longArrayOf(0, 11), intArrayOf(0,-1), -1)
    val attr = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build()
    vibrate(effect, attr)
}

/**
 * Only for OP7Pro
 */
fun Vibrator.setVibratorEffect(senceId: Int): Long {
    val TAG = "VibrationHelper2"
    val setVibrationEffect = try {
        Vibrator::class.java.getMethod("setVibratorEffect", Int::class.java)
    } catch (e: NoSuchMethodError) {
        Log.e(TAG, "failed to get method of name 'setVibratorEffect', error= $e")
        return 0L
    }
    try {
        val num = setVibrationEffect.invoke(this, *arrayOf<Any>(senceId)) as? Int
        return num?.toLong() ?: 1L
    } catch (e: IllegalAccessException) {
        Log.e(TAG, "setVibratorEffect# failed to set vibration effect: error= $e")
        return 1L
    } catch (e2: InvocationTargetException) {
        Log.e(TAG, "setVibratorEffect# failed to set vibration effect: error= $e2")
        return 1L
    }
}

fun getDependency(clazz: Class<*>): Any {
    val Dependency = XposedHelpers.findClass("com.android.systemui.Dependency", AndroidAppHelper.currentApplication().classLoader)
    return XposedHelpers.callStaticMethod(Dependency, "get", clazz)
}

fun getVpnStatus() : Boolean{
    val SecurityController = XposedHelpers.findClass("com.android.systemui.statusbar.policy.SecurityController", AndroidAppHelper.currentApplication().classLoader)
    val mSecurityController = getDependency(SecurityController)
    val isVpnEnabled = XposedHelpers.callMethod(mSecurityController, "isVpnEnabled")
    Log.d("OPAodMod", "Vpn Stat : $isVpnEnabled")
    return isVpnEnabled as Boolean
}

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type

@SuppressLint("SetWorldReadable", "SetWorldWritable")
fun File.chmod777() {
    val ex = setExecutable(true, false)
    val r = setReadable(true, false)
    val w = setWritable(true, false)
    logD("chmod777 ${this.absolutePath} $r $w $ex")
}

fun resetPrefPermissions(context: Context?){
    //Arbitrary delay to workaround delays with .apply() on prefs
    //This fixes an issue where the prefs would get reset to 644, resetting the AoD to defaults
    Handler().postDelayed({
        context?.let {
            val file = File("${it.cacheDir.parentFile.absolutePath}/shared_prefs", "${BuildConfig.APPLICATION_ID}_preferences.xml")
            file.chmod777()
        }
    }, 250)
}

fun generateAlarmText(context: Context): String {
    if (XPref.getShowAlarm()) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarm = alarmManager.nextAlarmClock
        val stringBuilder = StringBuilder()
        if(nextAlarm != null) {
            val nextAlarmTime = nextAlarm.triggerTime
            val currentTime = System.currentTimeMillis()
            if (nextAlarmTime - currentTime <= 86400000) {
                if (XPref.getWeatherShowCity()) {
                    if (XPref.getShowBullets()) {
                        stringBuilder.append(" • ")
                    }
                } else {
                    stringBuilder.append("\n")
                }
                //Alarm is within next 24h
                if (XPref.getShowAlarmEmoji()) {
                    stringBuilder.append("⏰")
                    stringBuilder.append(" ")
                }
                stringBuilder.append(SmaliImports.getFormattedTime(nextAlarmTime))
            }
        }
        return stringBuilder.toString()
    }
    return ""
}

fun Number.toCNString(): String {
    return Num2CN().convert(this.toLong(), true).reduce { acc, s ->
        acc + s
    }
}

private fun getNotiNotiMessage(): String {
    return NotificationManager.notificationMap.values.find {
        it.packageName == "mark.notification"
    }?.let {
        val data = it.notification.getNotificationData()
        val message = "${data.title}  ${data.content}"
        Log.d("OPAodMod", message)
        message
    } ?: ""
}

fun getNewAodNoteContent(): String {
    val oldContent = XPref.getAodNoteContent()
    if (oldContent.isBlank()) {
        return getNotiNotiMessage()
    } else if (getNotiNotiMessage().isBlank()) {
        return oldContent
    } else{
        return "${oldContent} \n${getNotiNotiMessage()} "
    }
}

val dateFormats = arrayOf("EEE, d MMM", "EEE, MMM d", "dd/MM/y", "MM/dd/y", "d/M/y", "M/d/y")

fun getDateFormatted(dateFormat: String): String {
    return SimpleDateFormat(dateFormat, Locale.ENGLISH)
            .format(Date())
}

fun Context.getToolbarHeight(): Int {
    // Calculate ActionBar height
    val tv = TypedValue()
    if (theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
        return TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
    }
    return 0
}

fun View.getBottomY(): Int {
    return getTopY() + height
}

fun View.getTopY(): Int {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    return location[1]
}

fun Context?.getDrawableC(@DrawableRes id: Int): Drawable? {
    this?.let {
        return ContextCompat.getDrawable(it, id)
    }
    return null
}

fun Snackbar.setGoogleSans() : Snackbar {
    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface = ResourcesCompat.getFont(context, com.retrox.aodmod.R.font.googlesans)
    return this
}

fun getAppString(@StringRes stringRes: Int): String {
    return if(isSettings()){
        App.application.getString(stringRes)
    }else{
        val appContext = AndroidAppHelper.currentApplication()
        if(appContext != null){
            appContext.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY).getString(stringRes)
        }else{
            getSystemContext().createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY).getString(stringRes)
        }

    }
}

//fun getBlueToothAudioStatus() : Boolean {
//    val BlueToothControllerClass = XposedHelpers.findClass("com.android.systemui.statusbar.policy.BluetoothController", AndroidAppHelper.currentApplication().classLoader)
//    val bluetoothController = getDependency(BlueToothControllerClass)
//    val devices = XposedHelpers.callMethod(bluetoothController, "getConnectedDevices")
//
//
//}