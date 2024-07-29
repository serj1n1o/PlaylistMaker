package com.practicum.playlistmaker.sharing.domain.api

import android.net.Uri

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun openSupport()
    fun openSettingsPermission()
    suspend fun saveImageToStorage(artworkUri: Uri, namePlaylist: String)
    suspend fun loadImageFromStorage(namePlaylist: String): Uri
}