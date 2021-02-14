package com.retrox.aodmod.util

import android.content.Context
import android.database.Cursor
import android.net.Uri

//Following methods based off https://code.highspec.ru/Mikanoshi/CustoMIUIzer
fun stringPrefToUri(name: String, defValue: String): Uri {
    return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/string/" + name + "/" + defValue)
}

fun intPrefToUri(name: String, defValue: Int): Uri {
    return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/integer/" + name + "/" + defValue.toString())
}

fun boolPrefToUri(name: String, defValue: Boolean): Uri {
    return Uri.parse("content://" + SharedPrefsProvider.AUTHORITY + "/boolean/" + name + "/" + if (defValue) '1' else '0')
}

fun getSharedStringPref(context: Context, name: String, defValue: String): String? {
    val uri: Uri = stringPrefToUri(name, defValue)
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    return if (cursor != null) {
        cursor.moveToFirst()
        val prefValue: String = cursor.getString(0)
        cursor.close()
        prefValue
    } else null
}

fun getSharedIntPref(context: Context, name: String, defValue: Int): Int {
    val uri: Uri = intPrefToUri(name, defValue)
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    return if (cursor != null) {
        cursor.moveToFirst()
        val prefValue: Int = cursor.getInt(0)
        cursor.close()
        prefValue
    } else defValue
}

fun getSharedBoolPref(context: Context, name: String, defValue: Boolean): Boolean {
    val uri: Uri = boolPrefToUri(name, defValue)
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    return if (cursor != null) {
        cursor.moveToFirst()
        val prefValue: Int = cursor.getInt(0)
        cursor.close()
        prefValue == 1
    } else defValue
}