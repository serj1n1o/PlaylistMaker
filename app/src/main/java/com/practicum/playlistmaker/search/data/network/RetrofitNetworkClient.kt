package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.CODE_BAD_REQUEST
import com.practicum.playlistmaker.CODE_NO_CONNECT
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.dto.ItunesRequest
import com.practicum.playlistmaker.search.data.dto.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.IOException

class RetrofitNetworkClient(private val context: Context) : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(context.getString(R.string.base_url_itunes))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesApiService = retrofit.create<ItunesApiService>()
    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = CODE_NO_CONNECT }
        }
        if (dto !is ItunesRequest) {
            return Response().apply { resultCode = CODE_BAD_REQUEST }
        }
        return try {
            val response = itunesApiService.getTrack(dto.expression).execute()
            val bodyResponse = response.body()
            bodyResponse?.apply { resultCode = response.code() }
                ?: Response().apply { resultCode = response.code() }
        } catch (e: IOException) {
            Response().apply { resultCode = CODE_NO_CONNECT }
        }

    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}