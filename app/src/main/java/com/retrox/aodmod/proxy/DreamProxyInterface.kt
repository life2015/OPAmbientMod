package com.retrox.aodmod.proxy

import android.service.dreams.DreamService

/**
 * 偷梁换柱
 */
interface DreamProxyInterface {
    val dreamService: DreamService
    fun onCreate()
    fun onAttachedToWindow()
    fun onDreamingStarted()
    fun onDreamingStopped()
    fun onWakingUp()
    fun onSingleTap()
}

