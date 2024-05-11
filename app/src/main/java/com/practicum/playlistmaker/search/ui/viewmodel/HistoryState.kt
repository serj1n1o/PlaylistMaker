package com.practicum.playlistmaker.search.ui.viewmodel

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface HistoryState {
    data class Content(val tracksHistory: List<Track>) : HistoryState
    data object Empty : HistoryState
}