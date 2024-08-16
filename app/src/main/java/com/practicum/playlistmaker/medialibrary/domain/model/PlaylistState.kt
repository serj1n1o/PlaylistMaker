package com.practicum.playlistmaker.medialibrary.domain.model

sealed interface PlaylistState {
    data class Content(val playlists: List<Playlist>) : PlaylistState
    data object Empty : PlaylistState

}
