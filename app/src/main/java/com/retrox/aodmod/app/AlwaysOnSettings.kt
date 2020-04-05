package com.retrox.aodmod.app

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.StringDef
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.shared.global.GlobalKV
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange

class AlwaysOnSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.always_on_settings)
        scrollView {
            verticalLayout {

//                title("强力时间矫正")
//                content("尝试使用AlarmManager的定时唤醒来提醒息屏更新时间，时间不准的手机可以尝试。\n注意：尚未实际测试耗电影响。")
//                toggleButton {
//                    textOn = "强力时间矫正开"
//                    textOff = "强力时间矫正关"
//                    isChecked = AppPref.alarmTimeCorrection
//                    onCheckedChange { _, isChecked ->
//                        AppPref.alarmTimeCorrection = isChecked
//                        Toast.makeText(context, "强力时间矫正:${AppPref.alarmTimeCorrection}", Toast.LENGTH_SHORT).show()
//                    }
//                }.lparams(width = matchParent, height = wrapContent) {
//                    verticalMargin = dip(12)
//                    horizontalMargin = dip(8)
//                }

                title(context.getString(R.string.always_on_hand_detection))
                content(context.getString(R.string.always_on_hand_detection_desc))
                toggleButton {
                    textOn = context.getString(R.string.raise_hand_detection_on)
                    textOff = context.getString(R.string.raise_hand_detection_off)
                    isChecked = AppPref.aodPickCheck
                    onCheckedChange { _, isChecked ->
                        AppPref.aodPickCheck = isChecked
                        Toast.makeText(context, context.getString(R.string.raise_hand_detection_toast, AppPref.aodPickCheck.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    topMargin = dip(12)
                    bottomMargin = dip(6)
                    horizontalMargin = dip(8)
                }


                title(context.getString(R.string.force_word_on_flat))
                content(context.getString(R.string.force_word_on_flat))
                toggleButton {
                    textOn = context.getString(R.string.use_word_clock)
                    textOff = context.getString(R.string.not_use_word_flat)
                    isChecked = AppPref.forceShowWordClockOnFlat
                    onCheckedChange { _, isChecked ->
                        AppPref.forceShowWordClockOnFlat = isChecked
                        Toast.makeText(context, context.getString(R.string.not_use_word_flat_toast, AppPref.forceShowWordClockOnFlat.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.ambient_memo))
                content(context.getString(R.string.ambient_will_display_memo))
                toggleButton {
                    textOn = context.getString(R.string.enable_aod_memo)
                    textOff = context.getString(R.string.disabel_aod_memo)
                    isChecked = AppPref.aodShowNote
                    onCheckedChange { _, isChecked ->
                        AppPref.aodShowNote = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_memo_toast, AppPref.aodShowNote.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    topMargin = dip(12)
                    bottomMargin = dip(6)
                    horizontalMargin = dip(8)
                }
                focusable = View.FOCUSABLE
                isFocusableInTouchMode = true

                val editNote = editText {
                    hint = context.getString(R.string.input_memo_here)
                    if (!AppPref.aodNoteContent.isNullOrBlank()) {
                        setText(AppPref.aodNoteContent)
                    }
                }
                button {
                    text = context.getString(R.string.save_memo)
                    setOnClickListener {
                        AppPref.aodNoteContent = editNote.text.toString()
                    }
                }

                title(context.getString(R.string.aod_display_weather))
                content(context.getString(R.string.aod_weather_tip))
                toggleButton {
                    textOn = context.getString(R.string.enable_aod_weather)
                    textOff = context.getString(R.string.disable_aod_weather)
                    isChecked = AppPref.aodShowWeather
                    onCheckedChange { _, isChecked ->
                        AppPref.aodShowWeather = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_weather_toast, AppPref.aodShowWeather.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }


                title(context.getString(R.string.nightmode_auto_close))
                content(context.getString(R.string.nightmode_auto_close_content))
                toggleButton {
                    textOn = context.getString(R.string.nightmode_autoclose)
                    textOff = context.getString(R.string.nightmode_not_close)
                    isChecked = AppPref.autoCloseByNightMode
                    onCheckedChange { _, isChecked ->
                        AppPref.autoCloseByNightMode = isChecked
                        Toast.makeText(context, context.getString(R.string.nightmode_toast, AppPref.autoCloseByNightMode.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.aod_force_english))
                content(context.getString(R.string.aod_force_english_desc))
                toggleButton {
                    textOn = context.getString(R.string.aod_force_english_enabled)
                    textOff = context.getString(R.string.aod_force_english_disabled)
                    isChecked = AppPref.forceEnglishWordClock
                    onCheckedChange { _, isChecked ->
                        AppPref.forceEnglishWordClock = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_force_english_toast , AppPref.forceEnglishWordClock.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.lyrics_translation))
                content(context.getString(R.string.lyrics_translation_desc))
                toggleButton {
                    textOn = context.getString(R.string.translation_on)
                    textOff = context.getString(R.string.translation_off)
                    isChecked = GlobalKV.get("lrc_trans")?.toBoolean() ?: false
                    onCheckedChange { _, isChecked ->
                        GlobalKV.put("lrc_trans", isChecked.toString())
                        Toast.makeText(context, getString(R.string.translation_toast, GlobalKV.get("lrc_trans")), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.flip_off_screen))
                content(context.getString(R.string.flip_off_screen_desc))
                toggleButton {
                    textOn = context.getString(R.string.flip_off_screen_enabled)
                    textOff = context.getString(R.string.flip_off_screen_disabled)
                    isChecked = AppPref.filpOffScreen
                    onCheckedChange { _, isChecked ->
                        AppPref.filpOffScreen = isChecked
                        Toast.makeText(context, context.getString(R.string.flip_off_screen_toast, AppPref.filpOffScreen.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.auto_brightness_aod))
                content(context.getString(R.string.auto_brightness_aod_desc))
                toggleButton {
                    textOn = context.getString(R.string.auto_brightness_aod_enabled)
                    textOff = context.getString(R.string.auto_brightness_aod_disabled)
                    isChecked = AppPref.autoBrightness
                    onCheckedChange { _, isChecked ->
                        AppPref.autoBrightness = isChecked
                        Toast.makeText(context, context.getString(R.string.auto_brightness_aod_toast, AppPref.autoBrightness.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.aod_show_sensitive_content))
                content(context.getString(R.string.aod_show_sensitive_content_desc))
                toggleButton {
                    textOn = context.getString(R.string.aod_show_sensitive_content_enabled)
                    textOff = context.getString(R.string.aod_show_sensitive_content_disabled)
                    isChecked = AppPref.aodShowSensitiveContent
                    onCheckedChange { _, isChecked ->
                        AppPref.aodShowSensitiveContent = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_show_sensitive_content_toast, AppPref.aodShowSensitiveContent.toString()), Toast.LENGTH_SHORT)
                            .show()

                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.aod_font))
                content(context.getString(R.string.aod_font_desc))
                toggleButton {
                    textOn = context.getString(R.string.aod_font_enabled)
                    textOff = context.getString(R.string.aod_font_disabled)
                    isChecked = AppPref.fontWithSystem
                    onCheckedChange { _, isChecked ->
                        AppPref.fontWithSystem = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_font_toast, AppPref.fontWithSystem.toString()), Toast.LENGTH_SHORT).show()

                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.aod_offset_music))
                content(context.getString(R.string.aod_offset_music_desc))
                toggleButton {
                    textOn = context.getString(R.string.aod_offset_music_enabled)
                    textOff = context.getString(R.string.aod_offset_music_disabled)
                    isChecked = AppPref.musicDisplayOffset
                    onCheckedChange { _, isChecked ->
                        AppPref.musicDisplayOffset = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_offset_music_toast, AppPref.musicDisplayOffset.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.aod_hide_after_hour))
                content(context.getString(R.string.aod_hide_after_hour_desc))
                toggleButton {
                    textOn = context.getString(R.string.aod_hide_after_hour_enabled)
                    textOff = context.getString(R.string.aod_hide_after_hour_disabled)
                    isChecked = AppPref.autoCloseAfterHour
                    onCheckedChange { _, isChecked ->
                        AppPref.autoCloseAfterHour = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_hide_after_hour_toast , AppPref.autoCloseAfterHour.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.aod_hide_after_15_seconds))
                content(context.getString(R.string.aod_hide_after_15_seconds_desc))
                toggleButton {
                    textOn = context.getString(R.string.aod_hide_after_15_seconds_enabled)
                    textOff = context.getString(R.string.aod_hide_after_15_seconds_disabled)
                    isChecked = AppPref.autoCloseBySeconds
                    onCheckedChange { _, isChecked ->
                        AppPref.autoCloseBySeconds = isChecked
                        Toast.makeText(context, context.getString(R.string.aod_hide_after_15_seconds_toast, AppPref.autoCloseBySeconds.toString()), Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }
            }
        }

    }

    fun _LinearLayout.title(title: String) = textView {
        text = title
        textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
        textSize = 18f
        gravity = Gravity.START
    }.lparams(width = matchParent, height = wrapContent) {
        verticalMargin = dip(8)
        horizontalMargin = dip(12)
    }

    fun _LinearLayout.content(content: String) = textView {
        text = content
        gravity = Gravity.START
        textColor = Color.BLACK
        textSize = 16f

    }.lparams(width = matchParent, height = wrapContent) {
        verticalMargin = dip(8)
        horizontalMargin = dip(12)
    }
}