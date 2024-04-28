package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.ItunesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("/search?entity=song")
    fun getTrack(
        @Query("term") nameSong: String
    ): Call<ItunesResponse>
}