package com.retrox.aodmod.app

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import com.retrox.aodmod.R
import org.jetbrains.anko.*

class EnglishLearnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollView {
            verticalLayout {
                title(context.getString(R.string.english_learn_weekdays))
                content("Sunday Sun. 星期天\n" +
                        "\n" +
                        "Monday Mon. 星期一\n" +
                        "\n" +
                        "Tuesday Tues. 星期二\n" +
                        "\n" +
                        "Wednesday Wed. 星期三\n" +
                        "\n" +
                        "Thursday Thu. 星期四\n" +
                        "\n" +
                        "Friday Fri. 星期五\n" +
                        "\n" +
                        "Saturday Sat. 星期六")

                title(context.getString(R.string.english_learn_battery_status))
                content("Charging 充电中\n\n" +
                        "Quick Charging 一加快速充电中")

                title(context.getString(R.string.english_learn_headphone_connection))
                content("Headset Unplugged 耳机拔出\n\n" +
                        "Headset Plugged 耳机插入")

                title(context.getString(R.string.english_learn_bluetooth_connection))
                content("DisConnected 断开连接\n\n" +
                        "Connected 已连接")

                title(context.getString(R.string.english_learn_volume_related))
                content("Volume 音量")
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