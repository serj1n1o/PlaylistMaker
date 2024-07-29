package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.markodevcic.peko.PermissionRequester
import com.practicum.playlistmaker.di.mediaLibraryModule
import com.practicum.playlistmaker.di.playerModule
import com.practicum.playlistmaker.di.searchModule
import com.practicum.playlistmaker.di.settingsModule
import com.practicum.playlistmaker.di.sharingModule
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class App : Application() {

    private var isDarkTheme = false

    override fun onCreate() {
        super.onCreate()
        PermissionRequester.initialize(this)
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(playerModule, searchModule, settingsModule, sharingModule, mediaLibraryModule)
        }

        val themeAppInteractor by inject<SettingsInteractor>()
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