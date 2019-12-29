package com.retrox.aodmod.proxy.layer.handon

import android.content.Context
import android.view.View
import com.retrox.aodmod.proxy.DreamProxy
import com.retrox.aodmod.proxy.layer.AbsLayer
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout

class HandonLayer(private val dreamProxy: DreamProxy) : AbsLayer {
    override val context: Context
        get() = dreamProxy.context

    override fun onCreateView(): View {
        context.verticalLayout {
            textView {
                text = "Hand Pins"
            }.lparams(wrapContent, wrapContent) {
            }

            leftPadding = dip(24)
        }
        return null!!
    }

    override fun onDestroyView() {

    }

}