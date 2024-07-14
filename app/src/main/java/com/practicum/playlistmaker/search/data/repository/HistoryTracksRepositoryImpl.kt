package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.db.FavoritesDatabase
import com.practicum.playlistmaker.search.converters.TrackConverter
import com.practicum.playlistmaker.search.data.localstorage.HistoryTracksStorage
import com.practicum.playlistmaker.search.domain.api.HistoryTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryTracksRepositoryImpl(
    private val historyTracksStorage: HistoryTracksStorage,
    private val favoritesDatabase: FavoritesDatabase,
    private val trackConverter: TrackConverter,
) : HistoryTracksRepository {
    override fun addTrackToHistory(track: Track) {
        historyTracksStorage.addTrackToHistory(track)
    }

    override suspend fun getTracksHistory(): List<Track> {
        val tracksIdInFavorites = withContext(Dispatchers.IO) {
            favoritesDatabase.trackDao().getTracksIdFromFavorites()
        }
        return historyTracksStorage.getTracksHistory().map {
            trackConverter.convertTrackFromTrackAndCheckFavorites(
                track = it,
                inFavorite = it.trackId in tracksIdInFavorites
            )
        }
    }

    override fun clearHistory() {
        historyTracksStorage.clearHistory()
    }
}