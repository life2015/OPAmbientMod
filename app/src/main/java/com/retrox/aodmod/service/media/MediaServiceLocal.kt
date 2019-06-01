package com.retrox.aodmod.service.media

import android.app.AndroidAppHelper
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Bundle
import com.retrox.aodmod.MainHook

object MediaServiceLocal {
    val mediaSessionManager = AndroidAppHelper.currentApplication().getSystemService(MediaSessionManager::class.java)
    val callback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            MainHook.logD("meta Changed ${metadata} $this")
            super.onMetadataChanged(metadata)
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            MainHook.logD("Play State Changed ${state} $this")

            super.onPlaybackStateChanged(state)
        }

        override fun onExtrasChanged(extras: Bundle?) {
            MainHook.logD("Play State Extra ${extras} $this")

            super.onExtrasChanged(extras)
        }
    }
    init {
//        getActiveSessions()
        test()
    }



    fun getActiveSessions() {
        mediaSessionManager
        mediaSessionManager.getActiveSessions(null).forEach {
            MainHook.logD(it.packageName + "2222")

            val mediaMetadata = it.metadata
            if (mediaMetadata != null) {
                val albumName = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""
                val artist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
                val name = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
                MainHook.logD("media from MediaService Local: $albumName $artist $name ${it.packageName}")
            }

            it.unregisterCallback(callback)
            it.registerCallback(callback)

        }
    }

    fun test() {
        mediaSessionManager.addOnActiveSessionsChangedListener(object : MediaSessionManager.OnActiveSessionsChangedListener {
            override fun onActiveSessionsChanged(controllers: MutableList<MediaController>?) {
                MainHook.logD("MediaSession Changed")
                controllers?.forEach {
                    MainHook.logD(it.packageName + "Active Session Changed")
                }
            }
        }, null, null)
    }

}