package com.retrox.aodmod.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
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
                    text = "这是干什么的？"
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = "我们在使用息屏的常亮模式时，有些时候我们并不想要它一直亮屏，比如说在睡觉的时候。睡眠模式打开的时候，常亮模式只会持续5-10秒钟然后关屏，反之亦然。"
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                button {
                    text = "开启睡眠模式"
                    setOnClickListener {
                        val intent = Intent("com.aodmod.sleep.on")
                        sendBroadcast(intent)
                        Toast.makeText(context, "已启用睡眠模式", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.START
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                button {
                    text = "关闭睡眠模式"
                    setOnClickListener {
                        val intent = Intent("com.aodmod.sleep.off")
                        sendBroadcast(intent)
                        Toast.makeText(context, "已关闭睡眠模式", Toast.LENGTH_SHORT).show()
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