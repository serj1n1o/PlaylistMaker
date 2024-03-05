package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


const val PREFERENCES_SETTINGS = "PREFERENCES_SETTINGS"
const val SETTINGS_KEY_THEME = "SETTINGS_KEY_THEME"
const val PREFERENCES_HISTORY = "PREFERENCES HISTORY"
const val HISTORY_TRACK_KEY = "HISTORY TRACK KEY"

class App : Application() {
    var isDarkTheme = false
    private val sharedPreferencesSettings by lazy {
        getSharedPreferences(
            PREFERENCES_SETTINGS, MODE_PRIVATE
        )
    }

    override fun onCreate() {
        super.onCreate()
        isDarkTheme = sharedPreferencesSettings.getBoolean(
            SETTINGS_KEY_THEME,
            applicationContext.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        )
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferencesSettings.edit().putBoolean(SETTINGS_KEY_THEME, darkThemeEnabled).apply()
    }
}