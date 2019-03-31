package com.retrox.aodmod.proxy.view.custom.flat

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.retrox.aodmod.extensions.setGradientTest
import com.retrox.aodmod.proxy.view.Ids
import com.retrox.aodmod.proxy.view.theme.ThemeManager
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

fun Context.sumSungAodMainView(lifecycleOwner: LifecycleOwner): View {
    return frameLayout {
        backgroundColor = Color.BLACK

        constraintLayout {
            id = Ids.ly_main

            val clockView = flatStyleAodClock(lifecycleOwner).apply {
                id = Ids.ly_clock
            }.lparams(width = matchParent, height = wrapContent) {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = dip(140)
                leftMargin = dip(36)
            }
            addView(clockView)

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