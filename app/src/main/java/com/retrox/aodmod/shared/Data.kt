package com.retrox.aodmod.shared

import android.support.annotation.Keep
import com.google.gson.Gson

/**
 * 接口规范化存取操作
 */
interface SharedFileReader<T> {
    fun readFromFile(): T
    val fileName: String
}

interface SharedFile {
    fun writeToFile()
}

val gson = Gson()

@Keep
data class SharedState(val workMode: String, val aodTimes: String, val lastTime: String) : SharedFile {
    override fun writeToFile() {
        val jsonString = gson.toJson(this)
        FileUtils.writeFileWithContent(fileName, jsonString)
    }

    companion object obj : SharedFileReader<SharedState> {
        override fun readFromFile(): SharedState {
            return gson.fromJson(FileUtils.readFile(fileName), SharedState::class.java)
        }

        override val fileName: String = "SharedState"
    }
}