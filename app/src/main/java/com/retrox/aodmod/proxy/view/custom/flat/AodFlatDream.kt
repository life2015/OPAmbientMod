package com.retrox.aodmod.proxy.view.custom.flat

import android.view.View
import com.retrox.aodmod.proxy.AbsDreamView
import com.retrox.aodmod.proxy.DreamProxy
import java.util.*

class AodFlatDream(dreamProxy: DreamProxy) : AbsDreamView(dreamProxy) {

    companion object {
        val bottomContainer = View.generateViewId()
        val clock = View.generateViewId()
    }

    override val layoutTheme: String
        get() = "Flat"

    override fun onCreateView(): View {
        return context.flatAodMainView(this)
    }

    override fun onAvoidScreenBurnt(mainView: View, lastTime: Long) {
        val vertical = Random().nextInt(450) - 500 // 更大的移动范围 (-500, -50)
        val horizontal = Random().nextInt(100) - 20

        val originalView = mainView.findViewById<View>(clock)

        originalView.animate()
            .translationX(horizontal.toFloat())
            .translationY(-vertical.toFloat())
            .setDuration(if (lastTime == 0L) /*加入初始位移 避免烧屏*/ 0L else 800L)
            .start()

        val musicLrcView = mainView.findViewById<View>(bottomContainer)
        val vertical2 = Random().nextInt(40) - 12  // 更大的移动范围 (-12, 28)
        musicLrcView.animate()
            .translationY(-vertical2.toFloat())
            .setDuration(if (lastTime == 0L) /*加入初始位移 避免烧屏*/ 0L else 800L)
            .start()
    }
}