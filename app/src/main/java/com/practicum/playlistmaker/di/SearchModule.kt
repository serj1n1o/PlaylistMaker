package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.converters.TrackConverter
import com.practicum.playlistmaker.search.data.localstorage.HistoryTracksStorage
import com.practicum.playlistmaker.search.data.localstorage.HistoryTracksStorageImpl
import com.practicum.playlistmaker.search.data.network.ItunesApiService
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.HistoryTracksRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.HistoryTracksInteractor
import com.practicum.playlistmaker.search.domain.api.HistoryTracksRepository
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.HistoryTracksInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.ui.viewmodel.TrackSearchViewModel
import org.koin.android.ext.koin.androidContext

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


val searchModule = module {

    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl(androidContext().getString(R.string.base_url_itunes))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<ItunesApiService>()
    }

    factory {
        TrackConverter()
    }

    factory<NetworkClient> {
        RetrofitNetworkClient(itunesApiService = get(), context = get())
    }


    factory<TrackRepository> {
        TrackRepositoryImpl(
            networkClient = get(),
            favoritesDatabase = get(),
            trackConverter = get()
        )
    }
    factory<TracksInteractor> {
        TracksInteractorImpl(repository = get())
    }


    factory<HistoryTracksStorage> {
        HistoryTracksStorageImpl(context = get())
    }

    factory<HistoryTracksRepository> {
        HistoryTracksRepositoryImpl(
            historyTracksStorage = get(),
            favoritesDatabase = get(),
            trackConverter = get()
        )
    }
    factory<HistoryTracksInteractor> {
        HistoryTracksInteractorImpl(historyTracksRepository = get())
    }


    viewModel<TrackSearchViewModel> {
        TrackSearchViewModel(tracksInteractor = get(), historyTracksInteractor = get())
    }
}



