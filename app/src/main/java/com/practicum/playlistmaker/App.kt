package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.util.Creator

const val PREFERENCES_SETTINGS = "PREFERENCES_SETTINGS"
const val SETTINGS_KEY_THEME = "SETTINGS_KEY_THEME"
const val DATA_FROM_AUDIO_PLAYER_KEY = "TRACK DATA"
const val CODE_NO_CONNECT = -1
const val CODE_BAD_REQUEST = 400
const val CODE_NO_FOUND = 1

class App : Application() {

    var isDarkTheme = false
    private val sharedPreferencesSettings by lazy {
        getSharedPreferences(
            PREFERENCES_SETTINGS, MODE_PRIVATE
        )
    }

    override fun onCreate() {
        super.onCreate()
        Creator.application = this
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