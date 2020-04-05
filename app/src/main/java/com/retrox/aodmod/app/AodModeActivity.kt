package com.retrox.aodmod.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.extensions.isOP7Pro
import org.jetbrains.anko.*

class AodModeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollView {
            verticalLayout {
                textView {
                    text = context.getString(R.string.aod_mode_whats_this)
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_mode_whats_this_desc)
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_mode_system_enhancement)
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_mode_system_enhancement_desc)
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                button {
                    text = context.getString(R.string.aod_mode_system_enhancement_click_to_use)
                    if (isOP7Pro()) {
                        isEnabled = false
                        text = context.getString(R.string.aod_mode_system_enhancement_7pro)
                    }
                    setOnClickListener {
                        AppPref.aodMode = "SYSTEM"
                        Toast.makeText(context, context.getString(R.string.aod_mode_system_enhancement_enabled), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.START
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }


                textView {
                    text = context.getString(R.string.aod_use_this)
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = context.getString(R.string.aod_mode_system_enhancement_features)
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                button {
                    text = context.getString(R.string.aod_click_to_use)
                    setOnClickListener {
                        AppPref.aodMode = "ALWAYS_ON"
                        Toast.makeText(context, context.getString(R.string.aod_enabled_toast), Toast.LENGTH_SHORT).show()
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