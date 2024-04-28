package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.ItunesRequest
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.interfaces.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(R.string.base_url_itunes.toString())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesApiService = retrofit.create<ItunesApiService>()
    override fun doRequest(dto: Any): Response {
        return if (dto is ItunesRequest) {
            val response = itunesApiService.getTrack(dto.expression).execute()
            val bodyResponse = response.body() ?: Response()
            bodyResponse.apply { response.code() }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}