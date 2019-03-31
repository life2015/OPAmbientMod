package com.retrox.aodmod.app

import android.graphics.Color
import android.graphics.PorterDuff
import android.hardware.camera2.params.ColorSpaceTransform
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.*
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.extensions.setGradientTest
import com.retrox.aodmod.proxy.view.custom.dvd.BallView
import com.retrox.aodmod.proxy.view.theme.ThemeClockPack
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import org.jetbrains.anko.*

class CustomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollView {
            backgroundColor = Color.BLACK
            verticalLayout {
                ThemeManager.getPresetThemes().forEach {
                    textView {
                        textColor = Color.WHITE
                        text = "${it.themeName} 点击使用"
                        setGradientTest(it)
                        textSize = 30f
                        tag = it

                        setOnClickListener {
                            val pack = tag as ThemeClockPack
                            ThemeManager.setThemePackSync(pack)
                            Toast.makeText(context, "主题已应用 ${pack.themeName}", Toast.LENGTH_SHORT).show()
                        }
                    }.lparams(wrapContent, wrapContent)
                }

                addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    applyRecursively {
                        if (it is TextView) {
                            if (it.tag is ThemeClockPack) {
                                it.setGradientTest(it.tag as ThemeClockPack)
                            }
                        }
                    }
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