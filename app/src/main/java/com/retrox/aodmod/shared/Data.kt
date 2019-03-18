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

