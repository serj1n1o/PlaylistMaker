package com.practicum.playlistmaker

import com.practicum.playlistmaker.data.players.MediaPlayerImpl
import com.practicum.playlistmaker.domain.api.Player
import com.practicum.playlistmaker.domain.api.PlayerManager
import com.practicum.playlistmaker.domain.impl.PlayerManagerImpl

object Creator {
    private fun getPlayer(): Player {
        return MediaPlayerImpl()
    }

    fun providePlayerManager(): PlayerManager {
        return PlayerManagerImpl(player = getPlayer())
    }
}