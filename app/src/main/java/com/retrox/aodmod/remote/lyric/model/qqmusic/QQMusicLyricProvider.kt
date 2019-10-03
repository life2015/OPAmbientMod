package com.retrox.aodmod.remote.lyric.model.qqmusic

import android.os.Build
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
            val mid = queryMusicMid(song) ?: queryMusicMidBySmartBox(song) ?: return null
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
            .addHeader(
                "User-Agent",
                "Android/Device ${Build.DEVICE} SDK Version/${Build.VERSION.SDK_INT}"
            )
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
        val mid = try {
            val jsonObject = JSONObject(realResponse)
            jsonObject.getJSONObject("data").getJSONObject("song").getJSONArray("list")
                .getJSONObject(0).getString("songmid")
        } catch (e: Exception) {
            e.printStackTrace()
            MainHook.logE("QQ音乐查询接口无法查到", t = e)
            null
        }
        return@withContext mid
    }

    suspend fun queryMusicMidBySmartBox(song: SongEntity) = withContext(Dispatchers.IO) {

        val url =
            HttpUrl.parse("https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?is_xml=0&format=json")!!
                .newBuilder().addQueryParameter("key", "${song.name}-${song.artist}").build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader(
                "User-Agent",
                "Android/Device ${Build.DEVICE} SDK Version/${Build.VERSION.SDK_INT}"
            )            .addHeader("Accept", "*/*")
            .addHeader("Host", "c.y.qq.com")
            .addHeader("Connection", "keep-alive")
            .addHeader("cache-control", "no-cache")
            .build()

        val response = client.newCall(request).execute().body()?.string() ?: return@withContext null
        val mid = try {
            val jsonObject = JSONObject(response)
            jsonObject.getJSONObject("data").getJSONObject("song").getJSONArray("itemlist")
                .getJSONObject(0).getString("mid")
        } catch (e: Exception) {
            e.printStackTrace()
            MainHook.logE("QQ音乐自动补全接口无法查到", t = e)
            null
        }
        MainHook.logD("QQ音乐自动补全接口查询到mid: $mid")
        mid
    }

    suspend fun queryMusicLyric(mid: String) = withContext(Dispatchers.IO) {

        val url = HttpUrl.parse("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg")!!
            .newBuilder().addQueryParameter("songmid", mid).build()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Referer", "y.qq.com/portal/player.html")
            .addHeader(
                "User-Agent",
                "Android/Device ${Build.DEVICE} SDK Version/${Build.VERSION.SDK_INT}"
            )
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