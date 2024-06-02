package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistViewModel : ViewModel() {

    private val playlistState = MutableLiveData<PlaylistState>(PlaylistState.Empty)

    fun getPlaylistState(): LiveData<PlaylistState> = playlistState
}