package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.PlayerManager
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.MapperDateTimeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val mediaPlayer: PlayerManager,
) : ViewModel() {


    private var timerJob: Job? = null
    private var currentPositionVM = START_POSITION

    private val playerStateLiveData = MutableLiveData<PlayerState>()
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val currentPositionLiveData = MutableLiveData<String>()
    fun getCurrentPositionLiveData(): LiveData<String> = currentPositionLiveData

    private val playerScreenStateLiveData = MutableLiveData<Track>()
    fun getPlayerScreenState(): LiveData<Track> = playerScreenStateLiveData

    fun prepared(url: String?) {
        if (url != null) {
            mediaPlayer.preparePlayer(url = url)
            playerStateLiveData.postValue(mediaPlayer.getPlayerState())
        }
    }

    fun togglePlaybackState() {
        if (playerStateLiveData.value != PlayerState.PLAYING) {
            play()
        } else pause()
    }

    private fun play() {
        mediaPlayer.seekToTrack(currentPositionVM)
        mediaPlayer.startPlayer()
        playerStateLiveData.value = mediaPlayer.getPlayerState()
        startTimer()

        mediaPlayer.addOnEndCallback {
            currentPositionVM = START_POSITION
            playerStateLiveData.value = PlayerState.PREPARED
        }
    }

    fun pause() {
        mediaPlayer.pausePlayer()
        timerJob?.cancel()
        playerStateLiveData.value = mediaPlayer.getPlayerState()
        currentPositionVM = getCurrentPositionTrack()
    }

    private fun getCurrentPositionTrack(): Int {
        return if (mediaPlayer.getPlayerState() == PlayerState.PLAYING || mediaPlayer.getPlayerState() == PlayerState.PAUSED) {
            mediaPlayer.currentPositionTrack()
        } else {
            START_POSITION
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.getPlayerState() == PlayerState.PLAYING) {
                delay(DELAY)
                currentPositionLiveData.postValue(
                    MapperDateTimeFormatter.mapTimeMillisToMinAndSec(
                        getCurrentPositionTrack()
                    )
                )
            }
        }
    }


    override fun onCleared() {
        mediaPlayer.release()
    }

    fun setPlayerScreenState(track: Track) {
        playerScreenStateLiveData.value = track
    }

    fun addToPlaylist(track: Track) {

    }

    fun addToFavorites(track: Track) {

    }

    companion object {
        private const val DELAY = 300L
        private const val START_POSITION = 0
    }

}