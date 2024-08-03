package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

sealed interface PlaylistState {
    data class Content(val playlists: List<Playlist>) : PlaylistState
    data object Empty : PlaylistState

}
