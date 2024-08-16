package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.db.Database
import com.practicum.playlistmaker.search.CodesRequest
import com.practicum.playlistmaker.search.converters.TrackConverter
import com.practicum.playlistmaker.search.data.dto.ItunesRequest
import com.practicum.playlistmaker.search.data.dto.ItunesResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val database: Database,
    private val trackConverter: TrackConverter,
) : TrackRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(ItunesRequest(expression))
        when (response.resultCode) {
            CodesRequest.CODE_NO_CONNECT -> emit(Resource.Error(resultCode = response.resultCode))

            CodesRequest.CODE_OK -> {
                if ((response as ItunesResponse).tracks.isEmpty()) {
                    emit(Resource.Error(resultCode = CodesRequest.CODE_NO_FOUND))
                } else {
                    val tracksIdInFavorites = withContext(Dispatchers.IO) {
                        database.trackDao().getTracksIdFromFavorites()
                    }

                    val dataTracks = response.tracks.map {
                        trackConverter.convertTrackDtoFromTrack(
                            track = it,
                            inFavorite = it.trackId in tracksIdInFavorites
                        )
                    }
                    emit(Resource.Success(data = dataTracks))
                }
            }

            else -> emit(Resource.Error(resultCode = response.resultCode))

        }
    }


}