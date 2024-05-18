package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface PlayerManager {
    fun getPlayerState(): PlayerState
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun currentPositionTrack(): Int
    fun release()
    fun seekToTrack(position: Int)
}