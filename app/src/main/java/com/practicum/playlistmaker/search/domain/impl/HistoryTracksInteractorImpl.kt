package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.HistoryTracksInteractor
import com.practicum.playlistmaker.search.domain.api.HistoryTracksRepository
import com.practicum.playlistmaker.search.domain.models.Track

class HistoryTracksInteractorImpl(private val historyTracksRepository: HistoryTracksRepository) :
    HistoryTracksInteractor {
    override fun addTrackToHistory(track: Track) {
        historyTracksRepository.addTrackToHistory(track)
    }

    override fun getTracksHistory(): List<Track> {
        return historyTracksRepository.getTracksHistory()
    }

    override fun clearHistory() {
        historyTracksRepository.clearHistory()
    }
}