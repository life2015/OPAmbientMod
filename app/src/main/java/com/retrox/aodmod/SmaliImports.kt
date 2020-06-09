package com.retrox.aodmod

import com.retrox.aodmod.pref.XPref.getDateFormat
import com.retrox.aodmod.pref.XPref.getIs24h
import com.retrox.aodmod.pref.XPref.getIsAmPm
import com.retrox.aodmod.pref.XPref.getShowBullets
import java.text.SimpleDateFormat
import java.util.*

object SmaliImports {

    val systemDateFormat: String
        get() = getDateFormat()!!

    val timeFormat: String
        get() = if (getIs24h()) {
            "HH:mm"
        } else {
            if (getIsAmPm()) {
                "h:mm a"
            } else {
                "h:mm"
            }
        }

    fun getFormattedTime(timeInMillis: Long): String {
        val time = Date(timeInMillis)
        return if (getIs24h()) {
            val format =
                SimpleDateFormat("HH:mm", Locale.getDefault())
            format.format(time)
        } else {
            if (getIsAmPm()) {
                val format =
                    SimpleDateFormat("h:mm a", Locale.getDefault())
                format.format(time)
            } else {
                val format =
                    SimpleDateFormat("h:mm", Locale.getDefault())
                format.format(time)
            }
        }
    }

    val bulletSymbol: String
        get() = if (getShowBullets()) {
            " • "
        } else {
            ""
        }


}