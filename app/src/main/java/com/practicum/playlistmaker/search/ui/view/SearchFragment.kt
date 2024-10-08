package com.practicum.playlistmaker.search.ui.view


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.view.AudioPlayer
import com.practicum.playlistmaker.search.CodesRequest
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.adapter.HistoryTrackAdapter
import com.practicum.playlistmaker.search.ui.adapter.TrackAdapter
import com.practicum.playlistmaker.search.ui.viewmodel.HistoryState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchState
import com.practicum.playlistmaker.search.ui.viewmodel.TrackSearchViewModel
import com.practicum.playlistmaker.util.FragmentWithBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : FragmentWithBinding<FragmentSearchBinding>() {


    private val viewModel by viewModel<TrackSearchViewModel>()

    private var inputText = INPUT_TEXT_DEFAULT

    private val trackAdapter by lazy {
        TrackAdapter<Any>(
            object : TrackAdapter.TrackClickListener {
                override val onItemClickListener: ((Track) -> Unit)
                    @SuppressLint("NotifyDataSetChanged")
                    get() = { track ->
                        viewModel.addTrackToHistory(track)
                        historyTrackAdapter.notifyDataSetChanged()
                        if (clickDebounce()) {
                            startAudioPlayer(track)
                        }
                    }
                override val onLongTrackClickListener: ((Track) -> Boolean)?
                    get() = { false }

            }
        )
    }

    private val historyTrackAdapter by lazy {
        HistoryTrackAdapter(
            object : TrackAdapter.TrackClickListener {
                override val onItemClickListener: ((Track) -> Unit)
                    get() = { track ->
                        viewModel.addTrackToHistory(track)
                        if (clickDebounce()) {
                            startAudioPlayer(track)
                        }
                    }
                override val onLongTrackClickListener: ((Track) -> Boolean)?
                    get() = { false }
            }
        )
    }


    private var isNotEmptyHistoryTracks = false

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.recyclerSearchTrack.adapter = trackAdapter

        binding.recyclerHistoryList.adapter = historyTrackAdapter
        viewModel.getTracksHistory()

        viewModel.observeHistoryState().observe(viewLifecycleOwner) {
            renderHistory(it)
        }

        if (isNotEmptyHistoryTracks && binding.inputSearch.hasFocus()) {
            showHistory()
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
            viewModel.clearResultSearchTracks()
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
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayer,
            AudioPlayer.createArgs(track)
        )
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            inputText = savedInstanceState.getString(INPUT_TEXT, INPUT_TEXT_DEFAULT)
        }
        binding.inputSearch.apply {
            setText(inputText)
            setSelection(binding.inputSearch.text.length)
        }
    }

    override fun onDestroyView() {
        with(binding) {
            recyclerSearchTrack.adapter = null
            recyclerHistoryList.adapter = null
            trackAdapter.tracks.clear()
        }
        super.onDestroyView()
    }

    private fun hideKeyboard() {
        val inputManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)
    }

    companion object {
        private const val INPUT_TEXT = "INPUT_TEXT"
        private const val INPUT_TEXT_DEFAULT = ""
        private const val PLACEHOLDER_CODE_NOT_VISIBLE = 0
    }

}