package com.retrox.aodmod.app

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.view.setBorderlessStyle
import org.jetbrains.anko.*

class AlarmSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.clock_alignment_settings)

        scrollView {
            verticalLayout {
                leftPadding = dip(12)
                title(context.getString(R.string.what_is_clock_alignment_setting))
                content(context.getString(R.string.what_is_clock_alignment_setting_desc))

                title(context.getString(R.string.rely_on_system_broadcast))
                content(context.getString(R.string.rely_on_system_broadcast_desc))
                button {
                    text = context.getString(R.string.click_to_set_system_broadcast_mode)
                    setBorderlessStyle()
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    setOnClickListener {
                        AppPref.aodAlarmMode = "SYSTEM"
                        Toast.makeText(context, getString(R.string.mode_set, AppPref.aodAlarmMode), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent)

                title(context.getString(R.string.use_alarm_manager))
                content(context.getString(R.string.use_alarm_manager_desc))
                button {
                    text = context.getString(R.string.click_to_set_alarm_manager)
                    setBorderlessStyle()
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    setOnClickListener {
                        AppPref.aodAlarmMode = "AlarmManager-1min"
                        Toast.makeText(context, getString(R.string.mode_set, AppPref.aodAlarmMode), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent)

                title(context.getString(R.string.use_alarm_manager_timeout_mode))
                content(context.getString(R.string.use_alarm_manager_timeout_mode_desc))
                button {
                    text = context.getString(R.string.click_to_set_alarm_manager_timeout)
                    setBorderlessStyle()
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    setOnClickListener {
                        AppPref.aodAlarmMode = "Alarm-TimeOutMode"
                        Toast.makeText(context, getString(R.string.mode_set, AppPref.aodAlarmMode), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent)

                title(context.getString(R.string.use_chore_mechanism))
                content(context.getString(R.string.use_chore_mechanism_desc))
                button {
                    text = context.getString(R.string.click_to_set_chore_mechanism)
                    setBorderlessStyle()
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    setOnClickListener {
                        AppPref.aodAlarmMode = "Chore"
                        Toast.makeText(context, getString(R.string.mode_set, AppPref.aodAlarmMode), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent)
            }
        }
    }


    fun _LinearLayout.title(title: String) = textView {
        text = title
        textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
        textSize = 18f
        gravity = Gravity.START
    }.lparams(width = matchParent, height = wrapContent) {
        topMargin = dip(12)
    }

    fun _LinearLayout.content(content: String) = textView {
        text = content
        gravity = Gravity.START
        textColor = Color.BLACK
        textSize = 16f

    }.lparams(width = matchParent, height = wrapContent) {
        verticalMargin = dip(4)
    }
}