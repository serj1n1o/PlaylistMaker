package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.CodesRequest
import com.practicum.playlistmaker.search.data.dto.ItunesRequest
import com.practicum.playlistmaker.search.data.dto.ItunesResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.MapperDateTimeFormatter
import com.practicum.playlistmaker.util.Resource

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
) : TrackRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(ItunesRequest(expression))
        return when (response.resultCode) {
            CodesRequest.CODE_NO_CONNECT -> Resource.Error(resultCode = response.resultCode)

            in 200..300 -> {
                if ((response as ItunesResponse).tracks.isEmpty()) {
                    Resource.Error(resultCode = CodesRequest.CODE_NO_FOUND)
                } else {
                    Resource.Success(data = response.tracks.map {
                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTime = MapperDateTimeFormatter.mapTimeMillisToMinAndSec(it.trackTimeMillis),
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            releaseYear = MapperDateTimeFormatter.mapDateToYear(it.releaseDate),
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl
                        )
                    })
                }
            }

            else -> Resource.Error(resultCode = response.resultCode)

        }
    }

}