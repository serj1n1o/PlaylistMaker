package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.CodesRequest
import com.practicum.playlistmaker.search.data.dto.ItunesRequest
import com.practicum.playlistmaker.search.data.dto.Response
import java.io.IOException

class RetrofitNetworkClient(
    private val itunesApiService: ItunesApiService,
    private val context: Context,
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = CodesRequest.CODE_NO_CONNECT }
        }
        if (dto !is ItunesRequest) {
            return Response().apply { resultCode = CodesRequest.CODE_BAD_REQUEST }
        }
        return try {
            val response = itunesApiService.getTrack(dto.expression).execute()
            val bodyResponse = response.body()
            bodyResponse?.apply { resultCode = response.code() }
                ?: Response().apply { resultCode = response.code() }
        } catch (e: IOException) {
            Response().apply { resultCode = CodesRequest.CODE_NO_CONNECT }
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