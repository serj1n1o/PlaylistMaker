package com.practicum.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search?entity=song")
    fun getTrack(
        @Query("term") nameSong: String
    ): Call<ItunesResponse>
}