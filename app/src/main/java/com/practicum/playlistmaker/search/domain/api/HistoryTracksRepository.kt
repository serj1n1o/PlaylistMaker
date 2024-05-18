package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface HistoryTracksRepository {
    fun addTrackToHistory(track: Track)
    fun getTracksHistory(): List<Track>
    fun clearHistory()
}