package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.ItunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    suspend fun getTrack(
        @Query("term") nameSong: String,
    ): ItunesResponse
}