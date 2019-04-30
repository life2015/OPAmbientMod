package com.retrox.aodmod.app

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.app.view.setBorderlessStyle
import org.jetbrains.anko.*

class AlarmSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "时钟对齐设置"

        scrollView {
            verticalLayout {
                leftPadding = dip(12)
                title("时钟对齐设置是什么")
                content("常亮模式中，常常会有时间不对应的情况，所以要进行一些操作来对齐时间，但是不同的对齐方案有着不同的耗电量\n这里你可以选择自己喜欢的对齐方案。\n**备注：仅适用于常亮模式")

                title("仅依靠系统广播")
                content("仅仅通过系统的广播来更新时间，适用于广播准确的OOS，但是在HOS会有延迟。\n**备注：最省电")
                button {
                    text = "点击设置系统广播模式"
                    setBorderlessStyle()
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    setOnClickListener {
                        AppPref.aodAlarmMode = "SYSTEM"
                        Toast.makeText(context, "已设置模式 ${AppPref.aodAlarmMode}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent)

                title("使用AlarmManager来唤醒")
                content("除了系统广播外使用AlarmManager来唤醒，这种方式大概一分钟会同步一次时间，但是也会影响到手机的睡眠状态。\n**备注：有电量消耗，在翻扣灭屏后会暂停唤醒（这种状态省电），因此建议在不用的时候翻扣或者口袋灭屏")
                button {
                    text = "点击设置AlarmManager模式"
                    setBorderlessStyle()
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    setOnClickListener {
                        AppPref.aodAlarmMode = "AlarmManager-1min"
                        Toast.makeText(context, "已设置模式 ${AppPref.aodAlarmMode}", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent)

                title("使用Chore机制来唤醒（默认）")
                content("另辟蹊径使用动画时钟来唤醒，可以做到相对最精准的时间同步，但是会影响手机耗电，在手机进入深度睡眠时候也可能会出现不准的情况\n**备注：有电量消耗，在翻扣灭屏后会暂停唤醒（这种状态省电），因此建议在不用的时候翻扣或者口袋灭屏")
                button {
                    text = "点击设置Chore模式"
                    setBorderlessStyle()
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    setOnClickListener {
                        AppPref.aodAlarmMode = "Chore"
                        Toast.makeText(context, "已设置模式 ${AppPref.aodAlarmMode}", Toast.LENGTH_SHORT).show()
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