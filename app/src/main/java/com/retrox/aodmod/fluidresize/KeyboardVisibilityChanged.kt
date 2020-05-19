package com.retrox.aodmod.fluidresize

data class KeyboardVisibilityChanged(
    val visible: Boolean,
    val contentHeight: Int,
    val contentHeightBeforeResize: Int
)
