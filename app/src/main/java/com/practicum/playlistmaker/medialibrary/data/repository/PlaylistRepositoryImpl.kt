package com.practicum.playlistmaker.medialibrary.data.repository

import com.practicum.playlistmaker.db.PlaylistDatabase
import com.practicum.playlistmaker.medialibrary.converters.PlaylistDbConverter
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val playlistConverter: PlaylistDbConverter,
) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            playlistDatabase.playlistDao().insertPlaylist(
                playlistConverter.map(playlist)
            )
        }
    }

    override suspend fun updatePlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO) {
            val oldPlaylist = playlistDatabase.playlistDao().getPlaylistById(playlistId)
            if (oldPlaylist != null) {
                val listTracksId = oldPlaylist.listTrackIdDbo.toMutableList()
                listTracksId.add(trackId)
                val newPlaylist = oldPlaylist.copy(
                    listTrackIdDbo = listTracksId,
                    amountTracks = listTracksId.size
                )
                playlistDatabase.playlistDao().updatePlaylist(newPlaylist)
            }
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            playlistDatabase.playlistDao().deletePlaylist(playlistConverter.map(playlist))
        }
    }
}