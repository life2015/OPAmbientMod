package com.retrox.aodmod.shared.global

import android.os.FileObserver
import android.util.Log
import com.retrox.aodmod.extensions.chmod777
import com.retrox.aodmod.shared.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object GlobalKV {
    val kvDir = FileUtils.sharedDir + "/files/kv"
    var watcher: FileObserver? = null

    fun getKVFileDir(): File {
        val file = File(kvDir)
        if (!file.exists()) {
            file.mkdir()
        }
        file.chmod777()
        return file
    }

    fun ensurePermissions() {
        val dir = getKVFileDir()
        dir.walkTopDown().forEach {
            it.chmod777()
        }
    }

    fun put(name: String, content: String): Boolean {
        return writeFileWithContent(name, content)
    }

    fun get(name: String) : String? {
        return readFile(name)
    }


    private fun writeFileWithContent(name: String, content: String): Boolean {
        try {
            val file = File(getKVFileDir(), name)
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

    private fun readFile(name: String): String? {
        try {
            val file = File(getKVFileDir(), name)
            val fin = FileInputStream(file)
            val text = fin.bufferedReader().readText()
            fin.close()
            return text
        } catch (e: Exception) {
            return null
        }
    }

    fun watchChange(callback: (String?) -> Unit) {
        watcher = object : FileObserver(kvDir, MODIFY) {
            override fun onEvent(event: Int, path: String?) {
                Log.d("AODMOD", "event: $event, path: $path")
                callback(path)
            }

        }
        watcher?.startWatching()
    }
}