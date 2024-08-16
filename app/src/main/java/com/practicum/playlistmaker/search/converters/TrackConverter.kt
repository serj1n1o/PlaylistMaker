package com.practicum.playlistmaker.search.converters

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.DataMapper

class TrackConverter {

    fun convertTrackDtoFromTrack(track: TrackDto, inFavorite: Boolean): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = DataMapper.mapTimeMillisToMinAndSec(track.trackTimeMillis),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseYear = DataMapper.mapDateToYear(track.releaseDate),
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            inFavorite = inFavorite,
        )
    }

    fun convertTrackFromTrackAndCheckFavorites(track: Track, inFavorite: Boolean): Track {
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
            inFavorite = inFavorite,
        )
    }
}