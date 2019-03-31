package com.retrox.aodmod.proxy.view.theme

import android.support.annotation.Keep
import com.retrox.aodmod.shared.FileUtils
import com.retrox.aodmod.shared.SharedFile
import com.retrox.aodmod.shared.SharedFileReader
import com.retrox.aodmod.shared.gson

@Keep
class ThemeClockPack(
    @Keep val themeName: String,
    @Keep val gradientStart: String,
    @Keep val gradientEnd: String,
    @Keep val tintColor: String
) : SharedFile {
    override fun writeToFile() {
        val jsonString = gson.toJson(this)
        FileUtils.writeFileWithContent(fileName, jsonString)
    }

    companion object obj : SharedFileReader<ThemeClockPack> {
        override fun readFromFile(): ThemeClockPack {
            return gson.fromJson(FileUtils.readFile(fileName), ThemeClockPack::class.java)
        }

        override val fileName: String = "ThemeClockPack"
    }
}