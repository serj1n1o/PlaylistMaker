package com.practicum.playlistmaker.settings.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.util.Creator

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    application: Application,
) : ViewModel() {

    companion object {
        fun getSettingsViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sharingInteractor = Creator.provideSharingInteractor()
                val settingsInteractor = Creator.provideSettingsInteractor()
                val application = Creator.application
                SettingsViewModel(sharingInteractor, settingsInteractor, application)
            }
        }
    }

    private val myApp by lazy { application as App }

    private val themeStateLiveData = MutableLiveData<ThemeSettings>()

    init {
        themeStateLiveData.value = settingsInteractor.getThemeApp()
    }

    fun getThemeStateLiveData(): LiveData<ThemeSettings> = themeStateLiveData

    fun switchTheme(isDarkTheme: Boolean) {
        if (isDarkTheme == themeStateLiveData.value?.isDarkTheme) {
            return
        }
        settingsInteractor.updateThemeApp(ThemeSettings(isDarkTheme))
        themeStateLiveData.value = settingsInteractor.getThemeApp()

        themeStateLiveData.value?.let { myApp.switchTheme(it.isDarkTheme) }
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

}