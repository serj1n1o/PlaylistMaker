package com.practicum.playlistmaker.medialibrary.converters

import android.net.Uri
import com.practicum.playlistmaker.db.entity.PlaylistDbo
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistDbo {
        return PlaylistDbo(
            name = playlist.name,
            description = playlist.description ?: "",
            cover = playlist.cover ?: Uri.EMPTY,
            listTrackIdDbo = emptyList(),
            amountTracks = 0,
        )
    }

    fun map(playlistDbo: PlaylistDbo): Playlist {
        return Playlist(
            name = playlistDbo.name,
            description = playlistDbo.description,
            cover = playlistDbo.cover,
        )
    }
}