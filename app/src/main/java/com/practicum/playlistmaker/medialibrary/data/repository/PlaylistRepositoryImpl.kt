package com.practicum.playlistmaker.medialibrary.data.repository

import com.practicum.playlistmaker.db.Database
import com.practicum.playlistmaker.db.entity.PlaylistDbo
import com.practicum.playlistmaker.db.entity.TrackInPlaylistDbo
import com.practicum.playlistmaker.medialibrary.converters.PlaylistDbConverter
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.PlaylistUpdateAction
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

    override suspend fun updatePlaylist(
        playlist: Playlist,
        trackId: Long,
        action: PlaylistUpdateAction,
    ): Int {
        val result = withContext(Dispatchers.IO) {
            val listTracksId = playlist.listTrackId.toMutableList()

            when (action) {
                PlaylistUpdateAction.ADD -> listTracksId.add(trackId)
                PlaylistUpdateAction.REMOVE -> listTracksId.remove(trackId)
            }
            val newPlaylist = playlist.copy(
                listTrackId = listTracksId,
                amountTracks = listTracksId.size
            )
            database.playlistDao().updatePlaylist(playlistConverter.map(newPlaylist))
        }
        if (action == PlaylistUpdateAction.REMOVE) checkTrackInPlaylistsAndRemove(trackId)
        return result
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            database.playlistDao().deletePlaylist(playlistConverter.map(playlist))
            playlist.listTrackId.forEach { trackId ->
                checkTrackInPlaylistsAndRemove(trackId)
            }
        }
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> = flow {
        val playlists = withContext(Dispatchers.IO) {
            database.playlistDao().getAllPlaylists()
        }
        emit(convertFromPlaylistDbo(playlists))
    }

    override suspend fun getPlaylistById(id: Long): Playlist {
        return withContext(Dispatchers.IO) {
            val playlist = database.playlistDao().getPlaylistById(id)
            playlistConverter.map(playlist)
        }
    }

    override suspend fun addTrackToTrackInPlaylist(track: Track) {
        withContext(Dispatchers.IO) {
            database.trackInPlaylistDao()
                .addTrackToTrackInPlaylist(playlistConverter.mapTrackToTrackInPlaylist(track))
        }
    }

    override suspend fun getTracksInPlaylist(listTrackId: List<Long>): Flow<List<Track>> = flow {
        val tracks = withContext(Dispatchers.IO) {
            database.trackInPlaylistDao().getTracksInPlaylists(listTrackId)
        }
        emit(convertFromTrackInPlaylist(tracks))
    }

    private suspend fun checkTrackInPlaylistsAndRemove(trackId: Long) {
        withContext(Dispatchers.IO) {
            val playlists = database.playlistDao().getAllPlaylists()
            val isTrackInPlaylists = playlists.any { playlist ->
                playlist.listTrackIdDbo.contains(trackId)
            }
            if (!isTrackInPlaylists) {
                database.trackInPlaylistDao().removeTrack(trackId)
            }
        }
    }


    private fun convertFromPlaylistDbo(playlists: List<PlaylistDbo>): List<Playlist> {
        return playlists.map { playlistDbo ->
            playlistConverter.map(playlistDbo)
        }
    }

    private fun convertFromTrackInPlaylist(tracks: List<TrackInPlaylistDbo>): List<Track> {
        return tracks.map { trackInPlaylist ->
            playlistConverter.mapTrackToTrackInPlaylist(trackInPlaylist)
        }
    }
}
