package com.retrox.aodmod.proxy.view

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.state.AodState
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.imageView

fun Context.aodThreeKeyView(lifecycleOwner: LifecycleOwner): FrameLayout {
    return frameLayout {
        imageView {
            visibility = View.INVISIBLE

            val runnable = Runnable { this@imageView.visibility = View.INVISIBLE }

            AodState.aodThreeKeyState.observeNew(lifecycleOwner, Observer {
                it?.let { state ->
                    val resourceUtils = ResourceUtils.getInstance(this)
                    val drawable = when (state) {
                        1 -> resourceUtils.getDrawable(R.drawable.ic_notifications_off)
                        2 -> resourceUtils.getDrawable(R.drawable.ic_vibration)
                        3 -> resourceUtils.getDrawable(R.drawable.ic_notifications_active)
                        else -> null
                    }

                    drawable?.let {
                        removeCallbacks(runnable)
                        this.visibility = View.VISIBLE
                        this.setImageDrawable(it)

                        postDelayed(runnable, 4000L)
                    }
                }
            })
        }.lparams(dip(36), dip(36)) {
            rightMargin = dip(16)
        }
    }
}