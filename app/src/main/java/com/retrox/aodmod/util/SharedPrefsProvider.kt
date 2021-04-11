package com.retrox.aodmod.util

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

//Based off https://code.highspec.ru/Mikanoshi/CustoMIUIzer
class SharedPrefsProvider : ContentProvider() {
    var prefs: SharedPreferences? = null

    companion object {
        const val AUTHORITY = "com.retrox.aodmod.provider.sharedprefs"
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(AUTHORITY, "string/*/*", 1)
            uriMatcher.addURI(AUTHORITY, "integer/*/*", 2)
            uriMatcher.addURI(AUTHORITY, "boolean/*/*", 3)
        }
    }

    override fun onCreate(): Boolean {
        return try {
            prefs = context?.getSharedPreferences(context?.packageName + "_preferences", Context.MODE_PRIVATE)
            true
        } catch (throwable: Throwable) {
            false
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val parts = uri.pathSegments
        val cursor = MatrixCursor(arrayOf("data"))
        when (uriMatcher.match(uri)) {
            1 -> {
                cursor.newRow().add("data", prefs!!.getString(parts[1], parts[2]))
                return cursor
            }
            2 -> {
                cursor.newRow().add("data", prefs!!.getInt(parts[1], parts[2].toInt()))
                return cursor
            }
            3 -> {
                cursor.newRow().add("data", if (prefs!!.getBoolean(parts[1], parts[2].toInt() == 1)) 1 else 0)
                return cursor
            }
        }
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }
}