package com.practicum.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.practicum.playlistmaker.db.converters.Converters
import com.practicum.playlistmaker.db.dao.PlaylistDao
import com.practicum.playlistmaker.db.dao.TrackFavoritesDao
import com.practicum.playlistmaker.db.dao.TrackInPlaylistDao
import com.practicum.playlistmaker.db.entity.PlaylistDbo
import com.practicum.playlistmaker.db.entity.TrackDbo
import com.practicum.playlistmaker.db.entity.TrackInPlaylistDbo

@Database(version = 1, entities = [TrackDbo::class, PlaylistDbo::class, TrackInPlaylistDbo::class])
@TypeConverters(value = [Converters::class])
abstract class Database : RoomDatabase() {
    abstract fun trackDao(): TrackFavoritesDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}