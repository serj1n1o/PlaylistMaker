package com.practicum.playlistmaker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_tracks_table")
data class TrackDbo(
    @PrimaryKey
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
    var inFavorite: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)