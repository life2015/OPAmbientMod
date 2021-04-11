package com.retrox.aodmod.data

import android.app.PendingIntent
import android.os.Parcel
import android.os.Parcelable
import com.retrox.aodmod.extensions.concatMusic

data class NowPlayingMediaData(val name: String, val artist: String, val album: String, val app: String = "", val overriddenFullString: String = "", val ttlMillis: Long = 0L): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeString(app)
        parcel.writeString(overriddenFullString)
        parcel.writeLong(ttlMillis)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NowPlayingMediaData> {
        override fun createFromParcel(parcel: Parcel): NowPlayingMediaData {
            return NowPlayingMediaData(parcel)
        }

        override fun newArray(size: Int): Array<NowPlayingMediaData?> {
            return arrayOfNulls(size)
        }

        private fun Parcel.readPendingIntentOrNull(): PendingIntent? {
            return if(readInt() == 1) return readParcelable(PendingIntent::class.java.classLoader)
            else null
        }

        private fun Parcel.writePendingIntentOrNull(pendingIntent: PendingIntent?) {
            if(pendingIntent != null){
                writeInt(1)
                writeParcelable(pendingIntent, 0)
            }else{
                writeInt(0)
            }
        }
    }

    override fun toString(): String {
        return "NPMD: $name - $artist - $album from $app"
    }

    fun getMusicString(concat: Boolean = false): String {
        return if(overriddenFullString.isEmpty()){
            if(concat) {
                "${artist.concatMusic()} - ${name.concatMusic()}"
            }else{
                "$name - $artist"
            }
        }else{
            overriddenFullString
        }
    }


}