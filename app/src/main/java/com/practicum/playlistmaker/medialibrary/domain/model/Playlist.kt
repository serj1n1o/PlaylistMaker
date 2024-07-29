package com.practicum.playlistmaker.medialibrary.domain.model

import android.net.Uri

data class Playlist(
    val name: String,
    val description: String?,
    val cover: Uri?,
)
