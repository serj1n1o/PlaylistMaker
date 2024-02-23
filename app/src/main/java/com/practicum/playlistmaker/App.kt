package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


const val PREFERENCES_SETTINGS = "PREFERENCES_SETTINGS"
const val SETTINGS_KEY_THEME = "SETTINGS_KEY_THEME"

class App : Application() {
    var isDarkTheme = false
    private val sharedPreferences by lazy {
        getSharedPreferences(
            PREFERENCES_SETTINGS,
            MODE_PRIVATE
        )
    }

    override fun onCreate() {
        super.onCreate()
        isDarkTheme = sharedPreferences.getBoolean(SETTINGS_KEY_THEME, false)
        if (sharedPreferences.contains(SETTINGS_KEY_THEME)) {
            switchTheme(isDarkTheme)
        } else {
            isDarkTheme =
                applicationContext.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
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
        sharedPreferences.edit().putBoolean(SETTINGS_KEY_THEME, darkThemeEnabled).apply()
    }
}