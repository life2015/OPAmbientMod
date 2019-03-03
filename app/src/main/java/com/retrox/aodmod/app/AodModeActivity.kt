package com.retrox.aodmod.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import org.jetbrains.anko.*

class AodModeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollView {
            verticalLayout {
                textView {
                    text = "这是什么功能？"
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = "这里可以控制手机的息屏模式，分为系统增强模式 和 常亮模式"
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = "系统增强模式"
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = "从一加原有息屏的基础上进行优化，抬手息屏的底部显示系统正在播放的音乐。息屏通知界面优化，比之前的更加美观，而且加上了新通知的跳动动画。"
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                button {
                    text = "点击使用系统增强模式"
                    setOnClickListener {
                        AppPref.aodMode = "SYSTEM"
                        Toast.makeText(context, "系统增强模式设置成功 回到主页重启息屏APP生效", Toast.LENGTH_SHORT).show()
                    }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.START
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }


                textView {
                    text = "Always On Display 常亮模式"
                    textColor = ContextCompat.getColor(context, R.color.colorPixelBlue)
                    textSize = 18f
                    gravity = Gravity.START
                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(8)
                    horizontalMargin = dip(12)
                }

                textView {
                    text = "对一加自带的息屏偷梁换柱，换成了我自己完全重写的息屏界面。优化了主界面的耗电和防烧屏，同时优化了主界面的通知显示。\n " +
                            "新特性：\n1.支持通知驻留，收到的最后一条通知会以小字的形式留在息屏主界面，更加方便的看到，避免错失通知 \n" +
                            "2.全面的动画效果，新通知到来，出现通知驻留的变化过程都有流畅的动画显示 \n" +
                            "3.音乐息屏，系统播放的音乐会显示在息屏上 \n" +
                            "4.睡眠模式，睡眠时候启用睡眠模式避免屏幕常亮的麻烦 \n" +
                            "5.低功耗，虽说不及关屏，但是整体无后台任务，屏幕处于低功耗状态 \n" +
                            "其他事项：此模式还在开发中，可能有些bug 测试期间免费提供 Enjoy！"
                    gravity = Gravity.START
                    textColor = Color.BLACK
                    textSize = 16f

                }.lparams(width = matchParent, height = wrapContent) {
                    verticalMargin = dip(12)
                    horizontalMargin = dip(8)
                }

                button {
                    text = "点击使用Always On Display"
                    setOnClickListener {
                        AppPref.aodMode = "ALWAYS_ON"
                        Toast.makeText(context, "Always On 常亮模式设置成功 回到主页重启息屏APP生效", Toast.LENGTH_SHORT).show()
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