package com.practicum.playlistmaker.medialibrary.domain.model

sealed interface ItemPlaylistState {

    data class Content(val playlist: Playlist) : ItemPlaylistState

    data object Empty : ItemPlaylistState
}