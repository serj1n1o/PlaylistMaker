package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.medialibrary.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    viewModel<FavoritesViewModel> {
        FavoritesViewModel()
    }

    viewModel<PlaylistViewModel> {
        PlaylistViewModel()
    }
}