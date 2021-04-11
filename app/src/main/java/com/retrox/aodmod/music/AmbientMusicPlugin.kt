package com.retrox.aodmod.music

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.util.Log
import com.retrox.aodmod.app.settings.models.NowPlayingAmbientTrack
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.state.AodMedia
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AmbientMusicPlugin {

    companion object {

        private const val AMBIENT_MUSIC_MOD_PACKAGE_NAME = "com.kieronquinn.app.ambientmusicmod"
        private const val PIXEL_AMBIENT_SERVICES_PACKAGE_NAME = "com.google.intelligence.sense"

        val AMBIENT_MUSIC_PACKAGES = arrayOf(AMBIENT_MUSIC_MOD_PACKAGE_NAME, PIXEL_AMBIENT_SERVICES_PACKAGE_NAME)

        private const val INTENT_ACTION_AMBIENT_INDICATION_SHOW = "com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW"
        private const val INTENT_ACTION_AMBIENT_INDICATION_HIDE = "com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE"

        private const val INTENT_EXTRA_AMBIENT_INDICATION_TEXT = "com.google.android.ambientindication.extra.TEXT"
        private const val INTENT_EXTRA_AMBIENT_INDICATION_TTL_MILLIS = "com.google.android.ambientindication.extra.TTL_MILLIS"

        private const val SECURE_BROADCAST_RECEIVER_EXTRA_PENDING_INTENT = "verification_intent"
        private val SECURE_BROADCAST_PACKAGE_WHITELIST = AMBIENT_MUSIC_PACKAGES

    }

    private val nowPlayingShowReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val pendingIntent = intent.getParcelableExtra<PendingIntent>(SECURE_BROADCAST_RECEIVER_EXTRA_PENDING_INTENT) ?: return
            if(!SECURE_BROADCAST_PACKAGE_WHITELIST.contains(pendingIntent.creatorPackage)) return
            if(!XPref.getAmbientMusic()) return
            val text = intent.getStringExtra(INTENT_EXTRA_AMBIENT_INDICATION_TEXT) ?: return
            val ttlMillis = intent.getLongExtra(INTENT_EXTRA_AMBIENT_INDICATION_TTL_MILLIS, 180000L)
            AodMedia.aodAmbientNowPlayingLiveData.postValue(NowPlayingAmbientTrack(text, ttlMillis), ttlMillis)
        }
    }

    private val nowPlayingHideReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val pendingIntent = intent.getParcelableExtra<PendingIntent>(SECURE_BROADCAST_RECEIVER_EXTRA_PENDING_INTENT) ?: return
            if(!SECURE_BROADCAST_PACKAGE_WHITELIST.contains(pendingIntent.creatorPackage)) return
            AodMedia.aodAmbientNowPlayingLiveData.postValue(null)
        }
    }

    fun setupReceivers(context: Context){
        context.registerReceiver(nowPlayingShowReceiver, IntentFilter(INTENT_ACTION_AMBIENT_INDICATION_SHOW))
        context.registerReceiver(nowPlayingHideReceiver, IntentFilter(INTENT_ACTION_AMBIENT_INDICATION_HIDE))
    }

}
