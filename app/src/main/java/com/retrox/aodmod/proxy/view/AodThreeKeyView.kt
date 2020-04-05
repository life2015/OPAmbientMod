package com.retrox.aodmod.proxy.view

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.transition.TransitionManager
import android.view.View
import android.widget.FrameLayout
import com.retrox.aodmod.MainHook
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

            val runnable = Runnable {
                TransitionManager.beginDelayedTransition(this@frameLayout)
                this@imageView.visibility = View.INVISIBLE
            }

            AodState.aodThreeKeyState.observeNewOnly(lifecycleOwner, Observer {
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
                        TransitionManager.beginDelayedTransition(this@frameLayout)
                        this.visibility = View.VISIBLE
                        this.setImageDrawable(it)

                        postDelayed(runnable, 3000L)
                    }
                }
            })
        }.lparams(dip(28), dip(28)) {
        }
    }
}