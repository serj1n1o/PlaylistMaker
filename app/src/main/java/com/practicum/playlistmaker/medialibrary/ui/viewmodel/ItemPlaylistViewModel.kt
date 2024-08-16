package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.DurationAndAmountTracks
import com.practicum.playlistmaker.medialibrary.domain.model.ItemPlaylistState
import com.practicum.playlistmaker.medialibrary.domain.model.TrackPlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.util.DataMapper
import com.practicum.playlistmaker.util.PlaylistUpdateAction
import kotlinx.coroutines.launch

class ItemPlaylistViewModel(
    private val sharingInteractor: SharingInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val durationTracksAndAmount = MutableLiveData<DurationAndAmountTracks>()
    fun getTotalDurationTracksAndAmount(): LiveData<DurationAndAmountTracks> =
        durationTracksAndAmount

    private val playlistState = MutableLiveData<ItemPlaylistState>()
    fun getPlaylistState(): LiveData<ItemPlaylistState> = playlistState


    private val trackState = MutableLiveData<TrackPlaylistState>(TrackPlaylistState.Empty)
    val getTrackState = trackState


    fun getPlaylist(id: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(id)
            playlistState.postValue(ItemPlaylistState.Content(playlist))

            playlist.id?.let { getTracksInfo(it) }

        }
    }


    fun removeTrackFromPlaylist(trackId: Long) {
        val playlist = (playlistState.value as ItemPlaylistState.Content).playlist
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist, trackId, PlaylistUpdateAction.REMOVE)
            playlist.id?.let { getTracksInfo(it) }
        }
    }

    fun removePlaylist() {
        val playlist = (playlistState.value as ItemPlaylistState.Content).playlist
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }

    fun sharePlaylist() {
        val message = generateMessagePlaylistInfo()
        message?.let { sharingInteractor.sharePlaylist(it) }
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun getTracksInfo(playlistId: Long) {
        val newPlaylist = playlistInteractor.getPlaylistById(playlistId)
        playlistInteractor.getTracksInPlaylist(newPlaylist.listTrackId).collect { tracks ->
            renderTrack(tracks)

            val duration = totalDurationTracksToMinutes(
                tracks.sumOf { DataMapper.mapMinAndSecTimeToMillis(it.trackTime) }
            )
            durationTracksAndAmount.postValue(
                DurationAndAmountTracks(duration, newPlaylist.amountTracks)
            )
        }
    }

    private fun generateMessagePlaylistInfo(): String? {
        if (playlistState.value is ItemPlaylistState.Content && trackState.value is TrackPlaylistState.Content) {
            val playlist = (playlistState.value as ItemPlaylistState.Content).playlist
            val tracks = (trackState.value as TrackPlaylistState.Content).tracks
            val message = StringBuilder()

            message.append("${playlist.name}\n")

            if (!playlist.description.isNullOrEmpty()) message.append("${playlist.description}\n")

            message.append("${DataMapper.mapAmountTrackToString(playlist.amountTracks)}\n")

            tracks.forEachIndexed { index, track ->
                message.append("${index + 1}.${track.artistName} - ${track.trackName} ${track.trackTime}\n")
            }
            return message.toString()
        }
        return null
    }

    private fun totalDurationTracksToMinutes(value: Int): String {
        return DataMapper.mapDurationToString(value)
    }

    private fun renderTrack(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            trackState.postValue(TrackPlaylistState.Empty)
        } else {
            trackState.postValue(TrackPlaylistState.Content(tracks))
        }
    }

}