package com.practicum.playlistmaker.player.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.domain.api.PlayerCallback
import com.practicum.playlistmaker.player.domain.api.PlayerManager
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.util.MapperDateTimeFormatter

class AudioPlayerViewModel(
    private val mediaPlayer: PlayerManager,
) : ViewModel(), PlayerCallback {

    companion object {

        fun getPlayerViewModelFactory(playerCallback: PlayerCallback): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val mediaPlayer = Creator.providePlayerManager(playerCallback)
                    AudioPlayerViewModel(mediaPlayer)
                }
            }

        private const val DELAY = 300L
        private const val START_POSITION = 0
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false
    var currentPositionVM = START_POSITION

    private var playerStateLiveData = MutableLiveData<PlayerState>()
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val currentPositionLiveData = MutableLiveData<String>()
    fun getCurrentPositionLiveData(): LiveData<String> = currentPositionLiveData

    private var playerScreenStateLiveData = MutableLiveData<Track>()
    fun getPlayerScreenState(): LiveData<Track> = playerScreenStateLiveData
    fun prepared(url: String) {
        mediaPlayer.preparePlayer(url = url)
        playerStateLiveData.postValue(mediaPlayer.getPlayerState())
    }

    fun togglePlaybackState() {
        isPlaying = !isPlaying
        if (isPlaying) {
            play()
        } else pause()
    }

    private fun play() {
        mediaPlayer.seekToTrack(currentPositionVM)
        mediaPlayer.startPlayer()
        playerStateLiveData.value = mediaPlayer.getPlayerState()
        handler.post(startTimer())
    }

    private fun pause() {
        mediaPlayer.pausePlayer()
        playerStateLiveData.value = mediaPlayer.getPlayerState()
        currentPositionVM = getCurrentPositionTrack()
    }

    private fun getCurrentPositionTrack(): Int {
        return mediaPlayer.currentPositionTrack()
    }

    private fun startTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                when (mediaPlayer.getPlayerState()) {
                    PlayerState.PLAYING -> {
                        currentPositionLiveData.value =
                            MapperDateTimeFormatter.mapTimeMillisToMinAndSec(getCurrentPositionTrack())
                        handler.postDelayed(this, DELAY)
                    }

                    PlayerState.PAUSED -> handler.removeCallbacks(this)

                    PlayerState.PREPARED -> {
                        isPlaying = false
                        playerStateLiveData.value = PlayerState.PREPARED
                        handler.removeCallbacks(this)

                    }

                    else -> {
                        return
                    }
                }
            }
        }
    }

    fun release() = mediaPlayer.release()

    override fun onCleared() {
        handler.removeCallbacks(startTimer())
    }

    fun setPlayerScreenState(track: Track) {
        playerScreenStateLiveData.value = track
    }

    override fun onTrackEnded() {
        currentPositionVM = START_POSITION
    }

}