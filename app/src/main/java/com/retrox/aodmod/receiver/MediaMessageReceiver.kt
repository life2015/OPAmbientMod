package com.retrox.aodmod.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.state.AodState

class MediaMessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mediaMetadata = intent.getParcelableExtra<NowPlayingMediaData>("mediaMetaData")
        AodState.mediaMetadata = mediaMetadata
        MainHook.logD("received media meta broadCast: $mediaMetadata")
    }
}