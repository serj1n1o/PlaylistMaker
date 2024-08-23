package com.practicum.playlistmaker.medialibrary.domain.model

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface TrackPlaylistState {
    data class Content(val tracks: List<Track>) : TrackPlaylistState
    data object Empty : TrackPlaylistState
}