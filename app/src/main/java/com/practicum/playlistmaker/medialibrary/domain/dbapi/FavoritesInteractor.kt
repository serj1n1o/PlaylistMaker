package com.practicum.playlistmaker.medialibrary.domain.dbapi

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun addTrackToFavorites(track: Track)

    suspend fun deleteTrackFromFavorites(track: Track)

    fun getTracksFromFavorites(): Flow<List<Track>>
}