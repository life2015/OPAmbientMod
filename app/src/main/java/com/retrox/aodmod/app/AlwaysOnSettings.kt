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