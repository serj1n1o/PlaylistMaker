package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface HistoryTracksInteractor {
    fun addTrackToHistory(track: Track)
    suspend fun getTracksHistory(): List<Track>
    fun clearHistory()
}