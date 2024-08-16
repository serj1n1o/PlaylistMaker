package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val sharingInteractor: SharingInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : PlaylistViewModel(sharingInteractor, playlistInteractor) {

    private val playlistData = MutableLiveData<Playlist>()
    fun getPlaylistData(): LiveData<Playlist> = playlistData

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlistData.postValue(playlist)
        }
    }

    suspend fun updatePlaylist(playlist: Playlist) {
        playlistInteractor.insertPlaylist(playlist)

    }

}