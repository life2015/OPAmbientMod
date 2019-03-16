package com.retrox.aodmod.shared

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

object SharedLogger {
    val logDir = FileUtils.sharedDir + "/log"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val detailTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    private val logBuffer = mutableListOf<String>()

    private fun createLogDir() {
        val file = File(logDir)
        if (!file.exists()) file.mkdir()
    }

    fun writeLog(content: String) {
//        Log.d("SharedLogger", content)

        val realLog = "${detailTimeFormat.format(Date())} -> $content \n"
        if (logBuffer.size < 20) {
            logBuffer.add(realLog)
            return
        }
        Log.d("SharedLogger", "Log Flushed")
        logBuffer.add(realLog)
        createLogDir()
        val temp = logBuffer.toList()
        logBuffer.clear()
        thread {
            writeFileWithContent(getLogFileName(), temp, true)
        }
    }


    private fun getLogFileName(): String {
        val dateString = dateFormat.format(Date())
        val fileName = "${dateString}.log"
        return fileName
    }

    private fun writeFileWithContent(name: String, content: List<String>, append: Boolean = true): Boolean {
        try {
            FileUtils.createSharedFileDir()
            val file = File(logDir, name)
            val fos = FileOutputStream(file, append)
            content.forEach {
                fos.write(it.toByteArray())
            }
            fos.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}