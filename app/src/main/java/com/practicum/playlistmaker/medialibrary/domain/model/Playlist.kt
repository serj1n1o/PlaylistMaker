package com.practicum.playlistmaker.medialibrary.domain.model

import android.net.Uri

data class Playlist(
    val id: Long?,
    val name: String,
    val description: String?,
    val cover: Uri? = null,
    val listTrackId: List<Long>,
    var amountTracks: Int = 0,
)
