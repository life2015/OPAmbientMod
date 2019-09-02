package com.retrox.aodmod.proxy.view.custom.flat

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.retrox.aodmod.extensions.setGoogleSans
import com.retrox.aodmod.extensions.setGradientTest
import com.retrox.aodmod.proxy.view.Ids
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import com.retrox.aodmod.remote.lyric.LrcSync
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

fun Context.flatAodMainView(lifecycleOwner: LifecycleOwner): View {
    return frameLayout {
        backgroundColor = Color.BLACK

        constraintLayout {
            id = Ids.ly_main

            val clockView = flatStyleAodClock(lifecycleOwner).apply {
                id = AodFlatDream.clock
            }.lparams(width = matchParent, height = wrapContent) {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = dip(140)
                leftMargin = dip(36)
            }
            addView(clockView)

            val bottomContainer = verticalLayout {
                id = AodFlatDream.bottomContainer

                val musicLyric = textView {
                    textColor = Color.WHITE
                    textSize = 15f
                    letterSpacing = 0.05f
                    setGoogleSans()
                    visibility = View.GONE
                    gravity = Gravity.CENTER_HORIZONTAL

                    LrcSync.currentLrcRowLive.observe(lifecycleOwner, Observer {
                        if (it == null) {
                            visibility = View.GONE
                        } else {
                            visibility = View.VISIBLE
                            text = it.content
                        }
                    })

                }.lparams(matchParent, wrapContent)


            }.lparams(matchParent, wrapContent) {
                bottomMargin = dip(24)
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                horizontalMargin = dip(44)
            }

            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                applyRecursively {
                    when(it) {
                        is TextView -> it.setGradientTest()
                        is ImageView -> it.imageTintList = ColorStateList.valueOf(Color.parseColor(ThemeManager.getCurrentColorPack().tintColor))
                    }
                }
            }

        }.lparams(width = matchParent, height = matchParent)
    }
}