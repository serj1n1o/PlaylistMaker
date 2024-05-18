package com.practicum.playlistmaker.sharing.domain.api

interface ExternalNavigator {
    fun shareLink()
    fun openLink()
    fun openEmail()
}