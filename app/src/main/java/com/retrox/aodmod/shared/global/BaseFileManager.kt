package com.retrox.aodmod.shared.global

import com.retrox.aodmod.extensions.chmod777
import java.io.File

object BaseFileManager {
    val baseFileDir = "/data/data/com.retrox.aodmod" + "/files"

    fun makeCacheFileDir(): File {
        val file = File(baseFileDir)
        if (!file.exists()) {
            file.mkdir()
        }
        file.chmod777()
        return file
    }

}