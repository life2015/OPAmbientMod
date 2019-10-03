package com.retrox.aodmod.hooks

import android.app.AndroidAppHelper
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.PlaybackState
import com.google.gson.Gson
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.data.NowPlayingMediaData
import com.retrox.aodmod.extensions.genericType
import com.retrox.aodmod.remote.lyric.NEMDownloader
import com.retrox.aodmod.remote.lyric.QueryResult
import com.retrox.aodmod.remote.lyric.model.CommonLyricProvider
import com.retrox.aodmod.remote.lyric.model.SongEntity
import com.retrox.aodmod.shared.global.GlobalCacheManager
import com.retrox.aodmod.shared.global.GlobalKV
import com.retrox.aodmod.shared.global.OwnFileManager
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

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
            object : XC_MethodHook() {
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
//                    val file = AndroidAppHelper.currentApplication().getExternalFilesDir(Environment.MEDIA_MOUNTED)
//                    MainHook.logD(file.toString())

                    val intentPulsing = Intent("com.oneplus.aod.doze.pulse")
                    application.applicationContext.sendBroadcast(intentPulsing)
                }
            })

        XposedHelpers.findAndHookMethod(
            mediaSessionClass,
            "setPlaybackState",
            "android.media.session.PlaybackState",
            object : XC_MethodHook() {
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
    val cacheMap = ConcurrentHashMap<String, QueryResult>()
    val cacheKey = "LyricIdMap.cache"
    var initialCacheSize = 0

    fun queryMusic2(artist: String, name: String) {
        val songEntity = SongEntity(name, artist)
        GlobalScope.launch(Dispatchers.Main + handler) {
            val lyric = CommonLyricProvider.fetchLyric(song = songEntity) ?: "[00:00.000] 歌词获取错误，请尝试更换网络\n[00:10.000] "

            val application = AndroidAppHelper.currentApplication()
            val intent = Intent("com.retrox.aodmod.NEW_MEDIA_LRC")
            intent.putExtra("mediaLyric", lyric)
            application.applicationContext.sendBroadcast(intent)
        }
    }

    fun queryMusic(artist: String, name: String) {
        GlobalScope.launch(Dispatchers.Main + handler) {
            val result = async (Dispatchers.IO) {
                // Load Cached Map
                if (cacheMap.isEmpty()) {
                    val str = GlobalCacheManager.readCache(cacheKey)
                    MainHook.logD("Cache Str: $str")
                    str?.let {
                        // Pair<String, String> 无法被反序列化
                        val type = genericType<HashMap<String, QueryResult>>()
                        val tempCacheMap = Gson().fromJson<HashMap<String, QueryResult>>(it, type)
                        initialCacheSize = tempCacheMap.size
                        cacheMap.putAll(tempCacheMap)
                        MainHook.logD("本地加载LrcMap缓存Size: ${cacheMap.size}")
                    }
                }

                val cache = cacheMap[(artist to name).toString()]

                val result = if (cache == null) {
                    val arrayResult = NEMDownloader.query(artist, name)
                    if (arrayResult.isNullOrEmpty()) {
                        val raw = "[00:00.000] 歌词获取错误，请尝试更换网络\n[00:10.000] "
                        val application = AndroidAppHelper.currentApplication()
                        val intent = Intent("com.retrox.aodmod.NEW_MEDIA_LRC")
                        intent.putExtra("mediaLyric", raw)
                        application.applicationContext.sendBroadcast(intent)
                        return@async
                    }
                    val bestMatch = arrayResult.find {
                        it.title == name
                    }
                    val first = bestMatch ?: arrayResult.firstOrNull()
                    first?.let {
                        cacheMap[(artist to name).toString()] = it
                    }
                    MainHook.logD("Lrc Search From NetWork $artist $name")

                    // 不需要拿到返回值 多了10个数据再回写
                    if (cacheMap.size - initialCacheSize > 5) {
                        initialCacheSize = cacheMap.size // 更新数据size位
                        async(Dispatchers.IO) {
                            // 异步回写
                            GlobalCacheManager.writeCache(cacheKey, Gson().toJson(cacheMap.toMap()))
                        }
                    }
                    first
                } else {
                    MainHook.logD("Lrc Search From Cache Hit $artist $name Size: ${cacheMap.size}")
                    cache
                }

                result?.let {
                    val needTrans = GlobalKV.get("lrc_trans")?.toBoolean() ?: false
                    val lrcFileName =
                        "${it.artist}-${it.title}-${it.id}${if (needTrans) "-trans" else ""}.lrc"
                    val lrcCacheString = GlobalCacheManager.readCache(lrcFileName).also {
                        MainHook.logD("LRC Cache hit: $lrcFileName , 一部分歌词: ${it?.take(30)}")
                    } ?: kotlin.run {
                        var raw = NEMDownloader.download(it, needTrans)
                        if (raw.isNullOrBlank()) {
                            raw = "[00:00.000] 歌词获取错误"
                        } else {
                            // 成功再存
                            async(Dispatchers.IO) {
                                GlobalCacheManager.writeCache(lrcFileName, raw)
                            }
                        }
                        raw
                    }
                    val application = AndroidAppHelper.currentApplication()
                    val intent = Intent("com.retrox.aodmod.NEW_MEDIA_LRC")
                    intent.putExtra("mediaLyric", lrcCacheString)
                    application.applicationContext.sendBroadcast(intent)
                    lrcCacheString
                }
            }
            result.await()
        }
    }

    val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
        MainHook.logE("歌词获取错误", t = throwable)
        throwable.printStackTrace()
    }
}