package com.practicum.playlistmaker.util

import android.app.Application
import com.practicum.playlistmaker.player.data.players.MediaPlayerImpl
import com.practicum.playlistmaker.player.domain.api.Player
import com.practicum.playlistmaker.player.domain.api.PlayerCallback
import com.practicum.playlistmaker.player.domain.api.PlayerManager
import com.practicum.playlistmaker.player.domain.impl.PlayerManagerImpl
import com.practicum.playlistmaker.search.data.localstorage.HistoryTracksStorageImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.HistoryTracksRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.HistoryTracksInteractor
import com.practicum.playlistmaker.search.domain.api.HistoryTracksRepository
import com.practicum.playlistmaker.search.domain.api.TrackRepository
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.HistoryTracksInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl

object Creator {

    lateinit var application: Application

    private fun getPlayer(callback: PlayerCallback): Player {
        return MediaPlayerImpl(callback)
    }

    fun providePlayerManager(callback: PlayerCallback): PlayerManager {
        return PlayerManagerImpl(player = getPlayer(callback))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTrackRepository())
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(application.applicationContext))
    }


    fun provideHistoryTracksInteractor(): HistoryTracksInteractor {
        return HistoryTracksInteractorImpl(getHistoryTracksRepository())
    }

    private fun getHistoryTracksRepository(): HistoryTracksRepository {
        return HistoryTracksRepositoryImpl(HistoryTracksStorageImpl(application))
    }
}