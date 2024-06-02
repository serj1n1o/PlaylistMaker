package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoritesState {
    data class Content(val favoritesList: List<Track>) : FavoritesState
    data object Empty : FavoritesState
}