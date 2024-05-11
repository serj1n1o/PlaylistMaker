package com.practicum.playlistmaker.search.ui.viewmodel

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    data object Loading : SearchState
    data class Content(val tracks: List<Track>) : SearchState
    data class Error(val codeError: Int) : SearchState

}