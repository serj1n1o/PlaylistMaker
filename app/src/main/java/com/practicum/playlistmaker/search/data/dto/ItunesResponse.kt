package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName

data class ItunesResponse(
    val resultCount: Int,
    @SerializedName("results") val tracks: List<TrackDto>,
) : Response()