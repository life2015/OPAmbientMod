package com.retrox.aodmod.remote.lyric.model

import android.support.annotation.Keep
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.remote.lyric.model.cache.CacheLyricProvider
import com.retrox.aodmod.remote.lyric.model.netease.NetEaseLyricProvider
import com.retrox.aodmod.remote.lyric.model.qqmusic.QQMusicLyricProvider


interface LyricProvider {
    suspend fun fetchLyric(song: SongEntity, forceReload: Boolean = false): String? // 返回歌词字符串
}

@Keep
data class SongEntity(@Keep val name: String, @Keep val artist: String) {
    override fun toString(): String {
        return "${name}-${artist}"
    }
}

object CommonLyricProvider: LyricProvider {
    private val cacheLyricProvider = CacheLyricProvider()
    private val netEaseLyricProvider: LyricProvider = NetEaseLyricProvider()
    private val qqMusicLyricProvider: LyricProvider = QQMusicLyricProvider()

    override suspend fun fetchLyric(song: SongEntity, forceReload: Boolean): String? {
        val lyricCache = cacheLyricProvider.fetchLyric(song, forceReload) // 这个cache接口 传入forceReload的时候就返回了空
        if (lyricCache != null) {
            MainHook.logD("从缓存中获取歌词: $lyricCache")
            return lyricCache
        } else {
            val netEaseLyric = null
            if (netEaseLyric != null) {
                MainHook.logD("从网易云中获取歌词: $netEaseLyric")
//                cacheLyricProvider.writeLyricToCache(song, netEaseLyric)
                return netEaseLyric
            }
            val qqMusicLyric = qqMusicLyricProvider.fetchLyric(song, forceReload)
            if (qqMusicLyric != null) {
                MainHook.logD("从QQ音乐中获取歌词: $qqMusicLyric")
                cacheLyricProvider.writeLyricToCache(song, qqMusicLyric)
                return qqMusicLyric
            }
        }
        return null
    }

}