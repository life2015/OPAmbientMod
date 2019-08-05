package com.retrox.aodmod.doze

import android.graphics.Color
import android.service.dreams.DreamService
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

class RetroDream : DreamService() {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        isInteractive = false
        isFullscreen = true

        val view = frameLayout {
            textView {
                text = "Hello From Magisk Dream"
                textSize = 30f
                textColor = Color.WHITE
            }
        }
        setContentView(view)
    }
}