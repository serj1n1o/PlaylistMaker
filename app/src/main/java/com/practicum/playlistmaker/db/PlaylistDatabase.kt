package com.practicum.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.practicum.playlistmaker.db.converters.Converters
import com.practicum.playlistmaker.db.dao.PlaylistDao
import com.practicum.playlistmaker.db.entity.PlaylistDbo

@Database(version = 1, entities = [PlaylistDbo::class])
@TypeConverters(value = [Converters::class])
abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}