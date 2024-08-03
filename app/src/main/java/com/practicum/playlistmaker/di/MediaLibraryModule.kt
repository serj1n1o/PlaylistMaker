package com.practicum.playlistmaker.di

import androidx.room.Room
import com.practicum.playlistmaker.db.Database
import com.practicum.playlistmaker.medialibrary.converters.PlaylistDbConverter
import com.practicum.playlistmaker.medialibrary.converters.TrackDbConverter
import com.practicum.playlistmaker.medialibrary.data.repository.FavoritesRepositoryImpl
import com.practicum.playlistmaker.medialibrary.data.repository.PlaylistRepositoryImpl
import com.practicum.playlistmaker.medialibrary.domain.dbapi.FavoritesInteractor
import com.practicum.playlistmaker.medialibrary.domain.dbapi.FavoritesRepository
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistRepository
import com.practicum.playlistmaker.medialibrary.domain.impl.FavoritesInteractorImpl
import com.practicum.playlistmaker.medialibrary.domain.impl.PlaylistInteractorImpl
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.medialibrary.ui.viewmodel.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    viewModel<FavoritesViewModel> {
        FavoritesViewModel(favoritesInteractor = get())
    }

    viewModel<PlaylistViewModel> {
        PlaylistViewModel(sharingInteractor = get(), playlistInteractor = get())
    }

    factory { TrackDbConverter() }
    factory { PlaylistDbConverter() }

    single {
        Room.databaseBuilder(
            context = get(),
            klass = Database::class.java,
            name = "playlist_maker_database.db"
        ).build()
    }



    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(database = get(), playlistConverter = get())
    }

    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(playlistRepository = get())
    }


    factory<FavoritesRepository> {
        FavoritesRepositoryImpl(database = get(), trackConverter = get())
    }

    factory<FavoritesInteractor> {
        FavoritesInteractorImpl(favoritesRepository = get())
    }

}