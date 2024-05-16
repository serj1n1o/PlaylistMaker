package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.practicum.playlistmaker.PREFERENCES_SETTINGS
import com.practicum.playlistmaker.SETTINGS_KEY_THEME
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_SETTINGS, Context.MODE_PRIVATE
    )

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            sharedPreferences.getBoolean(
                SETTINGS_KEY_THEME,
                context.applicationContext.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            )
        )
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit().putBoolean(SETTINGS_KEY_THEME, settings.isDarkTheme).apply()
    }
}