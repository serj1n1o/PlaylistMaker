package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.localstorage.HistoryTracksStorage
import com.practicum.playlistmaker.search.domain.api.HistoryTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track

class HistoryTracksRepositoryImpl(private val historyTracksStorage: HistoryTracksStorage) :
    HistoryTracksRepository {
    override fun addTrackToHistory(track: Track) {
        historyTracksStorage.addTrackToHistory(track)
    }

    override fun getTracksHistory(): List<Track> {
        return historyTracksStorage.getTracksHistory()
    }

    override fun clearHistory() {
        historyTracksStorage.clearHistory()
    }
}