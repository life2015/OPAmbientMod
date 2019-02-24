package com.retrox.aodmod.state

import android.media.MediaMetadata
import com.retrox.aodmod.data.NowPlayingMediaData

object AodState {
    var isImportantMessage = false

    private var currentDisplayState = 0 // 0 -> Screen Off  2 -> Screen ON

    fun setDisplayState(state: Int) {
        currentDisplayState = state
    }
    fun getDisplayState() = currentDisplayState

    var mediaMetadata: NowPlayingMediaData? = null

}