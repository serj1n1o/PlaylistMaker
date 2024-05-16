package com.practicum.playlistmaker.settings.domain.api

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeApp(): ThemeSettings
    fun updateThemeApp(settings: ThemeSettings)
}