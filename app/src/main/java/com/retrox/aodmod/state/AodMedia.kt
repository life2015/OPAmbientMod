package com.retrox.aodmod.state

import androidx.lifecycle.MutableLiveData
import com.retrox.aodmod.data.NowPlayingMediaData

object AodMedia {
    val aodMediaLiveData = MutableLiveData<NowPlayingMediaData>()
}