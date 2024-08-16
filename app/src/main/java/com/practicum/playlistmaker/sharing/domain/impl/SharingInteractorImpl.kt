package com.practicum.playlistmaker.sharing.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.InternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val internalNavigator: InternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink()
    }

    override fun openTerms() {
        externalNavigator.openLink()
    }

    override fun openSupport() {
        externalNavigator.openEmail()
    }

    override fun openSettingsPermission() {
        externalNavigator.openSettingsPermission()
    }

    override suspend fun saveImageToStorage(artworkUri: Uri, namePlaylist: String): Uri {
        return internalNavigator.saveImageToStorage(artworkUri, namePlaylist)
    }

    override fun sharePlaylist(playlistData: String) {
        externalNavigator.sharePlaylist(playlistData)
    }

}