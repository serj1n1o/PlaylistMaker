package com.practicum.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.db.entity.TrackDbo

@Dao
interface TrackFavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToFavorites(track: TrackDbo)

    @Delete
    suspend fun deleteTrackFromFavorites(track: TrackDbo)

    @Query("SELECT * FROM favorites_tracks_table ORDER BY timestamp DESC")
    suspend fun getFavoritesTracks(): List<TrackDbo>

    @Query("SELECT trackId FROM favorites_tracks_table")
    suspend fun getTracksIdFromFavorites(): List<Long>
}