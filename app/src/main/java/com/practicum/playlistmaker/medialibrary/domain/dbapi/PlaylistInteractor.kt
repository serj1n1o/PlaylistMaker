package com.practicum.playlistmaker.medialibrary.domain.dbapi

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.StatusAdd
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.PlaylistUpdateAction
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(
        playlist: Playlist,
        trackId: Long,
        action: PlaylistUpdateAction,
    ): StatusAdd

    suspend fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylist(): Flow<List<Playlist>>

    suspend fun getPlaylistById(id: Long): Playlist

    suspend fun addTrackToTrackInPlaylist(track: Track)

    suspend fun getTracksInPlaylist(listTrackId: List<Long>): Flow<List<Track>>
}