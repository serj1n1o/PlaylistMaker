package com.practicum.playlistmaker.medialibrary.domain.dbapi

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

interface PlaylistInteractor {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlistId: Long, trackId: Long)

    suspend fun deletePlaylist(playlist: Playlist)
}