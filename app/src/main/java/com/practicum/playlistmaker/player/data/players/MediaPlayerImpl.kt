package com.practicum.playlistmaker.player.data.players

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.api.Player
import com.practicum.playlistmaker.player.domain.models.PlayerState

class MediaPlayerImpl : Player {
    override var playerState = PlayerState.DEFAULT
    private val mediaPlayer = MediaPlayer()
    private var onEndCallback: (() -> Unit)? = null

    override fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            onEndCallback?.invoke()
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

    override fun currentPositionTrack(): Int {
        return mediaPlayer.currentPosition
    }

    override fun seekToTrack(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun addOnEndCallback(callback: () -> Unit) {
        this.onEndCallback = callback
    }

    override fun release() {
        mediaPlayer.release()
    }
}