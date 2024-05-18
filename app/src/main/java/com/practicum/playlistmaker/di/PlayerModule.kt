package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.players.MediaPlayerImpl
import com.practicum.playlistmaker.player.domain.api.Player
import com.practicum.playlistmaker.player.domain.api.PlayerCallback
import com.practicum.playlistmaker.player.domain.api.PlayerManager
import com.practicum.playlistmaker.player.domain.impl.PlayerManagerImpl
import com.practicum.playlistmaker.player.ui.viewmodel.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module


val playerModule = module {

    factory<Player> { (callback: PlayerCallback) ->
        MediaPlayerImpl(callback = callback)
    }

    factory<PlayerManager> { (callback: PlayerCallback) ->
        PlayerManagerImpl(player = get { parametersOf(callback) })
    }

    viewModel<AudioPlayerViewModel> { (callback: PlayerCallback) ->
        AudioPlayerViewModel(mediaPlayer = get { parametersOf(callback) })
    }

}