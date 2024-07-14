package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.dbapi.FavoritesInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val favoritesState = MutableLiveData<FavoritesState>()

    fun getFavoritesState(): LiveData<FavoritesState> = favoritesState


    fun getFavoritesTrack() {
        viewModelScope.launch {
            favoritesInteractor.getTracksFromFavorites()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) renderState(FavoritesState.Empty)
        else renderState(FavoritesState.Content(tracks))
    }

    private fun renderState(state: FavoritesState) {
        favoritesState.postValue(state)
    }
}