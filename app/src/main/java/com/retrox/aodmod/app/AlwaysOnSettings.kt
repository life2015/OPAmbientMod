package com.retrox.aodmod.app

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange

class AlwaysOnSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView {
            verticalLayout {

                title("强力时间矫正")
                content("尝试使用AlarmManager的定时唤醒来提醒息屏更新时间，时间不准的手机可以尝试。\n注意：尚未实际测试耗电影响。")
                toggleButton {
                    textOn = "强力时间矫正开"
                    textOff = "强力时间矫正关"
                    isChecked = AppPref.alarmTimeCorrection
                    onCheckedChange { _, isChecked ->
                        AppPref.alarmTimeCorrection = isChecked
                        Toast.makeText(context, "强力时间矫正:${AppPref.alarmTimeCorrection}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("息屏备忘")
                content("会在常驻通知区域显示一条备忘录")
                toggleButton {
                    textOn = "息屏备忘开"
                    textOff = "息屏备忘关"
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
                val editNote = editText {
                    hint = "备忘记录写在这里"
                    if (!AppPref.aodNoteContent.isNullOrBlank()) {
                        setText(AppPref.aodNoteContent)
                    }
                }
                button {
                    text = "保存备忘"
                    setOnClickListener {
                        AppPref.aodNoteContent = editNote.text.toString()
                    }
                }

                title("息屏显示天气")
                content("通过CP获取一加系统天气APP的数据，展示在息屏上，数据跟随系统天气App。")
                toggleButton {
                    textOn = "开启息屏天气显示"
                    textOff = "息屏不显示天气"
                    isChecked = AppPref.aodShowWeather
                    onCheckedChange { _, isChecked ->
                        AppPref.aodShowWeather = isChecked
                        Toast.makeText(context, "息屏天气:${AppPref.aodShowWeather}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("倒扣手机 放入口袋自动灭屏")
                content("替代之前的睡眠模式。在手机翻扣或者放入口袋后，自动关闭常亮状态的息屏，手机翻回来或者拿出口袋即可恢复。避免不必要的屏幕常亮。\n亲测好用，建议开启。")
                toggleButton {
                    textOn = "倒扣/口袋模式开"
                    textOff = "倒扣/口袋模式关"
                    isChecked = AppPref.filpOffScreen
                    onCheckedChange { _, isChecked ->
                        AppPref.filpOffScreen = isChecked
                        Toast.makeText(context, "倒扣手机 放入口袋自动灭屏:${AppPref.filpOffScreen}", Toast.LENGTH_SHORT).show()

                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                title("息屏光感调整亮度")
                content("息屏之后通过光线传感器来调整亮度，节省电量")
                toggleButton {
                    textOn = "息屏自动亮度开"
                    textOff = "息屏关闭自动亮度"
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

                title("锁屏字体跟随系统")
                content("你爱系统字体 GIAO辞")
                toggleButton {
                    textOn = "锁屏字体跟随系统"
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

                title("常亮半小时后自动关屏")
                content("...有需求就做吧")
                toggleButton {
                    textOn = "常亮半小时自动关屏"
                    textOff = "常亮半小时不自动关屏"
                    isChecked = AppPref.autoCloseAfterHour
                    onCheckedChange { _, isChecked ->
                        AppPref.autoCloseAfterHour = isChecked
                        Toast.makeText(context, "常亮一小时自动关屏:${AppPref.autoCloseAfterHour}", Toast.LENGTH_SHORT).show()
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