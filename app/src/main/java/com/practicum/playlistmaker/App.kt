package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.util.Creator

const val DATA_FROM_AUDIO_PLAYER_KEY = "TRACK DATA"

class App : Application() {

    private var isDarkTheme = false

    override fun onCreate() {
        super.onCreate()
        Creator.application = this
        val themeAppInteractor = Creator.provideSettingsInteractor()
        isDarkTheme = themeAppInteractor.getThemeApp().isDarkTheme
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