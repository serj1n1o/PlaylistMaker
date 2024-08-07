package com.practicum.playlistmaker.medialibrary.domain.model

sealed interface StatusAdd {
    data class Success(val playlistName: String) : StatusAdd
    data class Failure(val playlistName: String) : StatusAdd
}
