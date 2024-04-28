package com.practicum.playlistmaker.ui.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.practicum.playlistmaker.DATA_FROM_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.PREFERENCES_HISTORY
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.ItunesResponse
import com.practicum.playlistmaker.data.localstorage.SearchHistory
import com.practicum.playlistmaker.data.network.ItunesApiService
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.adapters.HistoryTrackAdapter
import com.practicum.playlistmaker.ui.adapters.TrackAdapter
import com.practicum.playlistmaker.ui.player.AudioPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class SearchActivity : AppCompatActivity() {

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val INPUT_TEXT_DEFAULT = ""
        const val PLACEHOLDER_CODE_NO_INTERNET = 0
        const val PLACEHOLDER_CODE_NOT_VISIBLE = -1
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private var isClickAllowed = true
    private val handlerMain = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { requestToTracks(inputText) }
    private var inputText = INPUT_TEXT_DEFAULT
    private val listTracks = mutableListOf<Track>()
    private val trackAdapter by lazy { TrackAdapter<Any>(listTracks) }
    private val historyTrackAdapter by lazy { HistoryTrackAdapter(SearchHistory.historyList) }
    private lateinit var searchHistory: SearchHistory
    private lateinit var itunesApiService: ItunesApiService
    private val sharedPreferencesSearchHistory by lazy {
        getSharedPreferences(
            PREFERENCES_HISTORY,
            MODE_PRIVATE
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        searchHistory = SearchHistory(sharedPreferencesSearchHistory)
        searchHistory.loadHistoryList()

        if (SearchHistory.historyList.isNotEmpty() && binding.inputSearch.hasFocus()) {
            historyShow()
        }

        val retrofit = Retrofit.Builder().baseUrl(getString(R.string.base_url_itunes))
            .addConverterFactory(GsonConverterFactory.create()).build()
        itunesApiService = retrofit.create<ItunesApiService>()

        binding.buttonBackSearch.setOnClickListener {
            finish()
        }

        binding.iconClearInput.setOnClickListener {
            with(binding) {
                inputSearch.setText(INPUT_TEXT_DEFAULT)
                inputSearch.clearFocus()
                showPlaceHolder(PLACEHOLDER_CODE_NOT_VISIBLE)
            }
            listTracks.clear()
            trackAdapter.notifyDataSetChanged()
            hideKeyboard()
        }

        val inputTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.iconClearInput.visibility = clearInputButtonVisibility(s)
                inputText = s.toString()
                if (s.isNullOrEmpty() && binding.inputSearch.hasFocus() && SearchHistory.historyList.isNotEmpty()) {
                    showPlaceHolder(PLACEHOLDER_CODE_NOT_VISIBLE)
                    historyShow()
                } else {
                    binding.historyListView.isVisible = false
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        with(binding) {
            inputSearch.addTextChangedListener(inputTextWatcher)

            inputSearch.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && SearchHistory.historyList.isNotEmpty()) {
                    historyShow()
                } else historyListView.isVisible = false
            }
        }

        trackAdapter.onItemClickListener = {
            searchHistory.saveTrackToHistory(it)
            historyTrackAdapter.notifyItemInserted(0)
            historyTrackAdapter.notifyItemRangeChanged(0, SearchHistory.historyList.size)
            if (clickDebounce()) startAudioPlayer(it)
        }

        historyTrackAdapter.onItemClickListener = {
            if (clickDebounce()) startAudioPlayer(it)
        }

        binding.btnClearHistory.setOnClickListener {
            searchHistory.clearHistory()
            historyTrackAdapter.notifyDataSetChanged()
            binding.historyListView.isVisible = false
        }

    }

    private fun startAudioPlayer(track: Track) {
        startActivity(Intent(this, AudioPlayer::class.java).apply {
            putExtra(
                DATA_FROM_AUDIO_PLAYER_KEY, Gson().toJson(track)
            )
        })
    }

    private fun historyShow() {
        with(binding) {
            recyclerSearchTrack.isVisible = false
            recyclerHistoryList.adapter = historyTrackAdapter
            historyListView.isVisible = true
        }
    }

    private fun clearInputButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun requestToTracks(searchText: String) {
        if (searchText.isNotEmpty()) {
            with(binding) {
                recyclerSearchTrack.isVisible = false
                historyListView.isVisible = false
                progressBar.isVisible = true
            }
            itunesApiService.getTrack(searchText).enqueue(object : Callback<ItunesResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<ItunesResponse>,
                    response: Response<ItunesResponse>
                ) {
                    listTracks.clear()
                    binding.progressBar.isVisible = false
                    binding.recyclerSearchTrack.isVisible = true
                    showPlaceHolder(PLACEHOLDER_CODE_NOT_VISIBLE)
                    if (response.isSuccessful) {
                        if (response.body()?.tracks?.isNotEmpty() == true) {
                            listTracks.addAll(response.body()?.tracks!!)
                            trackAdapter.notifyDataSetChanged()
                            binding.recyclerSearchTrack.adapter = trackAdapter
                        } else {
                            showPlaceHolder(response.code())
                        }
                    }
                }


                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    binding.progressBar.isVisible = false
                    showPlaceHolder(PLACEHOLDER_CODE_NO_INTERNET)
                    t.printStackTrace()
                }

            })
        }
    }

    private fun showPlaceHolder(code: Int) {
        when (code) {
            in 200..300 -> {
                with(binding) {
                    recyclerSearchTrack.isVisible = false
                    historyListView.isVisible = false
                    buttonUpdate.isVisible = false
                    textError.text = getString(R.string.nothing_found_txt)
                    placeholderError.isVisible = true
                }
            }

            -1 -> binding.placeholderError.isVisible = false
            else -> with(binding) {
                recyclerSearchTrack.isVisible = false
                historyListView.isVisible = false
                textError.text = getString(R.string.error_found_txt)
                buttonUpdate.isVisible = true
                placeholderError.isVisible = true
                buttonUpdate.setOnClickListener {
                    if (clickDebounce()) requestToTracks(inputText)
                }
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handlerMain.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handlerMain.removeCallbacks(searchRunnable)
        handlerMain.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

}