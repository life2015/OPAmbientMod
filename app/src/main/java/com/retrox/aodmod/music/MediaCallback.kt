package com.retrox.aodmod.music

import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.hooks.LyricHelper
import com.retrox.aodmod.state.AodMedia

class MediaCallback(private val mediaController: MediaController, private val context: Context) : MediaController.Callback() {

    private var playingMediaData : NowPlayingMediaData? = null
    private var playingState: Int = PlaybackState.STATE_NONE

    override fun onPlaybackStateChanged(state: PlaybackState?) {
        super.onPlaybackStateChanged(state)
        playingState = state?.state ?: PlaybackState.STATE_NONE
        updateMusicPlaybackState()
    }

    override fun onMetadataChanged(metadata: MediaMetadata?) {
        super.onMetadataChanged(metadata)
        metadata?.let {
            val track = it.getString(MediaMetadata.METADATA_KEY_TITLE)
            val artist = it.getString(MediaMetadata.METADATA_KEY_ARTIST)
            val album = it.getString(MediaMetadata.METADATA_KEY_ALBUM)
            val app = mediaController.packageName
            playingMediaData = if(artist != null && track != null && album != null && app != null){
                NowPlayingMediaData(track, artist, album, app)
            }else{
                null
            }
        } ?: run {
            playingMediaData = null
        }
        updateMusicPlaybackState()
    }

    private fun updateMusicPlaybackState(){
        if(playingState == PlaybackState.STATE_PLAYING) {
            AodMedia.aodMediaLiveData.postValue(playingMediaData)
            playingMediaData?.let {
                LyricHelper.queryMusic2(it.artist, it.name)
                val intentPulsing = Intent("com.oneplus.aod.doze.pulse")
                context.sendBroadcast(intentPulsing)
            }
        }else{
            AodMedia.aodMediaLiveData.postValue(null)
        }
    }

}