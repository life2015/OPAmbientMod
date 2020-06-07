package com.retrox.aodmod.hooks

import android.app.AndroidAppHelper
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.PlaybackState
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.R
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.pref.XPref
import com.retrox.aodmod.remote.lyric.model.CommonLyricProvider
import com.retrox.aodmod.remote.lyric.model.SongEntity
import com.retrox.aodmod.util.ToggleableXC_MethodHook
import com.retrox.aodmod.util.XC_MethodHook
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object MediaControl : IXposedHookLoadPackage {
    var metadata: MediaMetadata? = null
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "android") return
//        if (!(lpparam.packageName.contains("com.netease.cloudmusic") || lpparam.packageName.contains("com.tencent.qqmusic"))) return
        MainHook.logD("Hook -> Try MediaControl Hook in ${lpparam.packageName}")

        val classLoader: ClassLoader = lpparam.classLoader
        val mediaSessionClass =
            XposedHelpers.findClass("android.media.session.MediaSession", classLoader)

        XposedHelpers.findAndHookMethod(
            mediaSessionClass,
            "setMetadata",
            MediaMetadata::class.java,
            ToggleableXC_MethodHook(object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val mediaMetadata = param.args[0] as? MediaMetadata ?: return

                    val albumName = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""
                    val artist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
                    val name = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
                    MainHook.logD("media: $albumName $artist $name ${lpparam.packageName}")

                    // SB B站 专辑和名字都是混的
                    val nowPlayingMediaData =
                        if (lpparam.packageName == "tv.danmaku.bili" && name == "") {
                            NowPlayingMediaData(album = artist, name = name, artist = albumName)
                        } else NowPlayingMediaData(
                            album = albumName,
                            name = name,
                            artist = artist,
                            app = lpparam.packageName
                        )

                    val application = AndroidAppHelper.currentApplication()
                    val intent = Intent("com.retrox.aodmod.NEW_MEDIA_META")
                    intent.putExtra("mediaMetaData", nowPlayingMediaData)
                    application.applicationContext.sendBroadcast(intent)

                    LyricHelper.queryMusic2(artist, name)

                    val intentPulsing = Intent("com.oneplus.aod.doze.pulse")
                    application.applicationContext.sendBroadcast(intentPulsing)
                }
            }))

        XposedHelpers.findAndHookMethod(
            mediaSessionClass,
            "setPlaybackState",
            "android.media.session.PlaybackState",
            ToggleableXC_MethodHook(object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    MainHook.logD(param.args[0].toString())

                    val application = AndroidAppHelper.currentApplication()
                    val intent = Intent("com.retrox.aodmod.NEW_PLAY_STATE")
                    intent.putExtra("mediaPlayBackState", param.args[0] as PlaybackState)
                    application.applicationContext.sendBroadcast(intent)
                }
            }))


        XposedHelpers.findAndHookMethod(
            mediaSessionClass,
            "setActive",
            Boolean::class.java,
            ToggleableXC_MethodHook(object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val active = param.args[0] as Boolean
                    MainHook.logD("Media set Active Status: $active")

                }
            }))

        XposedHelpers.findAndHookMethod(
            mediaSessionClass,
            "release",
            ToggleableXC_MethodHook(object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    MainHook.logD("Media Session Release")

                }
            }))

    }

    fun saveMetaData(data: MediaMetadata) {
        metadata = data
        MainHook.logD("mediaMeta: $data")
    }

}

object LyricHelper {

    fun queryMusic2(artist: String, name: String) {
        if(!XPref.getLyricsEnabled()) return
        val songEntity = SongEntity(name, artist)
        GlobalScope.launch(Dispatchers.Main + handler) {
            val lyric = CommonLyricProvider.fetchLyric(song = songEntity) ?: "[00:00.000] ${ResourceUtils.getInstance().getString(R.string.lyrics_loading)}\n[00:10.000] "

            val application = AndroidAppHelper.currentApplication()
            val intent = Intent("com.retrox.aodmod.NEW_MEDIA_LRC")
            intent.putExtra("mediaLyric", lyric)
            application.applicationContext.sendBroadcast(intent)
        }
    }

    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        MainHook.logE("歌词获取错误", t = throwable)
        throwable.printStackTrace()
    }
}