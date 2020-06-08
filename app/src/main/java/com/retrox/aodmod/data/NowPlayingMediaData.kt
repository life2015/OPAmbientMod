package com.retrox.aodmod.data

import android.os.Parcel
import android.os.Parcelable

data class NowPlayingMediaData (val name: String, val artist: String, val album: String, val app: String = ""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeString(app)
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
    }

    override fun toString(): String {
        return "NPMD: $name - $artist - $album from $app"
    }

}