package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}