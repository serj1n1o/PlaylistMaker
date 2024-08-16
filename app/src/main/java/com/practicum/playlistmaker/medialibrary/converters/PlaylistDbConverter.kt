package com.practicum.playlistmaker.medialibrary.converters

import com.practicum.playlistmaker.db.entity.PlaylistDbo
import com.practicum.playlistmaker.db.entity.TrackInPlaylistDbo
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistDbo {
        return PlaylistDbo(
            playlistId = playlist.id,
            name = playlist.name,
            description = playlist.description ?: "",
            cover = playlist.cover,
            listTrackIdDbo = playlist.listTrackId,
            amountTracks = playlist.amountTracks,
        )
    }

    fun map(playlistDbo: PlaylistDbo): Playlist {
        return Playlist(
            id = playlistDbo.playlistId,
            name = playlistDbo.name,
            description = playlistDbo.description,
            cover = playlistDbo.cover,
            listTrackId = playlistDbo.listTrackIdDbo,
            amountTracks = playlistDbo.amountTracks,
        )
    }

    fun mapTrackToTrackInPlaylist(track: Track): TrackInPlaylistDbo {
        return TrackInPlaylistDbo(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseYear = track.releaseYear,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            inFavorite = track.inFavorite
        )
    }

    fun mapTrackToTrackInPlaylist(track: TrackInPlaylistDbo): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseYear = track.releaseYear,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            inFavorite = track.inFavorite
        )
    }


}