package com.practicum.playlistmaker


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
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
    }

    private var inputText = INPUT_TEXT_DEFAULT
    private val listTracks = mutableListOf<Track>()
    private val trackAdapter by lazy { TrackAdapter<Any>(listTracks) }
    private val historyTrackAdapter by lazy { HistoryTrackAdapter(SearchHistory.historyList) }
    private lateinit var searchHistory: SearchHistory
    private lateinit var itunesAPI: ItunesAPI
    private val sharedPreferencesSearchHistory by lazy {
        getSharedPreferences(
            PREFERENCES_HISTORY,
            MODE_PRIVATE
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchHistory = SearchHistory(sharedPreferencesSearchHistory)
        searchHistory.loadHistoryList()

        if (SearchHistory.historyList.isNotEmpty() && binding.inputSearch.hasFocus()) {
            historyShow(binding)
        }

        val retrofit = Retrofit.Builder().baseUrl(getString(R.string.base_url_itunes))
            .addConverterFactory(GsonConverterFactory.create()).build()
        itunesAPI = retrofit.create<ItunesAPI>()

        binding.buttonBackSearch.setOnClickListener {
            finish()
        }

        binding.iconClearInput.setOnClickListener {
            binding.apply {
                inputSearch.setText(INPUT_TEXT_DEFAULT)
                inputSearch.clearFocus()
                showPlaceHolder(PLACEHOLDER_CODE_NOT_VISIBLE, binding)
            }
            listTracks.clear()
            trackAdapter.notifyDataSetChanged()
            hideKeyboard(binding)
        }

        val inputTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.iconClearInput.visibility = clearInputButtonVisibility(s)
                inputText = s.toString()

                if (s.isNullOrEmpty() && binding.inputSearch.hasFocus() && SearchHistory.historyList.isNotEmpty()) {
                    historyShow(binding)
                    showPlaceHolder(PLACEHOLDER_CODE_NOT_VISIBLE, binding)
                } else binding.historyListView.isVisible = false

            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.apply {
            inputSearch.addTextChangedListener(inputTextWatcher)
            inputSearch.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && SearchHistory.historyList.isNotEmpty()) historyShow(binding)
                else historyListView.isVisible = false
            }
        }


        binding.inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requestToTracks(inputText, binding)
                hideKeyboard(binding)
            }

            false
        }

        trackAdapter.onItemClickListener = {
            searchHistory.saveTrackToHistory(it)
        }

        binding.btnClearHistory.setOnClickListener {
            searchHistory.clearHistory()
            historyTrackAdapter.notifyDataSetChanged()
            binding.historyListView.isVisible = false
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun historyShow(binding: ActivitySearchBinding) {
        binding.apply {
            historyTrackAdapter.notifyDataSetChanged()
            recyclerSearchTrack.isVisible = false
            recyclerHistoryList.adapter = historyTrackAdapter
            historyListView.isVisible = true
        }
    }

    private fun clearInputButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun requestToTracks(searchText: String, binding: ActivitySearchBinding) {
        itunesAPI.getTrack(searchText).enqueue(object : Callback<ItunesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
                listTracks.clear()
                binding.recyclerSearchTrack.isVisible = true
                showPlaceHolder(PLACEHOLDER_CODE_NOT_VISIBLE, binding)
                if (response.isSuccessful) {
                    if (response.body()?.tracks?.isNotEmpty() == true) {
                        listTracks.addAll(response.body()?.tracks!!)
                        trackAdapter.notifyDataSetChanged()
                        binding.recyclerSearchTrack.adapter = trackAdapter
                    } else {
                        showPlaceHolder(response.code(), binding)
                    }
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                showPlaceHolder(PLACEHOLDER_CODE_NO_INTERNET, binding)
            }

        })
    }

    private fun showPlaceHolder(code: Int, binding: ActivitySearchBinding) {
        when (code) {
            in 200..300 -> {
                binding.apply {
                    recyclerSearchTrack.isVisible = false
                    historyListView.isVisible = false
                    buttonUpdate.isVisible = false
                    textError.text = getString(R.string.nothing_found_txt)
                    placeholderError.isVisible = true
                }
            }

            -1 -> binding.placeholderError.isVisible = false
            else -> binding.apply {
                recyclerSearchTrack.isVisible = false
                historyListView.isVisible = false
                textError.text = getString(R.string.error_found_txt)
                buttonUpdate.isVisible = true
                placeholderError.isVisible = true
                buttonUpdate.setOnClickListener {
                    requestToTracks(inputText, binding)
                }
            }
        }
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

    private fun hideKeyboard(binding: ActivitySearchBinding) {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.inputSearch.windowToken, 0)
    }

}