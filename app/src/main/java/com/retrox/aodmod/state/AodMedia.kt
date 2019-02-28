package com.retrox.aodmod.state

import android.arch.lifecycle.MutableLiveData
import com.retrox.aodmod.data.NowPlayingMediaData

object AodMedia {
    val aodMediaLiveData = MutableLiveData<NowPlayingMediaData>()
}