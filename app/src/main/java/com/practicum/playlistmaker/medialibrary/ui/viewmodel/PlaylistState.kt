package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface PlaylistState {
    data class Content(val playlists: List<List<Track>>) : PlaylistState
    data object Empty : PlaylistState

}
