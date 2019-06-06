package com.retrox.aodmod.hooks

import android.app.AndroidAppHelper
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.os.Environment
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.remote.lyric.DefaultLrcBuilder
import com.retrox.aodmod.remote.lyric.NEMDownloader
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.coroutines.*

object MediaControl : IXposedHookLoadPackage {
    var metadata: MediaMetadata? = null
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "android") return
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
                    val mediaMetadata = param.args[0] as? MediaMetadata ?: return

                    val albumName = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""
                    val artist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: ""
                    val name = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: ""
                    MainHook.logD("media: $albumName $artist $name ${lpparam.packageName}")

                    // SB B站 专辑和名字都是混的
                    val nowPlayingMediaData = if (lpparam.packageName == "tv.danmaku.bili" && name == "") {
                        NowPlayingMediaData(album = artist, name = name, artist = albumName)
                    } else NowPlayingMediaData(album = albumName, name = name, artist = artist, app = lpparam.packageName)

                    val application = AndroidAppHelper.currentApplication()
                    val intent = Intent("com.retrox.aodmod.NEW_MEDIA_META")
                    intent.putExtra("mediaMetaData", nowPlayingMediaData)
                    application.applicationContext.sendBroadcast(intent)

                    LyricHelper.queryMusic(artist, name)
//                    val file = AndroidAppHelper.currentApplication().getExternalFilesDir(Environment.MEDIA_MOUNTED)
//                    MainHook.logD(file.toString())

                    val intentPulsing = Intent("com.oneplus.aod.doze.pulse")
                    application.applicationContext.sendBroadcast(intentPulsing)
                }
            })

        XposedHelpers.findAndHookMethod(mediaSessionClass, "setPlaybackState", "android.media.session.PlaybackState", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                MainHook.logD(param.args[0].toString())

                val application = AndroidAppHelper.currentApplication()
                val intent = Intent("com.retrox.aodmod.NEW_PLAY_STATE")
                intent.putExtra("mediaPlayBackState", param.args[0] as PlaybackState)
                application.applicationContext.sendBroadcast(intent)
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

object LyricHelper {
    fun queryMusic(artist: String, name: String) {
        GlobalScope.launch(Dispatchers.Main + handler) {
            val result = withContext(Dispatchers.IO) {
                val arrayResult = NEMDownloader.query(artist, name)
                val bestMatch = arrayResult.find {
                    it.title == name
                }
                val first = bestMatch ?: arrayResult.firstOrNull()

                first?.let {
                    var raw = NEMDownloader.download(first, false)
                    if (raw.isNullOrBlank()) {
                        raw = "[00:00.000] 歌词获取错误"
                    }
                    val application = AndroidAppHelper.currentApplication()
                    val intent = Intent("com.retrox.aodmod.NEW_MEDIA_LRC")
                    intent.putExtra("mediaLyric", raw)
                    application.applicationContext.sendBroadcast(intent)
                    raw
                }
            }
        }
    }

    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        MainHook.logE("歌词获取错误", t = throwable)
    }
}