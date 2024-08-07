package com.practicum.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.db.entity.TrackInPlaylistDbo

@Dao
interface TrackInPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToTrackInPlaylist(trackInPlaylistDbo: TrackInPlaylistDbo)
}