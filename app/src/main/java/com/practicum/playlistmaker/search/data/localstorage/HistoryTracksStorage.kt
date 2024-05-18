package com.practicum.playlistmaker.search.data.localstorage

import com.practicum.playlistmaker.search.domain.models.Track

interface HistoryTracksStorage {
    fun addTrackToHistory(track: Track)
    fun getTracksHistory(): MutableList<Track>
    fun clearHistory()
}