package com.retrox.aodmod.shared.data

import com.retrox.aodmod.shared.FileUtils
import com.retrox.aodmod.shared.SharedFile
import com.retrox.aodmod.shared.SharedFileReader
import com.retrox.aodmod.shared.gson

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