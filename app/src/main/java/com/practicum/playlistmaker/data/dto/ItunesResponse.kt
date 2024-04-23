package com.practicum.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName
import com.practicum.playlistmaker.domain.models.Track

data class ItunesResponse(
    val resultCount: Int,
    @SerializedName("results") val tracks: List<Track>
) : Response()