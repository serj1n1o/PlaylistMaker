package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val sharingModule = module {
    factory<ExternalNavigator> {
        ExternalNavigatorImpl(application = get())
    }
    factory<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }
}