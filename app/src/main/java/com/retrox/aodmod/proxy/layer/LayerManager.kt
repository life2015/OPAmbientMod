package com.retrox.aodmod.proxy.layer

import android.content.Context
import android.view.View
import android.view.WindowManager

fun WindowManager.addAbsLayer(layer: AbsLayer, layoutParams: WindowManager.LayoutParams): View {
    val view = layer.onCreateView()
    addView(view, layoutParams)
    return view
}

interface AbsLayer {
    val context: Context
    fun onCreateView(): View
    fun onDestroyView()
}