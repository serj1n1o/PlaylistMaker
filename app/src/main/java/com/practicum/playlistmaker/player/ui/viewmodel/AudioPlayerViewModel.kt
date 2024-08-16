package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.dbapi.FavoritesInteractor
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistState
import com.practicum.playlistmaker.medialibrary.domain.model.StatusAdd
import com.practicum.playlistmaker.player.domain.api.PlayerManager
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.DataMapper
import com.practicum.playlistmaker.util.PlaylistUpdateAction
import com.practicum.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val mediaPlayer: PlayerManager,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {


    private var timerJob: Job? = null
    private var currentPositionVM = START_POSITION

    private val playerStateLiveData = MutableLiveData<PlayerState>()
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val currentPositionLiveData = MutableLiveData<String>()
    fun getCurrentPositionLiveData(): LiveData<String> = currentPositionLiveData

    private val playerScreenStateLiveData = MutableLiveData<Track>()
    fun getPlayerScreenState(): LiveData<Track> = playerScreenStateLiveData

    private val playlistStateLiveData = MutableLiveData<PlaylistState>()
    fun getPlaylistState(): LiveData<PlaylistState> = playlistStateLiveData

    private val trackStatusAddingLiveData = SingleLiveEvent<StatusAdd>()
    fun getTrackStatusAdd(): LiveData<StatusAdd> = trackStatusAddingLiveData

    fun prepared(url: String?) {
        if (url != null) {
            mediaPlayer.preparePlayer(url = url)
            playerStateLiveData.postValue(mediaPlayer.getPlayerState())
        }
    }

    fun togglePlaybackState() {
        if (playerStateLiveData.value != PlayerState.PLAYING) {
            play()
        } else {
            pause()
        }
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
        if (mediaPlayer.getPlayerState() == PlayerState.PLAYING) {
            mediaPlayer.pausePlayer()
            timerJob?.cancel()
            playerStateLiveData.value = mediaPlayer.getPlayerState()
            currentPositionVM = getCurrentPositionTrack()
        }

    }

    private fun getCurrentPositionTrack(): Int {
        return if (mediaPlayer.getPlayerState() == PlayerState.PLAYING || mediaPlayer.getPlayerState() == PlayerState.PAUSED) {
            mediaPlayer.currentPositionTrack()
        } else {
            START_POSITION
        }
    }

    fun setPlayerScreenState(track: Track) {
        playerScreenStateLiveData.value = track
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.getPlayerState() == PlayerState.PLAYING) {
                delay(DELAY)
                currentPositionLiveData.postValue(
                    DataMapper.mapTimeMillisToMinAndSec(
                        getCurrentPositionTrack()
                    )
                )
            }
        }
    }

    override fun onCleared() {
        mediaPlayer.release()
    }


    fun addToPlaylist(track: Track, playlist: Playlist) {

        val listIdTracks = playlist.listTrackId
        if (track.trackId in listIdTracks) {
            trackStatusAddingLiveData.postValue(StatusAdd.Failure(playlist.name))
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToTrackInPlaylist(track)
                val result =
                    playlistInteractor.updatePlaylist(
                        playlist = playlist,
                        trackId = track.trackId,
                        PlaylistUpdateAction.ADD
                    )
                trackStatusAddingLiveData.postValue(result)
            }

        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylist()
                .collect { playlists ->
                    processResultPlaylist(playlists)
                }
        }
    }

    fun addToFavorites(track: Track) {
        viewModelScope.launch {
            if (track.inFavorite) {
                track.inFavorite = false
                favoritesInteractor.deleteTrackFromFavorites(track)
                playerScreenStateLiveData.postValue(track)
            } else {
                track.inFavorite = true
                favoritesInteractor.addTrackToFavorites(track)
                playerScreenStateLiveData.postValue(track)
            }

        }

    }

    private fun processResultPlaylist(playlists: List<Playlist>) {
        if (playlists.isEmpty()) playlistStateLiveData.postValue(PlaylistState.Empty)
        else playlistStateLiveData.postValue(PlaylistState.Content(playlists))
    }

    companion object {
        private const val DELAY = 300L
        private const val START_POSITION = 0
    }

}