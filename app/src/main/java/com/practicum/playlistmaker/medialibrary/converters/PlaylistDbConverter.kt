package com.practicum.playlistmaker.medialibrary.converters

import com.practicum.playlistmaker.db.entity.PlaylistDbo
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistDbo {
        return PlaylistDbo(
            name = playlist.name,
            description = playlist.description ?: "",
            cover = playlist.cover,
            listTrackIdDbo = emptyList(),
            amountTracks = playlist.amountTracks,
        )
    }

    fun map(playlistDbo: PlaylistDbo): Playlist {
        return Playlist(
            name = playlistDbo.name,
            description = playlistDbo.description,
            cover = playlistDbo.cover,
            amountTracks = playlistDbo.amountTracks,
        )
    }
}