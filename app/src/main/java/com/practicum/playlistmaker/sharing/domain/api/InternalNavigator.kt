package com.practicum.playlistmaker.sharing.domain.api

import android.net.Uri

interface InternalNavigator {
    suspend fun saveImageToStorage(artworkUri: Uri, namePlaylist: String)
    suspend fun loadImageFromStorage(namePlaylist: String): Uri
}