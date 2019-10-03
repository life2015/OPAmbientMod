package com.retrox.aodmod.shared.global

import android.annotation.SuppressLint
import android.os.FileObserver
import android.util.Log
import com.retrox.aodmod.extensions.chmod777
import com.retrox.aodmod.shared.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object OwnFileManager {
    val ownDir = FileUtils.sharedDir + "/files/own"
    var watcher: FileObserver? = null

    fun getOwnFileDir(): File {
        val file = File(ownDir)
        if (!file.exists()) {
            file.mkdir()
        }
        file.chmod777()
        return file
    }

    fun ensurePermissions() {
        val dir = getOwnFileDir()
        dir.walkTopDown().forEach {
            it.chmod777()
        }
    }


    fun writeFileWithContent(name: String, content: String): Boolean {
        try {
            val file = File(getOwnFileDir(), name)
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

    @Throws(IOException::class)
    fun readFile(name: String): String {
        val file = File(getOwnFileDir(), name)
        val fin = FileInputStream(file)
        val text = fin.bufferedReader().readText()
        fin.close()
        return text
    }

    // todo 暴露监控入口 抽象出RPC
    fun watchFile() {
        watcher = object : FileObserver(ownDir, FileObserver.MODIFY) {
            override fun onEvent(event: Int, path: String?) {
                Log.d("AODMOD", "event: $event, path: $path")
            }

        }
        watcher?.startWatching()
    }
}