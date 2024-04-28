package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.Player
import com.practicum.playlistmaker.domain.api.PlayerManager
import com.practicum.playlistmaker.domain.models.PlayerState

class PlayerManagerImpl(private val player: Player) : PlayerManager {

    override fun getPlayerState(): PlayerState {
        return player.playerState
    }

    override fun preparePlayer(url: String) {
        player.preparePlayer(url)
    }

    override fun startPlayer() {
        player.startPlayer()
    }

    override fun pausePlayer() {
        player.pausePlayer()
    }

    override fun currentPosition(): Int {
        return player.currentPosition()
    }

    override fun release() {
        player.release()
    }

}