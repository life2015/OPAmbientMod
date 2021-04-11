package com.retrox.aodmod.state

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.retrox.aodmod.app.settings.models.NowPlayingAmbientTrack
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.util.SelfClearingMutableLiveData

object AodMedia {
    val aodLocalNowPlayingLiveData = MutableLiveData<NowPlayingMediaData>()
    val aodAmbientNowPlayingLiveData = SelfClearingMutableLiveData<NowPlayingAmbientTrack>()

    val aodMediaLiveData = MediatorLiveData<NowPlayingMediaData>().apply {
        val update = Observer<Any>{
            when {
                aodLocalNowPlayingLiveData.value != null -> {
                    postValue(aodLocalNowPlayingLiveData.value)
                }
                aodAmbientNowPlayingLiveData.value != null -> {
                    postValue(aodAmbientNowPlayingLiveData.value?.toNowPlayingMetaData())
                }
                else -> {
                    postValue(null)
                }
            }
        }
        addSource(aodLocalNowPlayingLiveData, update)
        addSource(aodAmbientNowPlayingLiveData, update)
    }

}