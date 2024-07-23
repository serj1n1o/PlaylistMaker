package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.dbapi.FavoritesInteractor
import com.practicum.playlistmaker.medialibrary.domain.dbapi.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) :
    FavoritesInteractor {
    override suspend fun addTrackToFavorites(track: Track) {
        favoritesRepository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        favoritesRepository.deleteTrackFromFavorites(track)
    }

    override fun getTracksFromFavorites(): Flow<List<Track>> {
        return favoritesRepository.getTracksFromFavorites()
    }

}