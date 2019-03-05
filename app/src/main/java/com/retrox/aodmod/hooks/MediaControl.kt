package com.retrox.aodmod.hooks

import android.app.AndroidAppHelper
import android.content.Intent
import android.media.MediaMetadata
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.data.NowPlayingMediaData
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

object MediaControl : IXposedHookLoadPackage {
    var metadata: MediaMetadata? = null
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
//        if (!(lpparam.packageName.contains("com.netease.cloudmusic") || lpparam.packageName.contains("com.tencent.qqmusic"))) return
        MainHook.logD("Hook -> Try MediaControl Hook in ${lpparam.packageName}")

        val classLoader: ClassLoader = lpparam.classLoader
        val mediaSessionClass = XposedHelpers.findClass("android.media.session.MediaSession", classLoader)

        XposedHelpers.findAndHookMethod(
            mediaSessionClass,
            "setMetadata",
            MediaMetadata::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val mediaMetadata = param.args[0] as MediaMetadata

                    val albumName = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
                    val artist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
                    val name = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE)
                    MainHook.logD("media: $albumName $artist $name")

                    val nowPlayingMediaData = NowPlayingMediaData(album = albumName, name = name, artist = artist)

                    val application = AndroidAppHelper.currentApplication()
                    val intent = Intent("com.retrox.aodmod.NEW_MEDIA_META")
                    intent.putExtra("mediaMetaData", nowPlayingMediaData)
                    application.applicationContext.sendBroadcast(intent)

                    val intentPulsing = Intent("com.oneplus.aod.doze.pulse")
                    application.applicationContext.sendBroadcast(intentPulsing)
                }
            })


        XposedHelpers.findAndHookMethod(
            mediaSessionClass,
            "setActive",
            Boolean::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val active = param.args[0] as Boolean

                    MainHook.logD("Media set Active Status: $active")

                }
            })

        XposedHelpers.findAndHookMethod(
            mediaSessionClass,
            "release",
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {

                    MainHook.logD("Media Session Release")

                }
            })

    }

    fun saveMetaData(data: MediaMetadata) {
        metadata = data
        MainHook.logD("mediaMeta: $data")
    }

}