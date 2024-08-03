package com.practicum.playlistmaker.medialibrary.data.repository

import com.practicum.playlistmaker.db.Database
import com.practicum.playlistmaker.db.entity.TrackDbo
import com.practicum.playlistmaker.medialibrary.converters.TrackDbConverter
import com.practicum.playlistmaker.medialibrary.domain.dbapi.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val database: Database,
    private val trackConverter: TrackDbConverter,
) : FavoritesRepository {
    override suspend fun addTrackToFavorites(track: Track) {
        withContext(Dispatchers.IO) {
            database.trackDao().insertTrackToFavorites(trackConverter.map(track))
        }
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        withContext(Dispatchers.IO) {
            database.trackDao().deleteTrackFromFavorites(trackConverter.map(track))
        }
    }

    override fun getTracksFromFavorites(): Flow<List<Track>> = flow {
        val tracks = withContext(Dispatchers.IO) {
            database.trackDao().getFavoritesTracks()
        }
        emit(convertFromTracksDbo(tracks))
    }

    private fun convertFromTracksDbo(tracks: List<TrackDbo>): List<Track> {
        return tracks.map { track ->
            trackConverter.map(track)
        }
    }

}