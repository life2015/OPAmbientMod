package com.retrox.aodmod.app

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
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

                title("常亮模式依然开启抬手检测")
                content("检测抬手操作，仅做息屏美化。\n测试性功能，仅在7Pro上测试，不保证功能和系统自带息屏一致。")
                toggleButton {
                    textOn = "抬手检测开"
                    textOff = "抬手检测关"
                    isChecked = AppPref.aodPickCheck
                    onCheckedChange { _, isChecked ->
                        AppPref.aodPickCheck = isChecked
                        Toast.makeText(context, "抬手检测:${AppPref.aodPickCheck}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "Word on Flat:${AppPref.forceShowWordClockOnFlat}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "息屏备忘显示:${AppPref.aodShowNote}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "息屏天气:${AppPref.aodShowWeather}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "NightMode AutoOFF:${AppPref.autoCloseByNightMode}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("Force Use English on Word Theme")
                content("English on Word Clock")
                toggleButton {
                    textOn = "Force English ON"
                    textOff = "Force English OFF"
                    isChecked = AppPref.forceEnglishWordClock
                    onCheckedChange { _, isChecked ->
                        AppPref.forceEnglishWordClock = isChecked
                        Toast.makeText(context, "FORCE ENGLISH WORD CLOCK:${AppPref.forceEnglishWordClock}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("歌词开启翻译")
                content("为息屏歌词开启中文翻译（有时候开了也没用）")
                toggleButton {
                    textOn = "开启翻译"
                    textOff = "默认歌词"
                    isChecked = GlobalKV.get("lrc_trans")?.toBoolean() ?: false
                    onCheckedChange { _, isChecked ->
                        GlobalKV.put("lrc_trans", isChecked.toString())
                        Toast.makeText(context, "歌词翻译:${GlobalKV.get("lrc_trans")}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.flip_off_screen))
                content("替代之前的睡眠模式。在手机翻扣或者放入口袋后，自动关闭常亮状态的息屏，手机翻回来或者拿出口袋即可恢复。避免不必要的屏幕常亮。\n亲测好用，建议开启。Recommend to open")
                toggleButton {
                    textOn = "倒扣/口袋模式开 On"
                    textOff = "倒扣/口袋模式关 OFF"
                    isChecked = AppPref.filpOffScreen
                    onCheckedChange { _, isChecked ->
                        AppPref.filpOffScreen = isChecked
                        Toast.makeText(context, "倒扣手机 放入口袋自动灭屏:${AppPref.filpOffScreen}", Toast.LENGTH_SHORT).show()

                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.auto_brightness_aod))
                content("息屏之后通过光线传感器来调整亮度，节省电量")
                toggleButton {
                    textOn = "息屏自动亮度开 On"
                    textOff = "息屏关闭自动亮度 Off"
                    isChecked = AppPref.autoBrightness
                    onCheckedChange { _, isChecked ->
                        AppPref.autoBrightness = isChecked
                        Toast.makeText(context, "息屏自动亮度:${AppPref.autoBrightness}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("息屏消息驻留敏感消息内容设置")
                content("部分人不希望息屏的时候，自己最近的信息不要一直出现在息屏上，避免造成不必要的尴尬。\n关闭之后，电话信息，短信，QQ，微信信息具体内容不会显示在息屏上，仅显示标题作为提醒")
                toggleButton {
                    textOn = "息屏驻留内容正常显示消息"
                    textOff = "息屏驻留内容时屏蔽敏感消息"
                    isChecked = AppPref.aodShowSensitiveContent
                    onCheckedChange { _, isChecked ->
                        AppPref.aodShowSensitiveContent = isChecked
                        Toast.makeText(context, "息屏驻留时内容全部显示:${AppPref.aodShowSensitiveContent}", Toast.LENGTH_SHORT)
                            .show()

                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title(context.getString(R.string.aod_font))
                content("你爱系统字体 GIAO辞")
                toggleButton {
                    textOn = "锁屏字体跟随系统 System Fonts"
                    textOff = "使用Google Sans"
                    isChecked = AppPref.fontWithSystem
                    onCheckedChange { _, isChecked ->
                        AppPref.fontWithSystem = isChecked
                        Toast.makeText(context, "跟随系统字体:${AppPref.fontWithSystem}", Toast.LENGTH_SHORT).show()

                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("一加6T 专属音乐显示偏移")
                content("蛋疼的一加6T音乐和屏幕指纹位置冲突了 开启后音乐显示向上偏移到一个合适位置")
                toggleButton {
                    textOn = "已开启音乐向上偏移"
                    textOff = "不偏移 打扰了"
                    isChecked = AppPref.musicDisplayOffset
                    onCheckedChange { _, isChecked ->
                        AppPref.musicDisplayOffset = isChecked
                        Toast.makeText(context, "音乐向上偏移:${AppPref.musicDisplayOffset}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("常亮十分钟后自动关屏")
                content("...有需求就做吧")
                toggleButton {
                    textOn = "常亮十分钟自动关屏"
                    textOff = "常亮十分钟不自动关屏"
                    isChecked = AppPref.autoCloseAfterHour
                    onCheckedChange { _, isChecked ->
                        AppPref.autoCloseAfterHour = isChecked
                        Toast.makeText(context, "常亮一小时自动关屏:${AppPref.autoCloseAfterHour}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("亮屏15秒自动关屏（bug很多不建议用）")
                content("一部分人只需要美化，因此提供了此功能，配合最上面的抬手检测功能使用。（此设置优先级高于10分钟的设置，只有在15秒不关屏的时候10分钟才会显现作用）")
                toggleButton {
                    textOn = "亮屏15秒自动关屏"
                    textOff = "15秒不自动关屏"
                    isChecked = AppPref.autoCloseBySeconds
                    onCheckedChange { _, isChecked ->
                        AppPref.autoCloseBySeconds = isChecked
                        Toast.makeText(context, "亮屏15秒自动关屏:${AppPref.autoCloseBySeconds}", Toast.LENGTH_SHORT).show()
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