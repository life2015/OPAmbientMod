package com.retrox.aodmod.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.remote.lyric.LrcSync
import com.retrox.aodmod.state.AodMedia
import com.retrox.aodmod.state.AodState

class MediaMessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        MainHook.logD(intent.toString())
        when (intent.action) {
            "com.retrox.aodmod.NEW_MEDIA_META" -> {
                val mediaMetadata = intent.getParcelableExtra<NowPlayingMediaData>("mediaMetaData")
                AodState.mediaMetadata = mediaMetadata
                if (mediaMetadata != AodMedia.aodMediaLiveData.value) {
                    AodMedia.aodMediaLiveData.postValue(mediaMetadata)
                }
                MainHook.logD("received media meta broadCast: $mediaMetadata")
//                LrcSync.clearLrcRows()
            }
            "com.retrox.aodmod.NEW_PLAY_STATE" -> {
                val mediaPlaybackState = intent.getParcelableExtra<PlaybackState>("mediaPlayBackState")
                // todo 处理暂停的情况
                when(mediaPlaybackState.state) {
                    PlaybackState.STATE_PAUSED -> {
                        LrcSync.stopSync()
                    }
                    PlaybackState.STATE_PLAYING -> {
                        LrcSync.startLrcSync(mediaPlaybackState.position)
                    }
                }
            }
            "com.retrox.aodmod.NEW_MEDIA_LRC" -> {
                val rawLrc = intent.getStringExtra("mediaLyric")
                LrcSync.applyRawLrc(rawLrc)
            }
        }
    }
}