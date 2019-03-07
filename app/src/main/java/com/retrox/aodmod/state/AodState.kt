package com.retrox.aodmod.state

import android.arch.lifecycle.MutableLiveData
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.receiver.PowerData

object AodState {
    var isImportantMessage = false

    private var currentDisplayState = 0 // 0 -> Screen Off  2 -> Screen ON

    fun setDisplayState(state: Int) {
        currentDisplayState = state
    }
    fun getDisplayState() = currentDisplayState

    var mediaMetadata: NowPlayingMediaData? = null

    // ACTIVE  STOP
    val dreamState = MutableLiveData<String>()
    object DreamState {
        const val ACTIVE = "ACTIVE"
        const val DOZE = "DOZE"
        const val SCREENOFF = "SCREENOFF"
        const val STOP = "STOP"
    }

    var sleepMode = false // todo 优化传感器相关内容

    // DOZE OFF ON
    val screenState = MutableLiveData<String>()

    val powerState = MutableLiveData<PowerData>()

    val aodThreeKeyState = MutableLiveData<Int>() // 0 -> changing 1 -> mute 2 -> vibrate 3 -> ring
}