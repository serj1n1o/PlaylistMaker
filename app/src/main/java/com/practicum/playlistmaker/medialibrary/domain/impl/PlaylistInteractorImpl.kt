package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.StatusAdd
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist, trackId: Long): StatusAdd {
        val result = playlistRepository.updatePlaylist(playlist, trackId)
        return if (result > 0) {
            StatusAdd.Success(playlistName = playlist.name)
        } else {
            StatusAdd.Failure(playlistName = playlist.name)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylist()
    }

    override suspend fun addTrackToTrackInPlaylist(track: Track) {
        playlistRepository.addTrackToTrackInPlaylist(track)
    }
}