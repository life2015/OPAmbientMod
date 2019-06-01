package com.retrox.aodmod.proxy.view

import android.view.View
import com.retrox.aodmod.proxy.AbsDreamView
import com.retrox.aodmod.proxy.DreamProxy
import java.util.*

class AodDefaultDream(dreamProxy: DreamProxy) : AbsDreamView(dreamProxy) {
    override val layoutTheme: String
        get() = "Default"

    override fun onCreateView(): View {
        return context.aodMainView(this)
    }

    override fun onAvoidScreenBurnt(mainView: View, lastTime: Long) {
        val vertical = Random().nextInt(50)
        val horizontal = Random().nextInt(20) - 10

        mainView.animate()
            .translationX(horizontal.toFloat())
            .translationY(-vertical.toFloat())
            .setDuration(if (lastTime == 0L) /*加入初始位移 避免烧屏*/ 0L else 800L)
            .start()
    }

}