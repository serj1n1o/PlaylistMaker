package com.practicum.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.db.dao.TrackDao
import com.practicum.playlistmaker.db.entity.TrackDbo

@Database(version = 1, entities = [TrackDbo::class])
abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}