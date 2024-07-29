package com.practicum.playlistmaker.db.converters

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun coverFromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun coverToUri(uriString: String): Uri {
        return uriString.toUri()
    }

    @TypeConverter
    fun trackIdFromList(listTrackId: List<Long>): String {
        return gson.toJson(listTrackId)
    }

    @TypeConverter
    fun trackIdToList(listTrackId: String): List<Long> {
        val listType = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(listTrackId, listType)
    }

}