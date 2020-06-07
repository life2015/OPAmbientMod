package com.retrox.aodmod.remote.lyric.model

import androidx.annotation.Keep
import com.retrox.aodmod.MainHook
import com.retrox.aodmod.R
import com.retrox.aodmod.extensions.ResourceUtils
import com.retrox.aodmod.extensions.unescapeXml
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
            MainHook.logD("Lyrics from cache: $lyricCache")
            return lyricCache
        } else {

            val netEaseLyric = netEaseLyricProvider.fetchLyric(song, forceReload)?.unescapeXml()?.localise()
            if (netEaseLyric != null) {
                MainHook.logD("Lyrics from NetEase: $netEaseLyric")
                cacheLyricProvider.writeLyricToCache(song, netEaseLyric)
                return netEaseLyric
            }else{
                MainHook.logD("null returned from netease lyrics")
            }
            val qqMusicLyric = qqMusicLyricProvider.fetchLyric(song, forceReload)?.unescapeXml()?.localise()
            if (qqMusicLyric != null) {
                MainHook.logD("Lyrics from QQ: $qqMusicLyric")
                cacheLyricProvider.writeLyricToCache(song, qqMusicLyric)
                return qqMusicLyric
            }else{
                MainHook.logD("null returned from QQ lyrics")
            }
        }
        return null
    }

}

private fun String.localise(): String {
    return this.replace("此歌曲为没有填词的纯音乐，请您欣赏", ResourceUtils.getInstance().getString(R.string.lyrics_music_only))
}