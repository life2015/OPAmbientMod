package com.retrox.aodmod.shared.global

import com.retrox.aodmod.MainHook
import com.retrox.aodmod.extensions.chmod777
import com.retrox.aodmod.shared.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object GlobalCacheManager {
    val globalCacheDir = FileUtils.sharedDir + "/files/cache"

    fun getCacheFileDir(): File {
        val file = File(globalCacheDir)
        if (!file.exists()) {
            file.mkdir()
        }
        file.chmod777()
        return file
    }

    fun writeCache(fileName: String, content: String): Boolean {
        try {
            val file = File(getCacheFileDir(), fileName)
            val fos = FileOutputStream(file)
            fos.write(content.toByteArray())
            fos.close()
            file.chmod777()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    // 未命中Cache的话 就返回空
    @Throws(IOException::class)
    fun readCache(fileName: String): String? {
        try {
            val file = File(globalCacheDir, fileName)
            val fin = FileInputStream(file)
            val text = fin.bufferedReader().readText()
            fin.close()
            return text
        } catch (e: Exception) {
            MainHook.logE(msg = "读取缓存错误", t = e)
            e.printStackTrace()
            return null
        }
    }

}