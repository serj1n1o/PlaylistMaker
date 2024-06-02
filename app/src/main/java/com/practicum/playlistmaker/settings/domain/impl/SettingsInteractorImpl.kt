package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun getThemeApp(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }
    override fun updateThemeApp(settings: ThemeSettings) {
        settingsRepository.updateThemeSettings(settings)
    }
}