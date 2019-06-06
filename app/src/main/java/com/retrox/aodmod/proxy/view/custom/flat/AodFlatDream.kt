package com.retrox.aodmod.proxy.view.custom.flat

import android.view.View
import com.retrox.aodmod.proxy.AbsDreamView
import com.retrox.aodmod.proxy.DreamProxy
import java.util.*

class AodFlatDream(dreamProxy: DreamProxy) : AbsDreamView(dreamProxy) {
    override val layoutTheme: String
        get() = "Flat"

    override fun onCreateView(): View {
        return context.flatAodMainView(this)
    }

    override fun onAvoidScreenBurnt(mainView: View, lastTime: Long) {
        val vertical = Random().nextInt(350) - 400 // 更大的移动范围 (-400, -50)
        val horizontal = Random().nextInt(100) - 20

        mainView.animate()
            .translationX(horizontal.toFloat())
            .translationY(-vertical.toFloat())
            .setDuration(if (lastTime == 0L) /*加入初始位移 避免烧屏*/ 0L else 800L)
            .start()

    }
}