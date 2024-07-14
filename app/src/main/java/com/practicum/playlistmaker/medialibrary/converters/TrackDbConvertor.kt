package com.practicum.playlistmaker.medialibrary.converters

import com.practicum.playlistmaker.db.entity.TrackDbo
import com.practicum.playlistmaker.search.domain.models.Track

class TrackDbConvertor {

    fun map(track: Track): TrackDbo {
        return TrackDbo(
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

    fun map(track: TrackDbo): Track {
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