package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlistId: Long, trackId: Long) {
        playlistRepository.updatePlaylist(playlistId, trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }
}