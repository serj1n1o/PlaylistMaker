package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.StatusAdd
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.PlaylistUpdateAction
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun updatePlaylist(
        playlist: Playlist,
        trackId: Long,
        action: PlaylistUpdateAction,
    ): StatusAdd {
        val result = playlistRepository.updatePlaylist(playlist, trackId, action)
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

    override suspend fun getPlaylistById(id: Long): Playlist {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun addTrackToTrackInPlaylist(track: Track) {
        playlistRepository.addTrackToTrackInPlaylist(track)
    }

    override suspend fun getTracksInPlaylist(listTrackId: List<Long>): Flow<List<Track>> {
        return playlistRepository.getTracksInPlaylist(listTrackId)
    }


}