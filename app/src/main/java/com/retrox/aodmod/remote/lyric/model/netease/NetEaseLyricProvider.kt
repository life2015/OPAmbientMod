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
        val response = try {
            client.newCall(request).execute().body()?.string() ?: return@withContext null
        } catch (e: IOException) {
            e.printStackTrace()
            MainHook.logE(msg = "网易云歌词获取错误", t = e)
            return@withContext null
        }

        val jsonObject = try {
            JSONObject(response)
        } catch (e: Exception) {
            MainHook.logE(msg = "网易云歌词获取json解析错误", t = e)
            return@withContext null
        }

        val songs = jsonObject.getJSONObject("result").getJSONArray("songs")
        if (songs.length() > 1) {
            val id = songs.getJSONObject(0).getString("id")
            return@withContext id
        } else {
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
            Utils.mergeLyrics(lyric, tlyric)
        } else lyric

        return@withContext result
    }
}