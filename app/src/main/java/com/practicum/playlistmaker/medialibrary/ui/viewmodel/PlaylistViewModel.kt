package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.dbapi.PlaylistInteractor
import com.practicum.playlistmaker.medialibrary.domain.model.Playlist
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val sharingInteractor: SharingInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val playlistState = MutableLiveData<PlaylistState>(PlaylistState.Empty)

    fun getPlaylistState(): LiveData<PlaylistState> = playlistState

    fun saveImageToStorage(artworkUri: Uri, namePlaylist: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sharingInteractor.saveImageToStorage(artworkUri, namePlaylist)
        }
    }

    fun loadImageFromStorage(namePlaylist: String, callback: (Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = sharingInteractor.loadImageFromStorage(namePlaylist)
            callback(uri)
        }

    }

    fun openSettingsPermission() {
        sharingInteractor.openSettingsPermission()
    }

    fun createPlaylist(name: String?, description: String?, cover: Uri?) {
        viewModelScope.launch {
            if (name != null) {
                playlistInteractor.insertPlaylist(
                    Playlist(
                        name = name,
                        description = description,
                        cover = cover
                    )
                )
            }
        }
    }


}