package com.retrox.aodmod.proxy.view.custom.dvd

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.graphics.Color
import android.view.View
import com.retrox.aodmod.proxy.AbsDreamView
import com.retrox.aodmod.proxy.DreamProxy
import com.retrox.aodmod.proxy.view.Ids
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent

fun Context.dvdAodMainView(lifecycleOwner: LifecycleOwner): View {
    return frameLayout {
        backgroundColor = Color.BLACK

        frameLayout {
            id = Ids.ly_main
        }

        val view  = BallView(context)
        view.lparams(matchParent, matchParent)
        addView(view)

    }
}

class AodDVDDream(dreamProxy: DreamProxy) : AbsDreamView(dreamProxy) {
    override val layoutTheme: String
        get() = "DVD"

    override fun onCreateView(): View {
        return context.dvdAodMainView(this)
    }

    override fun onAvoidScreenBurnt(mainView: View, lastTime: Long) {
    }

}