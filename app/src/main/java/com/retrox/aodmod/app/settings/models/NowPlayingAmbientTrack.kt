package com.retrox.aodmod.app.settings.models

import com.retrox.aodmod.data.NowPlayingMediaData

data class NowPlayingAmbientTrack(val text: String, val ttlMillis: Long){

    fun toNowPlayingMetaData(): NowPlayingMediaData {
        return NowPlayingMediaData("", "", "", "", text, ttlMillis)
    }

}
