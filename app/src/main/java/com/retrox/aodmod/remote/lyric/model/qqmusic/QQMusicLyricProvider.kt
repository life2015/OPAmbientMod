package com.retrox.aodmod.remote.lyric.model.qqmusic

import com.retrox.aodmod.MainHook
import com.retrox.aodmod.remote.lyric.model.LyricProvider
import com.retrox.aodmod.remote.lyric.model.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*


class QQMusicLyricProvider : LyricProvider {
    private val client = OkHttpClient()

    override suspend fun fetchLyric(song: SongEntity, forceReload: Boolean): String? {
        try {
            val mid = queryMusicMid(song) ?: return null
            return queryMusicLyric(mid)
        } catch (e: Exception) {
            e.printStackTrace()
            MainHook.logE(msg = "歌词下载错误", t = e)
            return null
        }
    }

    suspend fun queryMusicMid(song: SongEntity) = withContext(Dispatchers.IO) {
        val url =
            HttpUrl.parse("https://c.y.qq.com/soso/fcgi-bin/client_search_cp")!!
                .newBuilder().addQueryParameter("w", "${song.name}-${song.artist}").build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("User-Agent", "PostmanRuntime/7.17.1")
            .addHeader("Accept", "*/*")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Host", "c.y.qq.com")
            .addHeader("Connection", "keep-alive")
            .addHeader("cache-control", "no-cache")
            .build()

        MainHook.logD(url.toString())
        val response = client.newCall(request).execute().body()?.string() ?: return@withContext null
        val realResponse = response.drop(9).dropLast(1)

        MainHook.logD(response)
        val jsonObject = JSONObject(realResponse)
        val mid = jsonObject.getJSONObject("data").getJSONObject("song").getJSONArray("list")
            .getJSONObject(0).getString("songmid")
        return@withContext mid
    }

    suspend fun queryMusicLyric(mid: String) = withContext(Dispatchers.IO) {

        val url = HttpUrl.parse("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg")!!
            .newBuilder().addQueryParameter("songmid", mid).build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Referer", "y.qq.com/portal/player.html")
            .addHeader("User-Agent", "PostmanRuntime/7.17.1")
            .addHeader("Accept", "*/*")
            .addHeader("Cache-Control", "no-cache")
            .addHeader("Host", "c.y.qq.com")
            .addHeader("Connection", "keep-alive")
            .addHeader("cache-control", "no-cache")
            .build()

        val response = client.newCall(request).execute().body()?.string() ?: return@withContext null
        val realResponse = response.drop(18).dropLast(1)
        val jsonObject = JSONObject(realResponse)
        val base64Lyric = jsonObject.getString("lyric")
        val lyric = String(Base64.getDecoder().decode(base64Lyric))
        return@withContext lyric
    }
}