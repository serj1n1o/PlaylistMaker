package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.Player
import com.practicum.playlistmaker.player.domain.api.PlayerManager
import com.practicum.playlistmaker.player.domain.models.PlayerState

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

    override fun currentPositionTrack(): Int {
        return player.currentPositionTrack()
    }

    override fun release() {
        player.release()
    }

    override fun seekToTrack(position: Int) {
        player.seekToTrack(position)
    }

}