package com.practicum.playlistmaker.search.domain.models

import android.os.Parcel
import android.os.Parcelable


data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseYear: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?,
    var inFavorite: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(trackId)
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(trackTime)
        parcel.writeString(artworkUrl100)
        parcel.writeString(collectionName)
        parcel.writeString(releaseYear)
        parcel.writeString(primaryGenreName)
        parcel.writeString(country)
        parcel.writeString(previewUrl)
        parcel.writeByte(if (inFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}
