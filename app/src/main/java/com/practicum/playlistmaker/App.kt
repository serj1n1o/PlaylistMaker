package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.util.Creator

const val PREFERENCES_SETTINGS = "PREFERENCES_SETTINGS"
const val SETTINGS_KEY_THEME = "SETTINGS_KEY_THEME"
const val DATA_FROM_AUDIO_PLAYER_KEY = "TRACK DATA"
const val CODE_NO_CONNECT = -1
const val CODE_BAD_REQUEST = 400
const val CODE_NO_FOUND = 1

class App : Application() {

    private var isDarkTheme = false

    override fun onCreate() {
        super.onCreate()
        Creator.application = this
        val themeInteractor = Creator.provideSettingsInteractor()
        isDarkTheme = themeInteractor.getThemeApp().isDarkTheme
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
    }
}