package com.retrox.aodmod.shared

import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

object SharedContentManager {

    private var workMode: String = ""
    private var aodTimes = 0
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    fun setWorkMode(mode: String) {
        workMode = mode
    }

    fun addAodTimes() {
        aodTimes++
        thread {
            SharedState(workMode, aodTimes.toString(), dateFormat.format(Date())).writeToFile()
        }
    }

    fun resetStateFile() {
        val state = SharedState("刚刚重启", 0.toString(), "无记录")
        state.writeToFile()
    }

    fun getSharedState(): SharedState {
        return try {
            SharedState.readFromFile()
        } catch (e: Exception) {
            e.printStackTrace()
            return SharedState("尚未检测到工作模式", 0.toString(), "无记录")
        }
    }
}