package com.retrox.aodmod.plugin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.widget.TextView
import android.content.Context.VIBRATOR_SERVICE
import android.media.AudioAttributes
import androidx.core.content.ContextCompat.getSystemService
import android.os.Vibrator
import android.util.Log
import android.view.View
import java.lang.reflect.InvocationTargetException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val button: TextView = findViewById(R.id.tv_hello)
//        button.setOnClickListener {
//            val intent = Intent("com.retrox.aodplugin.plugin.boardcast").apply {
//                `package` = "com.retrox.aodmod.plugin"
//            }
//            sendBroadcast(intent)
//        }

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val container = findViewById<View>(R.id.container)

        container.setOnClickListener {
            val attr = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build()
            vibrator.setVibratorEffect(1007)
            val effect = VibrationEffect.createWaveform(longArrayOf(120, 16, 100, 16), intArrayOf(0, 255, 0, 255), -1)
//            val effect = VibrationEffect.createWaveform(longArrayOf(-1, 0 , setVibratorEffect(1007, vibrator)) ,-1)
//            val effect = VibrationEffect.createWaveform(longArrayOf(-1, 0 , setVibratorEffect(1007, vibrator)) ,-1)
            vibrator.vibrate(effect, attr)
        }
    }

    fun Vibrator.simpleTap() {
        setVibratorEffect(1007)
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 11), intArrayOf(0,-1), -1)
        val attr = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build()
        vibrate(effect, attr)
    }

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

}
