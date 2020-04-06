package com.retrox.aodmod.shared

import android.content.Context
import com.retrox.aodmod.R
import com.retrox.aodmod.shared.data.SharedState
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

    fun resetStateFile(context: Context) {
        val state = SharedState(context.getString(R.string.just_restarted), 0.toString(), context.getString(
                    R.string.no_record))
        state.writeToFile()
    }

    fun getSharedState(context: Context): SharedState {
        return try {
            SharedState.readFromFile()
        } catch (e: Exception) {
            e.printStackTrace()
            return SharedState(context.getString(R.string.operating_mode_not_detected), 0.toString(), context.getString(
                R.string.no_record))
        }
    }
}