package com.practicum.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.db.entity.TrackInPlaylistDbo

@Dao
interface TrackInPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToTrackInPlaylist(trackInPlaylistDbo: TrackInPlaylistDbo)

    @Query("SELECT * FROM track_in_playlist_table WHERE trackId  IN (:listId)")
    fun getTracksInPlaylists(listId: List<Long>): List<TrackInPlaylistDbo>

    @Query("DELETE FROM track_in_playlist_table WHERE trackId = :id")
    suspend fun removeTrack(id: Long)
}