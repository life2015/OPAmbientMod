package com.retrox.aodmod.proxy.view.custom.word

import android.arch.lifecycle.Observer
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.extensions.setGradientTest
import com.retrox.aodmod.extensions.toCNString
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.proxy.AbsDreamView
import com.retrox.aodmod.proxy.DreamProxy
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.state.AodClockTick
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import java.util.*

class WordDream(dreamProxy: DreamProxy) : AbsDreamView(dreamProxy) {
    override val layoutTheme: String
        get() = "Word"


    override fun onCreateView(): View {
        return context.constraintLayout {

            verticalLayout {


                val timeTextView = textView {
                    text = ""
                    textSize = 34f
                    setLineSpacing(8f, 1f)
                }.lparams(wrapContent, wrapContent) {
                    bottomMargin = dip(16)
                }

                val dateTextView = textView {
                    text = ""
                    textSize = 18f
                }.lparams(wrapContent, wrapContent)

                AodClockTick.tickLiveData.observe(this@WordDream, Observer {
                    val cal = Calendar.getInstance()
                    val hour = cal.get(Calendar.HOUR)
                    val minute = cal.get(Calendar.MINUTE)
                    val month = cal.get(Calendar.MONTH) + 1
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    val weekDay = cal.get(Calendar.DAY_OF_WEEK)

                    timeTextView.text = "${hour.toCNString()}时\n${minute.toCNString()}分\nです"
                    val weekDayStr = if (weekDay == 1) "日" else ((weekDay - 1).toCNString())
                    val text = "${month.toCNString()}月${day.toCNString()}日 周${weekDayStr}" + " "
                    MainHook.logD(text)
                    dateTextView.text = "${month.toCNString()}月${day.toCNString()}日 周${weekDayStr}" + " "
                })

                textView {// 备忘
                    textColor = Color.WHITE
                    setGoogleSans()
                    letterSpacing = 0.02f
                    textSize = 16f

                    maxWidth = dip(260)
                    visibility = View.GONE
                    if (XPref.getAodShowNote() && !XPref.getAodNoteContent().isNullOrBlank()) {
                        visibility = View.VISIBLE
                        text = XPref.getAodNoteContent()
                    }
                }.lparams(wrapContent, wrapContent) {
                    bottomMargin = dip(6)
                }


            }.lparams(width = wrapContent, height = wrapContent) {
                startToStart = PARENT_ID
                topToTop = PARENT_ID
                topMargin = dip(200)
                marginStart = dip(40)
            }

            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                applyRecursively {
                    when (it) {
                        is TextView -> it.setGradientTest()
                        is ImageView -> it.imageTintList =
                            ColorStateList.valueOf(Color.parseColor(ThemeManager.getCurrentColorPack().tintColor))
                    }
                }
            }
        }
    }

    override fun onAvoidScreenBurnt(mainView: View, lastTime: Long) {
        val horizontal = 0 // 这个模式避免左右移动
        val vertical = Random().nextInt(400) - 300 // 更大的移动范围 (-300, 100)

        mainView.animate()
            .translationX(horizontal.toFloat())
            .translationY(-vertical.toFloat())
            .setDuration(if (lastTime == 0L) /*加入初始位移 避免烧屏*/ 0L else 800L)
            .start()    }

}