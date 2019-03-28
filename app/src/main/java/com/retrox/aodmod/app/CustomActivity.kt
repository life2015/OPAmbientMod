package com.retrox.aodmod.app

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import com.retrox.aodmod.R
import com.retrox.aodmod.app.pref.AppPref
import com.retrox.aodmod.proxy.view.custom.dvd.BallView
import org.jetbrains.anko.*

class CustomActivity : AppCompatActivity() {
    val themeLayoutList = listOf("Default", "Flat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollView {
            verticalLayout {
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    textView("显示风格")
                    spinner {
                        background.setColorFilter(Color.parseColor("#568FFF"), PorterDuff.Mode.SRC_ATOP)
                        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, themeLayoutList)
                        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {}

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val style = themeLayoutList[position]
                                AppPref.aodLayoutTheme = style
                                Toast.makeText(context, "主题已设置 $style", Toast.LENGTH_SHORT).show()
                            }
                        }
                        setSelection(themeLayoutList.indexOf(AppPref.aodLayoutTheme), true)
                    }
                }

                val view  = BallView(context)
                view.lparams(dip(300), dip(300))
                addView(view)
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