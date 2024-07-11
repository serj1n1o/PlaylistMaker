package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.CodesRequest
import com.practicum.playlistmaker.search.data.dto.ItunesRequest
import com.practicum.playlistmaker.search.data.dto.ItunesResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.MapperDateTimeFormatter
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
) : TrackRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(ItunesRequest(expression))
        when (response.resultCode) {
            CodesRequest.CODE_NO_CONNECT -> emit(Resource.Error(resultCode = response.resultCode))

            CodesRequest.CODE_OK -> {
                if ((response as ItunesResponse).tracks.isEmpty()) {
                    emit(Resource.Error(resultCode = CodesRequest.CODE_NO_FOUND))
                } else {
                    val dataTracks = response.tracks.map {
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
                    }
                    emit(Resource.Success(data = dataTracks))
                }
            }

            else -> emit(Resource.Error(resultCode = response.resultCode))

        }
    }

}