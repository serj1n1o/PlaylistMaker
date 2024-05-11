package com.practicum.playlistmaker.search.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.domain.api.HistoryTracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Creator

class TrackSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyTracksInteractor: HistoryTracksInteractor,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getSearchViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tracksInteractor = Creator.provideTracksInteractor()
                val historyTracksInteractor = Creator.provideHistoryTracksInteractor()
                TrackSearchViewModel(tracksInteractor, historyTracksInteractor)
            }
        }
    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val historyStateLiveData = MutableLiveData<HistoryState>()
    fun observeHistoryState(): LiveData<HistoryState> = historyStateLiveData

    private var latestSearchText: String? = null

    private val handler = Handler(Looper.getMainLooper())

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(searchText = changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
    }

    fun updateSearchBeforeError(searchText: String) {
        searchRequest(searchText = searchText)
    }

    private fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            tracksInteractor.searchTracks(
                searchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorCode: Int?) {
                        val tracks = mutableListOf<Track>()
                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorCode != null -> renderState(SearchState.Error(codeError = errorCode))
                            else -> renderState(SearchState.Content(tracks = tracks))
                        }
                    }

                })
        }
    }

    fun getTracksHistory() {
        val tracks = historyTracksInteractor.getTracksHistory()
        if (tracks.isEmpty()) {
            renderHistoryState(state = HistoryState.Empty)
        } else {
            renderHistoryState(state = HistoryState.Content(tracks))
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

    private fun renderHistoryState(state: HistoryState) {
        historyStateLiveData.postValue(state)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}