package com.practicum.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.HistoryTracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class TrackSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyTracksInteractor: HistoryTracksInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val historyStateLiveData = MutableLiveData<HistoryState>()
    fun observeHistoryState(): LiveData<HistoryState> = historyStateLiveData

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { searchText ->
            searchRequest(searchText)
        }
    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        trackSearchDebounce(changedText)
    }

    fun updateSearchAfterError(searchText: String) {
        searchRequest(searchText = searchText)
    }

    private fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            stateLiveData.postValue(SearchState.Loading)

            viewModelScope.launch {
                tracksInteractor.searchTracks(searchText)
                    .collect { pairData ->
                        processResult(pairData.first, pairData.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorCode: Int?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            errorCode != null -> stateLiveData.postValue(SearchState.Error(codeError = errorCode))
            else -> stateLiveData.postValue(SearchState.Content(tracks = tracks))
        }
    }

    fun getTracksHistory() {
        viewModelScope.launch {
            val tracks = historyTracksInteractor.getTracksHistory()

            if (tracks.isEmpty()) {
                historyStateLiveData.postValue(HistoryState.Empty)
            } else {
                historyStateLiveData.postValue(HistoryState.Content(tracks))
            }
        }
    }

    fun addTrackToHistory(track: Track) {
        historyTracksInteractor.addTrackToHistory(track = track)
        getTracksHistory()
    }

    fun clearHistoryTracks() {
        historyTracksInteractor.clearHistory()
        getTracksHistory()
    }

    fun clearResultSearchTracks() {
        stateLiveData.value = SearchState.Content(emptyList())
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}