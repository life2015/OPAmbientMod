package com.retrox.aodmod.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import org.jetbrains.anko.*

class SleepModeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollView {
            verticalLayout {
                textView {
                    text = context.getString(R.string.aod_sleep_what)
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_sleep_what_desc)
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                button {
                    text = context.getString(R.string.aod_sleep_turn_on)
                    setOnClickListener {
                        val intent = Intent("com.aodmod.sleep.on")
                        sendBroadcast(intent)
                        Toast.makeText(context, context.getString(R.string.aod_sleep_enabled_toast), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.START
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                button {
                    text = context.getString(R.string.aod_sleep_turn_off)
                    setOnClickListener {
                        val intent = Intent("com.aodmod.sleep.off")
                        sendBroadcast(intent)
                        Toast.makeText(context, context.getString(R.string.aod_sleep_disabled_toast), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.START
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }
            }
        }
    }
}