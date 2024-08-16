package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.medialibrary.domain.model.PlaylistState
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistViewModel(
    private val sharingInteractor: SharingInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val playlistState = MutableLiveData<PlaylistState>()

    fun getPlaylistState(): LiveData<PlaylistState> = playlistState

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylist()
                .collect { playlists ->
                    processResult(playlists)
                }
        }

    }

    suspend fun saveImageToStorage(coverUri: Uri, namePlaylist: String): Uri {
        return withContext(Dispatchers.IO) {
            sharingInteractor.saveImageToStorage(coverUri, namePlaylist)
        }
    }

    fun openSettingsPermission() {
        sharingInteractor.openSettingsPermission()
    }

    suspend fun createPlaylist(name: String?, description: String?, cover: Uri?) {
        if (name != null) {
            playlistInteractor.insertPlaylist(
                Playlist(
                    id = null,
                    name = name,
                    description = description,
                    cover = cover,
                    listTrackId = emptyList(),
                )
            )
        }

    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) renderState(PlaylistState.Empty)
        else renderState(PlaylistState.Content(playlists))
    }

    private fun renderState(state: PlaylistState) {
        playlistState.postValue(state)
    }


}