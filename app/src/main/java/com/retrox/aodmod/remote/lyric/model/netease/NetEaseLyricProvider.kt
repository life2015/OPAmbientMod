package com.retrox.aodmod.remote.lyric.model.netease

import android.os.Build
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.remote.lyric.Utils
import com.retrox.aodmod.remote.lyric.model.LyricProvider
import com.retrox.aodmod.remote.lyric.model.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import org.json.JSONObject


class NetEaseLyricProvider : LyricProvider {
    private val client = OkHttpClient()

    override suspend fun fetchLyric(song: SongEntity, forceReload: Boolean): String? {
        val lyric = try {
            val id = queryMusicId(song) ?: return null
            queryMusicLyric(id)
        } catch (e: Exception) {
            e.printStackTrace()
            MainHook.logE(msg = "歌词下载错误", t = e)
            null
        }
        return lyric
    }

    suspend fun queryMusicId(song: SongEntity) = withContext(Dispatchers.IO) {

        val url =
            HttpUrl.parse("https://music.163.com/api/search/get/web?csrf_token=&type=1&offset=0&total=true&limit=20")!!
                .newBuilder().addQueryParameter("s", "${song.name}-${song.artist}").build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader(
                "User-Agent",
                "Android/Device ${Build.DEVICE} SDK Version/${Build.VERSION.SDK_INT}"
            )
            .addHeader("Accept", "*/*")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Host", "music.163.com")
            .addHeader("Connection", "keep-alive")
            .addHeader("cache-control", "no-cache")
            .build()

        // 反正返回空就是出问题了
        val response = client.newCall(request).execute().body()?.string() ?: return@withContext null
        try {
            MainHook.logD(response)
            val jsonObject = JSONObject(response)
            val id = jsonObject.getJSONObject("result").getJSONArray("songs").getJSONObject(0)
                .getString("id")
            return@withContext id
        } catch (e: Exception) {
            e.printStackTrace()
            MainHook.logE(msg = "网易云歌词搜索结果为空")
            return@withContext null
        }

    }

    suspend fun queryMusicLyric(id: String) = withContext(Dispatchers.IO) {

        val url = HttpUrl.parse("https://music.163.com/api/song/lyric?os=pc&lv=-1&kv=-1&tv=-1")!!
            .newBuilder().addQueryParameter("id", id).build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader(
                "User-Agent",
                "Android/Device ${Build.DEVICE} SDK Version/${Build.VERSION.SDK_INT}"
            )
            .addHeader("Accept", "*/*")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Host", "music.163.com")
            .addHeader("Connection", "keep-alive")
            .addHeader("cache-control", "no-cache")
            .build()

        val response = client.newCall(request).execute().body()?.string() ?: return@withContext null
        val jsonObject = JSONObject(response)
        val lyric = jsonObject.getJSONObject("lrc").getString("lyric")
        val tlyric = jsonObject.getJSONObject("tlyric").getString("lyric")

        val result = if (null != tlyric && tlyric != "null" && tlyric != "") {
            val merged = Utils.mergeLyrics(lyric, tlyric)
            if (merged.isEmpty()) lyric else merged // todo 暂时避免merge出现问题的情况 有时间排查下
        } else lyric

        MainHook.logD(lyric)
        MainHook.logD(tlyric)
        MainHook.logD(result.isEmpty().toString())
        return@withContext result
    }
}