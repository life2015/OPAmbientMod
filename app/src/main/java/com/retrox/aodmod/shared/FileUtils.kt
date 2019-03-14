package com.retrox.aodmod.shared

import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
    val sharedDir = Environment.getExternalStorageDirectory().path + "/Android/aod"

    fun createSharedFileDir() {
        val file = File(sharedDir)
        if (!file.exists()) file.mkdir()


    }

    fun writeFileWithContent(name: String, content: String): Boolean {
        try {
            createSharedFileDir()
            val file = File(sharedDir, name)
            val fos = FileOutputStream(file)
            fos.write(content.toByteArray())
            fos.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    @Throws(IOException::class)
    fun readFile(name: String): String {
        val file = File(sharedDir, name)
        val fin = FileInputStream(file)
        val text = fin.bufferedReader().readText()
        fin.close()
        return text
    }

}