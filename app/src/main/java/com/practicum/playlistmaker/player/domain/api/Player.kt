package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.domain.models.PlayerState

interface Player {
    var playerState: PlayerState
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun currentPositionTrack(): Int
    fun release()
    fun seekToTrack(position: Int)
    fun addOnEndCallback(callback: () -> Unit)

}