package com.practicum.playlistmaker.medialibrary.data.repository

import com.practicum.playlistmaker.db.FavoritesDatabase
import com.practicum.playlistmaker.db.entity.TrackDbo
import com.practicum.playlistmaker.medialibrary.converters.TrackDbConverter
import com.practicum.playlistmaker.medialibrary.domain.dbapi.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val favoritesDatabase: FavoritesDatabase,
    private val trackConverter: TrackDbConverter,
) : FavoritesRepository {
    override suspend fun addTrackToFavorites(track: Track) {
        withContext(Dispatchers.IO) {
            favoritesDatabase.trackDao().insertTrackToFavorites(trackConverter.map(track))
        }
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        withContext(Dispatchers.IO) {
            favoritesDatabase.trackDao().deleteTrackFromFavorites(trackConverter.map(track))
        }
    }

    override fun getTracksFromFavorites(): Flow<List<Track>> = flow {
        val tracks = withContext(Dispatchers.IO) {
            favoritesDatabase.trackDao().getFavoritesTracks()
        }
        emit(convertFromTracksDbo(tracks))
    }

    private fun convertFromTracksDbo(tracks: List<TrackDbo>): List<Track> {
        return tracks.map { track ->
            trackConverter.map(track)
        }
    }

}