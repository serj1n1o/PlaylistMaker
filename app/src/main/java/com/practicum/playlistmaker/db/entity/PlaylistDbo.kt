package com.practicum.playlistmaker.db.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.practicum.playlistmaker.db.converters.Converters

@Entity(tableName = "playlist_table")
@TypeConverters(value = [Converters::class])
data class PlaylistDbo(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long?,
    val name: String,
    val description: String,
    val cover: Uri?,
    val listTrackIdDbo: List<Long>,
    val amountTracks: Int,
)