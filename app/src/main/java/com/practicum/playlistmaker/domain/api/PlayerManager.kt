package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.PlayerState

interface PlayerManager {
    fun getPlayerState(): PlayerState
    fun preparePlayer(url: String)
    fun startPlayer()
    fun pausePlayer()
    fun currentPosition(): Int
    fun release()
}