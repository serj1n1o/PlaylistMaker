package com.practicum.playlistmaker.search.ui.view


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.gson.Gson
import com.practicum.playlistmaker.DATA_FROM_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.view.AudioPlayer
import com.practicum.playlistmaker.search.CodesRequest
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapter.HistoryTrackAdapter
import com.practicum.playlistmaker.search.ui.adapter.TrackAdapter
import com.practicum.playlistmaker.search.ui.viewmodel.HistoryState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchState
import com.practicum.playlistmaker.search.ui.viewmodel.TrackSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private var isClickAllowed = true
    private val handlerMain = Handler(Looper.getMainLooper())

    private val viewModel by viewModel<TrackSearchViewModel>()

    private var inputText = INPUT_TEXT_DEFAULT
    private val trackAdapter by lazy {
        TrackAdapter<Any>(
            object : TrackAdapter.TrackClickListener {
                override val onItemClickListener: ((Track) -> Unit)
                    @SuppressLint("NotifyDataSetChanged")
                    get() = {
                        viewModel.addTrackToHistory(it)
                        historyTrackAdapter.notifyDataSetChanged()
                        if (clickDebounce()) startAudioPlayer(it)
                    }

            }
        )
    }
    private val historyTrackAdapter by lazy {
        HistoryTrackAdapter(
            object : TrackAdapter.TrackClickListener {
                override val onItemClickListener: ((Track) -> Unit)
                    get() = { if (clickDebounce()) startAudioPlayer(it) }

            }
        )
    }
    private var isNotEmptyHistoryTracks = false


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.observeState().observe(this) {
            render(it)
        }
        binding.recyclerSearchTrack.adapter = trackAdapter
        binding.recyclerHistoryList.adapter = historyTrackAdapter
        viewModel.getTracksHistory()
        viewModel.observeHistoryState().observe(this) {
            renderHistory(it)
        }

        if (isNotEmptyHistoryTracks && binding.inputSearch.hasFocus()) {
            showHistory()
        }

        binding.buttonBackSearch.setOnClickListener {
            finish()
        }

        binding.iconClearInput.setOnClickListener {
            with(binding) {
                inputSearch.setText(INPUT_TEXT_DEFAULT)
                inputSearch.clearFocus()
                showPlaceHolder(PLACEHOLDER_CODE_NOT_VISIBLE)
            }
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            hideKeyboard()
        }

        binding.inputSearch.doOnTextChanged { text, _, _, _ ->
            binding.iconClearInput.visibility = clearInputButtonVisibility(text)
            inputText = text.toString()
            if (text.isNullOrEmpty() && binding.inputSearch.hasFocus() && isNotEmptyHistoryTracks) {
                showHistory()
                showPlaceHolder(PLACEHOLDER_CODE_NOT_VISIBLE)
            } else hideHistory()

            viewModel.searchDebounce(changedText = inputText)

        }


        binding.inputSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && isNotEmptyHistoryTracks && inputText.isEmpty()) {
                showHistory()
            } else hideHistory()
        }


        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistoryTracks()
            historyTrackAdapter.notifyDataSetChanged()
            hideHistory()
        }

    }

    private fun startAudioPlayer(track: Track) {
        startActivity(Intent(this, AudioPlayer::class.java).apply {
            putExtra(
                DATA_FROM_AUDIO_PLAYER_KEY, Gson().toJson(track)
            )
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderHistory(state: HistoryState) {
        isNotEmptyHistoryTracks = when (state) {
            is HistoryState.Content -> {
                historyTrackAdapter.tracks.clear()
                historyTrackAdapter.notifyDataSetChanged()
                historyTrackAdapter.tracks.addAll(state.tracksHistory)
                true
            }

            HistoryState.Empty -> {
                false
            }
        }
    }

    private fun showHistory() {
        with(binding) {
            recyclerSearchTrack.isVisible = false
            historyListView.isVisible = true
        }
    }

    private fun hideHistory() {
        binding.historyListView.isVisible = false
    }

    private fun clearInputButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }


    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showPlaceHolder(state.codeError)
            SearchState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            recyclerSearchTrack.isVisible = false
            hideHistory()
            placeholderError.isVisible = false
        }
    }

    private fun showPlaceHolder(code: Int) {
        binding.progressBar.isVisible = false
        when (code) {
            CodesRequest.CODE_NO_FOUND -> {
                with(binding) {
                    recyclerSearchTrack.isVisible = false
                    historyListView.isVisible = false
                    buttonUpdate.isVisible = false
                    textError.text = getString(R.string.nothing_found_txt)
                    placeholderError.isVisible = true
                }
            }

            PLACEHOLDER_CODE_NOT_VISIBLE -> binding.placeholderError.isVisible = false
            else -> with(binding) {
                recyclerSearchTrack.isVisible = false
                historyListView.isVisible = false
                textError.text = getString(R.string.no_connect_error_txt)
                buttonUpdate.isVisible = true
                placeholderError.isVisible = true
                buttonUpdate.setOnClickListener {
                    if (clickDebounce()) viewModel.updateSearchAfterError(inputText)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) {
        with(binding) {
            recyclerSearchTrack.isVisible = true
            recyclerSearchTrack.scrollToPosition(0)
            placeholderError.isVisible = false
            progressBar.isVisible = false
        }
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()

    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handlerMain.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        inputText = savedInstanceState.getString(INPUT_TEXT, INPUT_TEXT_DEFAULT)
        binding.inputSearch.apply {
            setText(inputText)
            setSelection(binding.inputSearch.text.length)
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val INPUT_TEXT_DEFAULT = ""
        private const val PLACEHOLDER_CODE_NOT_VISIBLE = 0
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}