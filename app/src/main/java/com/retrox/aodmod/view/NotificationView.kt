package com.retrox.aodmod.view

import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.retrox.aodmod.proxy.view.custom.flat.flatStyleAodClock

class NotificationView(context: Context, attributes: AttributeSet?, styleRes: Int) : FrameLayout(context, attributes, styleRes) {

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        getActivity()?.let {
            addView(context.flatStyleAodClock(it))
        }
    }

    fun getActivity(): LifecycleOwner? {
        var context = context
        while (context is ContextWrapper) {
            if (context is LifecycleOwner) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

}