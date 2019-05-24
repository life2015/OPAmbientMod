package com.retrox.aodmod.plugin

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.widget.TextView
import android.content.Context.VIBRATOR_SERVICE
import android.media.AudioAttributes
import android.support.v4.content.ContextCompat.getSystemService
import android.os.Vibrator
import android.view.View


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
            val effect = VibrationEffect.createWaveform(longArrayOf(0L, 50L), intArrayOf(0, -1), -1)
            vibrator.vibrate(effect, attr)
        }
    }
}
