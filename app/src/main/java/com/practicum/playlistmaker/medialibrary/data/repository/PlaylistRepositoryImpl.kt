package com.practicum.playlistmaker.medialibrary.data.repository

import com.practicum.playlistmaker.db.Database
import com.practicum.playlistmaker.db.entity.PlaylistDbo
import com.practicum.playlistmaker.medialibrary.converters.PlaylistDbConverter
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val database: Database,
    private val playlistConverter: PlaylistDbConverter,
) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            database.playlistDao().insertPlaylist(
                playlistConverter.map(playlist)
            )
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist, trackId: Long): Int {
        return withContext(Dispatchers.IO) {
            val listTracksId = playlist.listTrackId.toMutableList()
            listTracksId.add(trackId)
            val newPlaylist = playlist.copy(
                listTrackId = listTracksId,
                amountTracks = listTracksId.size
            )
            database.playlistDao().updatePlaylist(playlistConverter.map(newPlaylist))
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            database.playlistDao().deletePlaylist(playlistConverter.map(playlist))
        }
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> = flow {
        val playlists = withContext(Dispatchers.IO) {
            database.playlistDao().getAllPlaylists()
        }
        emit(convertFromPlaylistDbo(playlists))
    }

    override suspend fun addTrackToTrackInPlaylist(track: Track) {
        withContext(Dispatchers.IO) {
            database.trackInPlaylistDao()
                .addTrackToTrackInPlaylist(playlistConverter.mapTrackToTrackInPlaylist(track))
        }
    }


    private fun convertFromPlaylistDbo(playlists: List<PlaylistDbo>): List<Playlist> {
        return playlists.map { playlistDbo ->
            playlistConverter.map(playlistDbo)
        }
    }
}
