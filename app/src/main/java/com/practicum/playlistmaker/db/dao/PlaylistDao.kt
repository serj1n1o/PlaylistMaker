package com.practicum.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.db.entity.PlaylistDbo

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistDbo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlist: PlaylistDbo): Int

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistDbo)

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistDbo?

    @Query("SELECT * FROM playlist_table ORDER BY amountTracks DESC")
    suspend fun getAllPlaylists(): List<PlaylistDbo>
}