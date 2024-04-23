package com.practicum.playlistmaker.data.players

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.api.Player
import com.practicum.playlistmaker.domain.models.PlayerState

class MediaPlayerImpl() : Player {

    override var playerState = PlayerState.DEFAULT
    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun currentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun release() {
        mediaPlayer.release()
    }


}