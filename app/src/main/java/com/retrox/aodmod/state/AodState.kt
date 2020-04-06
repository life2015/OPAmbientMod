package com.retrox.aodmod.state

import androidx.lifecycle.MutableLiveData
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.extensions.LiveEvent
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
        const val SCREENDOZE = "SCREENDOZE"
        const val SCREENOFF = "SCREENOFF"
        const val STOP = "STOP"
    }

    var sleepMode = false // todo 优化传感器相关内容

    // DOZE OFF ON 来自Display的参数
    val screenState = MutableLiveData<Int>()

    val powerState = MutableLiveData<PowerData>()

    val aodThreeKeyState = LiveEvent<Int>() // 0 -> changing 1 -> mute 2 -> vibrate 3 -> ring

    val singleTapLiveEvent = LiveEvent<String>()
}