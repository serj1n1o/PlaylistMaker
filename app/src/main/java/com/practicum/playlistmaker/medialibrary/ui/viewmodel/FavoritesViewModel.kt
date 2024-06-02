package com.practicum.playlistmaker.medialibrary.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {

    private val favoritesState = MutableLiveData<FavoritesState>(FavoritesState.Empty)

    fun getFavoritesState(): LiveData<FavoritesState> = favoritesState
}