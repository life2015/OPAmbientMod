package com.retrox.aodmod.remote.lyric.model.cache

import com.retrox.aodmod.remote.lyric.model.LyricProvider
import com.retrox.aodmod.remote.lyric.model.SongEntity
import com.retrox.aodmod.shared.global.GlobalCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CacheLyricProvider : LyricProvider {
    override suspend fun fetchLyric(song: SongEntity, forceReload: Boolean): String? {
        if (forceReload) return null
        val cacheLyric = withContext(Dispatchers.IO) {
            GlobalCacheManager.readCache(song.toString())
        }
        return cacheLyric
    }

    fun writeLyricToCache(song: SongEntity, lyric: String) {
        if (lyric.trimIndent().isNotEmpty()) { // 避免写入脏数据
            GlobalCacheManager.writeCache(song.toString(), lyric)
        }
    }
}