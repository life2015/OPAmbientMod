package com.retrox.aodmod.proxy.view.custom.flat

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewManager
import android.widget.TextView
import de.robv.android.xposed.XposedHelpers
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.singleLine

class MarqueTextView : TextView {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun isFocused(): Boolean {
        return true
    }

    init {
        setMarqueeTime()

        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        focusable = View.FOCUSABLE
        isFocusableInTouchMode = true
        singleLine = true
    }

    private fun setMarqueeTime() {
        val clazz = XposedHelpers.findClass("android.widget.TextView.Marquee", javaClass.classLoader)
        XposedHelpers.setStaticIntField(clazz, "MARQUEE_DELAY", 100000)
    }
}

inline fun ViewManager.marqueTextView(init: MarqueTextView.() -> Unit): MarqueTextView {
    return ankoView({ MarqueTextView(it) }, theme = 0) {
        init()
    }
}

